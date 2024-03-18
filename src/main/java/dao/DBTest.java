package dao;

import bean.ServerUser;

import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args){
        ServerUser p = new ServerUser(1,"dbtest@gmail.com","firstN", "lastN", "23/4/2002", "Male", "SecQues1?", "Unknown", "student");
        UserDaoImpl peronDao = UserDaoImpl.getInstance();
        try {
            peronDao.add(p);
            System.out.println("success");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
