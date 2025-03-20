import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    // 添加学生信息
    public boolean addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students(user_id, name, phone) VALUES(?,?,?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, student.getUserId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getPhone());
            return ps.executeUpdate() > 0;
        }
    }

    // 查询所有学生
    public List<Student> getAllStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setUserId(rs.getInt("user_id"));
                s.setName(rs.getString("name"));
                s.setPhone(rs.getString("phone"));
                list.add(s);
            }
        }
        return list;
    }

    // 更新手机号
    public boolean updatePhone(int studentId, String newPhone) throws SQLException {
        String sql = "UPDATE students SET phone=? WHERE id=?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPhone);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }
}
