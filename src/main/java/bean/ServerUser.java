package bean;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerUser {
    /**
     * define username as userID
     */
    private String userName;
    /**
     * user' status outLine, inLine
     */
    private String status;
    /**
     * user's message queue
     */
    public Queue<String> session;

    public String password;

    public int id;

    private String firstName;
    private String surname;
    private String gender;
    private String birthDay;
    private String secQuestion;
    private String answerSecQuestion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getSurname() {
        return surname;
    }
    public String getBirthDay() {
        return birthDay;
    }
    public String getGender() {
        return gender;
    }
    public String getSecQuestion() {
        return secQuestion;
    }
    public String getAnswerSecQuestion() {
        return answerSecQuestion;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ServerUser(int id,String username, String firstName, String surname, String birthDay, String gender, String secQuestion, String answerSecQuestion, String password) {
        super();
        this.userName = username;
        this.id = id;
        this.password = password;
        this.firstName = firstName;
        this.surname = surname;
        this.birthDay = birthDay;
        this.gender = gender;
        this.secQuestion = secQuestion;
        this.answerSecQuestion = answerSecQuestion;
        //Ensure thread concurrent security
        session = new ConcurrentLinkedQueue();
    }

    public ServerUser() {
        super();
        new ServerUser(0, null,null,null,null,null,null,null,null);
    }

    public void addMsg(String message) {
        session.offer(message);
    }

    public String getMsg() {
        if (session.isEmpty())
            return null;
        return session.poll();
    }

}
