package dao;

import bean.ServerUser;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    // Phương thức thêm
    public void add(ServerUser p) throws SQLException;

    // Phương thức cập nhật
    public void update(ServerUser p) throws SQLException;

    // Phương thức xóa
    public void delete(int id) throws SQLException;

    // Phương thức tìm theo ID
    public ServerUser findById(int id) throws SQLException;

    // Phương thức tìm theo tên
    public ServerUser findByName(String username) throws SQLException;

    // Phương thức tìm tất cả
    public List<ServerUser> findAll() throws SQLException;
}
