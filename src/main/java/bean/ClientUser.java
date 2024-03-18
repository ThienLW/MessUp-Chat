package bean;

import java.io.Serializable;

public class ClientUser implements Serializable {
    /**
     * define username as userID
     */
    private String userName;
    /**
     * user' status outLine, inLine
     */
    private String status;

    private boolean notify;

    private String firstName;
    private String surname;
    private String gender;
    private String birthDay;
    private String secQuestion;
    private String answerSecQuestion;

    public String getFirstName() {
        return firstName;
    }
    public String getSurname() {
        return surname;
    }
    public String getSecQuestion() {
        return secQuestion;
    }
    public String getAnswerSecQuestion() {
        return answerSecQuestion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
