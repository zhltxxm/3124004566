import java.sql.*;

public class UserDAO {
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setRole(rs.getString("role"));
                return user;
            }
            return null;
        }
    }
//    public boolean register(User user) throws SQLException {
//        String sql = "INSERT INTO users(username, password, role) VALUES(?,?,?)";
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, user.getUsername());
//            ps.setString(2, user.getPassword());
//            ps.setString(3, user.getRole());
//            return ps.executeUpdate() > 0;
//        }
//    }
    public boolean isUsernameExist(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username=?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // 完整注册方法
    public boolean register(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, role) VALUES(?,?,?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // 获取自增ID
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public boolean uodatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE username=?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }
}

