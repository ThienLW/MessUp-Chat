package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DbUtils {

    // Địa chỉ kết nối cơ sở dữ liệu
    public static String URL;
    // Tên người dùng
    public static String USERNAME;
    // Mật khẩu
    public static String PASSWORD;
    // Lớp điều khiển của MySQL
    public static String DRIVER;
    // Lấy nội dung thông tin cấu hình
    private static Properties properties;

    private DbUtils() {
    }

    // Sử dụng khối tĩnh để tải lớp điều khiển
    static {
        try {
            InputStream input = Class.forName(DbUtils.class.getName()).getResourceAsStream("/db-config.properties");
            properties = new Properties();
            properties.load(input);
            URL = properties.getProperty("jdbc.url");
            USERNAME = properties.getProperty("jdbc.username");
            PASSWORD = properties.getProperty("jdbc.password");
            DRIVER = properties.getProperty("jdbc.driver");
            Class.forName(DRIVER);
            System.out.println(URL);
            System.out.println("DBUTILs success!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("DBUTILs error!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Định nghĩa một phương thức để lấy kết nối cơ sở dữ liệu
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Can't connect");
        }
        return conn;
    }

    /**
     * Đóng kết nối cơ sở dữ liệu
     * @param rs
     * @param stat
     * @param conn
     */
    public static void close(ResultSet rs, Statement stat, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stat != null) stat.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
