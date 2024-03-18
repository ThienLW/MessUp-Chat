package server;

import bean.ServerUser;
import com.google.gson.Gson;
import dao.UserDaoImpl;
import org.mindrot.jbcrypt.BCrypt;
import utils.EmailSender;
import utils.GsonUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static utils.Constants.*;

public class WorkerServer extends Thread {

    private ServerUser workUser; //the user is connected

    private Socket socket;

    private ArrayList<ServerUser> users;

    private BufferedReader reader;

    private PrintWriter writer;

    private boolean isLogOut = false;

    private String errorName = null;

    private long currentTime = 0;

    private Gson gson;

    public WorkerServer(Socket socket, ArrayList users) {
        super();
        gson = new Gson();
        this.socket = socket; //bind socket
        this.users = users;   //get the common user resource
    }

    @Override
    public void run() {
        //todo server's work
        try {
            currentTime = new Date().getTime();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            String readLine;
            while (true) {
                //heart check
                long newTime = new Date().getTime();
                if (newTime - currentTime > 2000) {
                    logOut();
                } else {
                    currentTime = newTime;
                }
                readLine = reader.readLine();
                if (readLine == null)
                    logOut();
                handleMessage(readLine);
                sentMessageToClient();
                if (isLogOut) {
                    // kill the I/O stream
                    reader.close();
                    writer.close();
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            logOut();
        } catch (IOException e) {
            e.printStackTrace();
            logOut();
        }
    }


    /**
     * the message to deal with Client's command
     *
     * @param readLine
     */
    private void handleMessage(String readLine) {
        System.out.println("Xử lý tin nhắn: " + readLine);
        Map<Integer, Object> gsonMap = GsonUtils.GsonToMap(readLine);
        Integer command = GsonUtils.Double2Integer((Double) gsonMap.get(COMMAND));
        HashMap map = new HashMap();
        String username, password, status;
        switch (command) {
            case COM_GROUP:
                writer.println(getGroup());
                System.out.println(workUser.getUserName() + " yêu cầu lấy chi tiết người dùng trực tuyến");
                break;
            case COM_SIGNUP:
                username = (String) gsonMap.get(USERNAME);
                password = (String) gsonMap.get(PASSWORD);

                String firstName = new String (gsonMap.get(FIRSTNAME).toString());
                String surname = new String (gsonMap.get(SURNAME).toString());
                String birthDay = new String (gsonMap.get(BIRTHDAY).toString());
                String gender = new String (gsonMap.get(GENDER).toString());
                String secQuestion = new String (gsonMap.get(SECURITYQUESTION).toString());
                String answerSecQuestion = new String (gsonMap.get(SECURITYANSWER).toString());

                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                map.put(COMMAND, COM_RESULT);
                if (createUser(username, firstName, surname, birthDay, gender, secQuestion, answerSecQuestion, hashedPassword)) {
                    // Cần ngay lập tức cập nhật heart rate
                    currentTime = new Date().getTime();
                    // Lưu thông tin
                    map.put(COM_RESULT, SUCCESS);
                    map.put(COM_DESCRIPTION, "Thành công");
                    writer.println(gson.toJson(map));
                    broadcast(getGroup(), COM_SIGNUP);
                    System.out.println("Người dùng " + username + " đã đăng ký trực tuyến");

                    // Send confirmation email to user
                    // CompletableFuture hỗ trợ xử lý bất đồng bộ
                    CompletableFuture.runAsync(() -> {
                        String senderEmail = "messupcommunity@gmail.com";
                        String senderPassword = "pmfg fuof qiyh dbih";
                        EmailSender emailSender = new EmailSender(senderEmail, senderPassword);
                        emailSender.sendConfirmationEmail(username, firstName, surname);
                    }).thenAccept((result) -> {
                        // Xử lý kết quả sau khi gửi email thành công (nếu cần).
                        System.out.println("CompletableFuture Work Successful!");
                    }).exceptionally((e) -> {
                        // Xử lý lỗi khi gửi email thất bại (nếu cần).
                        System.out.println("CompletableFuture Error - Weak network connection or incorrect information!");
                        return null;
                    });
                } else {
                    map.put(COM_RESULT, FAILED);
                    map.put(COM_DESCRIPTION, errorName);
                    writer.println(gson.toJson(map)); // Trả lời tin nhắn cho máy chủ
                    System.out.println("Lỗi khi đăng ký người dùng "+ username + ":\n"+errorName);
                }
                break;
            case COM_LOGIN:
                username = (String) gsonMap.get(USERNAME);
                password = (String) gsonMap.get(PASSWORD);
                boolean find = false;
                for (ServerUser u : users) {
                    if (u.getUserName().equalsIgnoreCase(username)) {
                        String hashedPasswordFromDatabase = u.getPassword();
                        if (!BCrypt.checkpw(password, hashedPasswordFromDatabase)) {
                            map.put(COM_DESCRIPTION, "Incorrect password!");
                            break;
                        }
                        if (!u.getStatus().equals("offline")) {
                            map.put(COM_DESCRIPTION, "This user is logged in!");
                            break;
                        }
                        currentTime = new Date().getTime();
                        map.put(COM_RESULT, SUCCESS);
                        map.put(FIRSTNAME, u.getFirstName());
                        map.put(SURNAME, u.getSurname());
                        map.put(COM_DESCRIPTION, username + " đã đăng nhập thành công");
                        u.setStatus("online");
                        writer.println(gson.toJson(map));
                        workUser = u;
                        broadcast(getGroup(), COM_SIGNUP);
                        find = true;
                        System.out.println("Người dùng " + username + " đã đăng nhập trực tuyến");
                        break;
                    }
                }
                if (!find) {
                    map.put(COM_RESULT, FAILED);
                    if (!map.containsKey(COM_DESCRIPTION))
                        map.put(COM_DESCRIPTION, "Account does not exist!");
                    writer.println(gson.toJson(map)); // Trả lời tin nhắn cho máy chủ
                }
                break;
            case COM_CHATWITH:
                String receiver = (String) gsonMap.get(RECEIVER);
                map = new HashMap();
                map.put(COMMAND, COM_CHATWITH);
                map.put(SPEAKER, gsonMap.get(SPEAKER));
                map.put(RECEIVER, gsonMap.get(RECEIVER));
                map.put(CONTENT, gsonMap.get(CONTENT));
                map.put(TIME, getFormatDate());
                for (ServerUser u : users) {
                    if (u.getUserName().equals(receiver)) {
                        u.addMsg(gson.toJson(map));
                        break;
                    }
                }
                workUser.addMsg(gson.toJson(map));
                break;
            case COM_CHATALL:
                map = new HashMap();
                map.put(COMMAND, COM_CHATALL);
                map.put(SPEAKER, workUser.getUserName());
                map.put(FIRSTNAME, workUser.getFirstName());
                map.put(SURNAME, workUser.getSurname());
                map.put(TIME, getFormatDate());
                map.put(CONTENT, gsonMap.get(CONTENT));
                broadcast(gson.toJson(map), COM_MESSAGEALL);
                break;
//            case COM_CHANGESTATUS:
//                status = (String) gsonMap.get(STATUS);
//                for (ServerUser u : users) {
//                    if (u.getUserName().equals(workUser.getUserName())){
//                        u.setStatus(status);
//                        break;
//                    }
//                }
//                map = new HashMap();
//                map.put(COMMAND, COM_CHANGESTATUS);
//                map.put(USERNAME,workUser.getUserName());
//                map.put(STATUS, status);
//                writer.println(gson.toJson(map));
//                break;
            case COM_CHECK_SECURITY_INFO:
                map = new HashMap();
                username = (String) gsonMap.get(USERNAME);
                secQuestion = (String) gsonMap.get(SECURITYQUESTION);
                answerSecQuestion = (String) gsonMap.get(SECURITYANSWER);

                find = false;
                for (ServerUser u : users) {
                    if (u.getUserName().equalsIgnoreCase(username)) {
                        find = true;
                        if (u.getSecQuestion().equalsIgnoreCase(secQuestion)) {
                           if (u.getAnswerSecQuestion().equalsIgnoreCase(answerSecQuestion)) {
                               map.put(COM_RESULT, SUCCESS);
                               writer.println(gson.toJson(map));
                           }
                           else {
                               map.put(COM_RESULT, FAILED);
                               map.put(COM_DESCRIPTION, "The answer to the security question is incorrect!");
                               writer.println(gson.toJson(map));
                           }
                        }
                        else {
                            map.put(COM_RESULT, FAILED);
                            map.put(COM_DESCRIPTION, "Security question do not match!");
                            writer.println(gson.toJson(map));
                        }
                        break;
                    }
                }
                if (!find) {
                    map.put(COM_RESULT, FAILED);
                    if (!map.containsKey(COM_DESCRIPTION))
                        map.put(COM_DESCRIPTION, "Account does not exist!");
                    writer.println(gson.toJson(map)); // Trả lời tin nhắn cho máy chủ
                }
                break;
            case COM_CHANGE_PASSWD:
                map = new HashMap();
                username = (String) gsonMap.get(USERNAME);
                password = (String) gsonMap.get(PASSWORD);
                hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                if (changePasswd(username,hashedPassword)) {
                    // Lưu thông tin
                    map.put(COM_RESULT, SUCCESS);
                    writer.println(gson.toJson(map));
                } else {
                    map.put(COM_RESULT, FAILED);
                    map.put(COM_DESCRIPTION, "The system encountered an error, please try again later!");
                    writer.println(gson.toJson(map)); // Trả lời tin nhắn cho máy chủ
                }
                break;
//            case COM_LOGOUT:
//                logOut();
//                break;
            default:
                break;
        }
    }


    /**
     * @return current time the formatDate String
     */
    public String getFormatDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * broadcast the message to all user
     *
     * @param message the message
     * @param type    that contain "message", "logOUt", "signUp"
     */
    private void broadcast(String message, int type) {
        System.out.println(workUser.getUserName() + " BROADCAST " + message);

        switch (type) {
            case COM_MESSAGEALL:
                for (ServerUser u : users) {
                    u.addMsg(message);
                }
                break;
            case COM_LOGOUT:
            case COM_SIGNUP:
                for (ServerUser u : users) {
                    if (!u.getUserName().equals(workUser.getUserName())) {
                        u.addMsg(message);
                    }
                }
                break;
        }

    }


    /**
     * send the message to Client
     */
    private void sentMessageToClient() {
        String message;
        if (workUser != null)
            while ((message = workUser.getMsg()) != null) {
                writer.println(message); // Gửi thông điệp.
                System.out.println(workUser.getUserName() + " gửi message: " + message + " với kích thước " + workUser.session.size());
            }
    }

    /**
     * the  method to release socket's resource.
     */
    private void logOut() {
        if (workUser == null)
            return;
        System.out.println("Người dùng " + workUser.getUserName() + " đã ngoại tuyến");
        // vẫn giữ người dùng này và thay đổi trạng thái của nó
        workUser.setStatus("offline");
        for (ServerUser u : users) {
            if (u.getUserName().equals(workUser.getUserName()))
                u.setStatus("offline");
        }
        broadcast(getGroup(), COM_LOGOUT);
        isLogOut = true;
    }

    /**
     * get a random name
     *
     * @return
     */
    private String getRandomName() {
        String[] str1 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "0", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        StringBuilder name = new StringBuilder();
        String userName = name.toString();
        Random ran = new Random();
        boolean success = false;
        do {
            for (int i = 0; i < 6; i++) {
                int n = ran.nextInt(str1.length);
                String str = str1[n];
                name.append(str);
                System.out.println(name);
            }
            success = true;
            userName = name.toString();
            for (ServerUser user : users) {
                if (userName.equals(user.getUserName())) {
                    success = false;
                    break;
                }
            }
        } while (!success);
        return userName;
    }

    /**
     * create username and bind userName. if failed it will return failed.
     * if success it will add to users.
     *
     * @param username
     * @return
     */
    private boolean createUser(String username, String firstName, String surname, String birthDay, String gender, String secQuestion, String answerSecQuestion, String password) {
        for (ServerUser user : users) {
            if (user.getUserName().equalsIgnoreCase(username)) {
                errorName = "This user is already registered!";
                return false;
            }
        }
        //add user to userList
        ServerUser newUser = new ServerUser(users.size()+1, username, firstName, surname, birthDay, gender, secQuestion, answerSecQuestion, password);
        newUser.setStatus("online");
        users.add(newUser);
        //  add user to DB
        try {
            UserDaoImpl.getInstance().add(newUser);
        } catch (SQLException e) {
            e.printStackTrace();
            errorName = "Error adding user to database!";
            return false;
        }
        workUser = newUser;
        return true;
    }
    private boolean changePasswd(String username, String newPassword) {
        ServerUser userToUpdate = null;
        for (ServerUser user : users) {
            if (user.getUserName().equalsIgnoreCase(username)) {
                userToUpdate = user;
                break;
            }
        }
        // Cập nhật mật khẩu cho người dùng
        userToUpdate.setPassword(newPassword);

        // Cập nhật mật khẩu trong cơ sở dữ liệu
        UserDaoImpl.getInstance().changePasswdDB(userToUpdate.getUserName(), newPassword);
        return true;
    }

    /**
     * return the json of group
     *
     * @return
     */
    private String getGroup() {
        String[] userList = new String[users.size() * 4];
        int j = 0;
        for (int i = 0; i < users.size(); i++, j++) {
            userList[j] = users.get(i).getUserName();
            userList[++j] = users.get(i).getStatus();
            userList[++j] = users.get(i).getFirstName(); // FirstName
            userList[++j] = users.get(i).getSurname();   // Surname
        }
        HashMap map = new HashMap();
        map.put(COMMAND, COM_GROUP);
        map.put(COM_GROUP, userList);
        return gson.toJson(map);
    }

}
