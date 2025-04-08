package serveletTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/test_database?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "Zhl060903@";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL驱动加载失败");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


}