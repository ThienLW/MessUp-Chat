package com.example.project_caroz.model;

import bean.ClientUser;
import bean.Message;
import com.example.project_caroz.MainChatViewController;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.GsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.*;

import static utils.Constants.*;

public class ClientModel {

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket client;
    private final int port = 23402;
    private String IP = "localhost";
    private boolean isConnect = false;                               //connected?
    private boolean chatChange = false;
    private String chatUser = "Global Chat";
    private String thisUser;
    private String thisUserFirstName;
    private String thisUserSurname;
    private Gson gson;

    private LinkedHashMap<String, ArrayList<Message>> userSession;   //Được sử dụng để lưu trữ hàng đợi tin nhắn của người dùng
    private Thread keepalive = new Thread(new KeepAliveWatchDog());
    private Thread keepreceive = new Thread(new ReceiveWatchDog());

    private ObservableList<ClientUser> userList;
    private ObservableList<Message> chatRecorder;

    private ClientModel() {
        super();
        gson = new Gson();
        ClientUser user = new ClientUser();
        user.setUserName("Global Chat");
        user.setFirstName("");
        user.setSurname("");
        user.setStatus("");
        userSession = new LinkedHashMap<>();
        userSession.put("Global Chat", new ArrayList<>());
        userList = FXCollections.observableArrayList();
        chatRecorder = FXCollections.observableArrayList();
        userList.add(user);
    }

    private static ClientModel instance;

    public static ClientModel getInstance() {
        if (instance == null) {
            synchronized (ClientModel.class) {
                if (instance == null) {
                    instance = new ClientModel();
                }
            }
        }
        return instance;
    }


    public void setChatUser(String chatUser) {
        if (!this.chatUser.equals(chatUser))
            chatChange = true;
        this.chatUser = chatUser;
        // Xoá trạng thái tin nhắn chưa đọc
        for (int i = 0; i < userList.size(); i++) {
            ClientUser user = userList.get(i);
            if (user.getUserName().equals(chatUser)) {
                if (user.isNotify()) {
                    System.out.println("Chuyển đổi thư mục tin nhắn");
//                    user.setStatus(user.getStatus().substring(0, user.getStatus().length() - 3));
                    userList.remove(i);
                    userList.add(i, user);
                    user.setNotify(false);
                }
                chatRecorder.clear();
                chatRecorder.addAll(userSession.get(user.getUserName()));
                break;
            }
        }
    }

    public ObservableList<Message> getChatRecorder() {
        return chatRecorder;
    }

    public ObservableList<ClientUser> getUserList() {
        return userList;
    }

    public String getThisUser() {
        return thisUser;
    }
    public String getThisUserFirstName() {
        return thisUserFirstName;
    }
    public String getThisUserSurname() {
        return thisUserSurname;
    }

    class KeepAliveWatchDog implements Runnable {
        @Override
        public void run() {
            HashMap<Integer, Integer> map = new HashMap<>();
            map.put(COMMAND, COM_KEEP);
            try {
                System.out.println("keep alive start" + Thread.currentThread());
                //heartbeat detection
                while (isConnect) {
                    Thread.sleep(500);
//                    System.out.println("500ms keep");
                    writer.println(gson.toJson(map));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveWatchDog implements Runnable {
        @Override
        public void run() {
            try {
                System.out.println(" Receive start" + Thread.currentThread());
                String message;
                while (isConnect) {
                    message = reader.readLine();
                    // System.out.println("Đọc thông tin máy chủ: " + message);
                    handleMessage(message);
                }
            } catch (IOException e) {

            }
        }
    }

    /**
     * disconnect
     */
//    public void disConnect() {
//        isConnect = false;
//        keepalive.stop();
//        keepreceive.stop();
//        if (writer != null) {
//            writer.close();
//            writer = null;
//        }
//        if (client != null) {
//            try {
//                client.close();
//                client = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    public void disConnect() {
        isConnect = false;

        // Gọi các phương thức để đóng các tiến trình hoặc tài nguyên
        stopKeepAlive();
        stopKeepReceive();

        if (writer != null) {
            try {
                writer.close();
            } finally {
                writer = null;
            }
        }

        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                client = null;
            }
        }
    }

    // Phương thức dùng để dừng keepalive
    private void stopKeepAlive() {
        if (keepalive != null) {
            keepalive.interrupt(); // Sử dụng interrupt() để dừng tiến trình
        }
    }

    // Phương thức dùng để dừng keepreceive
    private void stopKeepReceive() {
        if (keepreceive != null) {
            keepreceive.interrupt(); // Sử dụng interrupt() để dừng tiến trình
        }
    }


    private void handleMessage(String message) {
        Map<Integer, Object> gsonMap = GsonUtils.GsonToMap(message);
        Integer command = GsonUtils.Double2Integer((Double) gsonMap.get(COMMAND));
        Message m;
        switch (command) {
            case COM_GROUP:
                HashSet<String> recoder = new HashSet<>();
                for (ClientUser u : userList) {
                    if (u.isNotify()) {
                        recoder.add(u.getUserName());
                    }
                }
                ArrayList<String> userData = (ArrayList<String>) gsonMap.get(COM_GROUP);
                userList.remove(1, userList.size());
                int onlineUserNum = 0;
                for (int i = 0; i < userData.size(); i++) {
                    ClientUser user = new ClientUser();
                    user.setUserName(userData.get(i));
                    user.setStatus(userData.get(++i));
                    user.setFirstName(userData.get(++i));
                    user.setSurname(userData.get(++i));
                    if (user.getStatus().equals("online"))
                        onlineUserNum++;
                    if (recoder.contains(user.getUserName())) {
                        user.setNotify(true);
                        user.setStatus(user.getStatus() + "(*)");
                    }
                    userList.add(user);
                    userSession.put(user.getUserName(), new ArrayList<>());
                }
                int finalOnlineUserNum = onlineUserNum;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        MainChatViewController.getInstance().getLabUserCounter().setText("" + finalOnlineUserNum);
                    }
                });
                break;
            case COM_CHATALL:
                m = new Message();
                m.setTimer((String) gsonMap.get(TIME));
                m.setSpeaker((String) gsonMap.get(SPEAKER));
                m.setSpeakerFirstname((String) gsonMap.get(FIRSTNAME));
                m.setSpeakerSurname((String) gsonMap.get(SURNAME));
                m.setContent((String) gsonMap.get(CONTENT));
                if (chatUser.equals("Global Chat")) {
                    chatRecorder.add(m);
                }
                userSession.get("Global Chat").add(m);
                break;
            case COM_CHATWITH:
                String speaker = (String) gsonMap.get(SPEAKER);
                String receiver = (String) gsonMap.get(RECEIVER);
                String time = (String) gsonMap.get(TIME);
                String content = (String) gsonMap.get(CONTENT);
                m = new Message();
                m.setSpeaker(speaker);
                m.setContent(content);
                m.setTimer(time);
                if (thisUser.equals(receiver)) {
                    if (!chatUser.equals(speaker)) {
                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).getUserName().equals(speaker)) {
                                ClientUser user = userList.get(i);
                                if (!user.isNotify()) {
                                    //user.setStatus(userList.get(i).getStatus() + "(*)");
                                    user.setNotify(true);
                                }
                                userList.remove(i);
                                userList.add(i, user);
                                break;
                            }
                        }
                        System.out.println("Đánh dấu tin nhắn là chưa đọc");
                    }else{
                        chatRecorder.add(m);
                    }
                    userSession.get(speaker).add(m);
                }else{
                    if(chatUser.equals(receiver))
                        chatRecorder.add(m);
                    userSession.get(receiver).add(m);
                }
                break;
//            case COM_CHANGESTATUS:
//                String username = (String) gsonMap.get(USERNAME);
//                String status = (String) gsonMap.get(STATUS);
//
//                for (int i = 0; i < userList.size(); i++) {
//                    if (userList.get(i).getUserName().equals(username)) {
//                        ClientUser user = userList.get(i);
//                        user.setStatus(status);
//                        break;
//                    }
//                }
//                break;
            default:
                break;
        }
        System.out.println("Thông báo từ máy chủ: " + message + " - Kết thúc tin nhắn");
    }


    /**
     * Phương thức này không còn hiệu lực
     *
     * @param chatUser
     * @return
     */
    private LinkedList<Message> loadChatRecorder(String chatUser) {
        LinkedList<Message> messagesList = new LinkedList<>();
        if (userSession.containsKey(chatUser)) {
            ArrayList<Message> recorder = userSession.get(chatUser);
            for (Message s : recorder) {
                messagesList.add(s);
            }
        }
        return messagesList;
    }

    /**
     * Gửi chuỗi JSON đến máy chủ
     *
     * @param message phải là chuỗi JSON
     */
    public void sentMessage(String message) {
        writer.println(message);
    }


    /**
     * @param username
     * @param IP
     * @param buf
     * @return
     */
    public boolean CheckLogin(String username, String IP, String password, StringBuffer buf) {
        this.IP = IP; //bind server IP
        Map<Integer, Object> map;
        try {
            // Xử lý đối với nhiều lần cố gắng đăng nhập
            if (client == null || client.isClosed()) {
                client = new Socket(IP, port);
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream(), true);
            }
            map = new HashMap<>();
            map.put(COMMAND, COM_LOGIN);
            map.put(USERNAME, username);
            map.put(PASSWORD, password);
            writer.println(gson.toJson(map));
            String strLine = reader.readLine(); // Biến readline đang bị chặn bởi luồng
            System.out.println(strLine);
            map = GsonUtils.GsonToMap(strLine);
            Integer result = GsonUtils.Double2Integer((Double) map.get(COM_RESULT));
            if (result == SUCCESS) {
                thisUserFirstName = (String) map.get(FIRSTNAME);
                thisUserSurname = (String) map.get(SURNAME);
                isConnect = true;
                //request group
                map.clear();
                map.put(COMMAND, COM_GROUP);
                writer.println(gson.toJson(map));
                thisUser = username;
                keepalive.start();
                keepreceive.start();
                return true;
            } else {
                String description = (String) map.get(COM_DESCRIPTION);
                buf.append(description);
                return false;
            }
        } catch (ConnectException e) {
            buf.append(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            buf.append(e.toString());
        }
        return false;
    }

    public boolean CheckRegister(String username, String password, String firstName, String surname, String birthDay, String gender, String secQuestion, String answerSecQuestion, String IP, StringBuffer buf) {
        this.IP = IP; //bind server IP
        Map<Integer, Object> map;
        try {
            // Xử lý đối với nhiều lần cố gắng đăng nhập
            if (client == null || client.isClosed()) {
                client = new Socket(IP, port);
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream(), true);
            }
            map = new HashMap<>();
            map.put(COMMAND, COM_SIGNUP);
            map.put(USERNAME, username);
            map.put(PASSWORD, password);
            map.put(FIRSTNAME, firstName);
            map.put(SURNAME, surname);
            map.put(BIRTHDAY, birthDay);
            map.put(GENDER, gender);
            map.put(SECURITYQUESTION, secQuestion);
            map.put(SECURITYANSWER, answerSecQuestion);

            writer.println(gson.toJson(map));
            String strLine = reader.readLine(); // Biến readline đang bị chặn bởi luồng
            System.out.println(strLine);
            map = GsonUtils.GsonToMap(strLine);
            Integer result = GsonUtils.Double2Integer((Double) map.get(COM_RESULT));
            if (result == SUCCESS) {
                isConnect = true;
                //request group
                map.clear();
                map.put(COMMAND, COM_GROUP);
                writer.println(gson.toJson(map));
                thisUser = username;
                thisUserFirstName = firstName;
                thisUserSurname = surname;
                keepalive.start();
                keepreceive.start();
                return true;
            } else {
                String description = (String) map.get(COM_DESCRIPTION);
                buf.append(description);
                return false;
            }
        } catch (ConnectException e) {
            buf.append(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            buf.append(e.toString());
        }
        return false;
    }

    public boolean checkSecurityInfor(String username, String secQuestion, String answer, StringBuffer buf) {
        Map<Integer, Object> map = new HashMap<>();
        try {
            // Xử lý đối với nhiều lần cố gắng đăng nhập
            if (client == null || client.isClosed()) {
                client = new Socket(IP, port);
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream(), true);
            }
            map.put(COMMAND, COM_CHECK_SECURITY_INFO);
            map.put(USERNAME, username);
            map.put(SECURITYQUESTION, secQuestion);
            map.put(SECURITYANSWER, answer);

            writer.println(gson.toJson(map));

            String strLine = reader.readLine();
            map = GsonUtils.GsonToMap(strLine);
            Integer result = GsonUtils.Double2Integer((Double) map.get(COM_RESULT));

            if (result == SUCCESS) {
                return true;
            } else {
                String description = (String) map.get(COM_DESCRIPTION);
                buf.append(description);
                return false;
            }
        } catch (ConnectException e) {
            buf.append(e);
        } catch (IOException e) {
            e.printStackTrace();
            buf.append(e);
        }
        return false;
    }

    public boolean changePasswd(String username, String passwd, StringBuffer buf){
        Map<Integer, Object> map = new HashMap<>();
        try {
            // Xử lý đối với nhiều lần cố gắng đăng nhập
            if (client == null || client.isClosed()) {
                client = new Socket(IP, port);
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer = new PrintWriter(client.getOutputStream(), true);
            }
            map.put(COMMAND, COM_CHANGE_PASSWD);
            map.put(USERNAME, username);
            map.put(PASSWORD, passwd);

            writer.println(gson.toJson(map));

            String strLine = reader.readLine();
            map = GsonUtils.GsonToMap(strLine);
            Integer result = GsonUtils.Double2Integer((Double) map.get(COM_RESULT));

            if (result == SUCCESS) {
                return true;
            } else {
                String description = (String) map.get(COM_DESCRIPTION);
                buf.append(description);
                return false;
            }
        } catch (ConnectException e) {
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}