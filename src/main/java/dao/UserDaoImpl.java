package dao;

import bean.ServerUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements of User DAO
 * @author Trtthien
 */
public class UserDaoImpl implements UserDao {

    private UserDaoImpl() {
        super();
    }
    private  static UserDaoImpl userDao;
    public static UserDaoImpl getInstance(){
        if(userDao==null){
            synchronized (UserDaoImpl.class){
                if(userDao==null){
                    userDao = new UserDaoImpl();
                }
            }
        }
        return userDao;
    }

    /**
     * Thực hiện phương thức thêm
     */
    @Override
    public void add(ServerUser p) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "insert into ChatUser(id,username,firstname,surname,birthday,gender,security_question,answer,password)values(?,?,?,?,?,?,?,?,?)";

        try{
            conn = DbUtils.getConnection();
            System.out.println("get connect");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getId());
            ps.setString(2, p.getUserName());
            ps.setString(3, p.getFirstName());
            ps.setString(4, p.getSurname());
            ps.setString(5, p.getBirthDay());
            ps.setString(6, p.getGender());
            ps.setString(7, p.getSecQuestion());
            ps.setString(8, p.getAnswerSecQuestion());
            ps.setString(9, p.getPassword());
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("add error");
            throw new SQLException("Thêm dữ liệu thất bại");
        }finally{
            DbUtils.close(null, ps, conn);
        }
    }

    /**
     * Phương thức cập nhật
     */
    @Override
    public void update(ServerUser p) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update ChatUser set name=?,age=?,password=? where id=?";

        try{
            conn = DbUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getId());
            ps.setString(2, p.getUserName());
            ps.setString(3, p.getPassword());
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Cập nhật dữ liệu thất bại");
        }finally{
            DbUtils.close(null, ps, conn);
        }
    }

    /**
     * Phương thức xóa
     */
    @Override
    public void delete(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "delete from ChatUser where id=?";
        try{
            conn = DbUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Xóa dữ liệu thất bại");
        }finally{
            DbUtils.close(null, ps, conn);
        }
    }

    /**
     * Tìm một đối tượng theo ID
     */
    @Override
    public ServerUser findById(int id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ServerUser p = null;
        String sql = "select name,password from ChatUser where id=?";
        try{
            conn = DbUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                p = new ServerUser();
                p.setId(id);
                p.setUserName(rs.getString(1));
                p.setPassword(rs.getString(2));
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Tìm theo ID thất bại");
        }finally{
            DbUtils.close(rs, ps, conn);
        }
        return p;
    }

    @Override
    public ServerUser findByName(String username) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ServerUser p = null;
        String sql = "select * from ChatUser where name=?";
        try{
            conn = DbUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1,username);
            rs = ps.executeQuery();
            if(rs.next()){
                p = new ServerUser(rs.getInt(1),username,rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9));
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Tìm theo tên thất bại");
        }finally{
            DbUtils.close(rs, ps, conn);
        }
        return p;
    }

    /**
     * Tìm tất cả dữ liệu
     */
    @Override
    public List<ServerUser> findAll() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ServerUser p = null;
        List<ServerUser> persons = new ArrayList<ServerUser>();
        String sql = "select * from ChatUser";
        try{
            conn = DbUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                p = new ServerUser(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9));
                persons.add(p);
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("Truy vấn tất cả dữ liệu thất bại");
        }finally{
            DbUtils.close(rs, ps, conn);
        }
        return persons;
    }

    public boolean changePasswdDB(String username, String newPasswd) {
        String sql = "UPDATE ChatUser SET password = ? WHERE username = ?";
        try (Connection connection = DbUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newPasswd);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("change password error");
            e.printStackTrace();
            return false;
        }
    }



}
