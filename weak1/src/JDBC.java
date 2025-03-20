// ========== 数据库工具类 ==========
import java.sql.*;

public class JDBC {
    private static final String URL = "jdbc:mysql://localhost:3306/school?useUnicode=true&characterEncoding=utf8&useSSL=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Zhl060903@";

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            //throw new SQLException("驱动加载失败");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 关闭资源
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
