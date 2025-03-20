import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// ====================
public class CourseDAO {
    // 学生选课
    public boolean selectCourse(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO student_courses(student_id, course_id) VALUES(?,?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    // 学生退课
    public boolean dropCourse(int studentId, int courseId) throws SQLException {
        String sql = "DELETE FROM student_courses WHERE student_id=? AND course_id=?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    // 查询学生已选课程
    public List<Course> getSelectedCourses(int studentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.* FROM courses c " +
                "JOIN student_courses sc ON c.id = sc.course_id " +
                "WHERE sc.student_id = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setCredit(rs.getInt("credit"));
                list.add(c);
            }
        }
        return list;
    }
    //获取学生全部课程
    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses  ";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course s = new Course();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setCredit(Integer.parseInt(rs.getString("credit")));
                list.add(s);
            }
        }
        return list;
    }

    public boolean addCourse(String newCourse, int newCredit) throws SQLException {
        String sql = "INSERT INTO courses(name, credit) VALUES(?,?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newCourse);
            ps.setInt(2, newCredit);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(String needDeleteCourse) throws SQLException {
        String sql = "DELETE FROM courses WHERE name = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, needDeleteCourse);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Student> getStudentsByCourse(int courseId) throws SQLException {
        List<Student> students = new ArrayList<>();

        // SQL查询：通过选课表关联学生表
        String sql = "SELECT s.id, s.name, s.phone " +
                "FROM students s " +
                "JOIN student_courses sc ON s.id = sc.student_id " +
                "WHERE sc.course_id = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setPhone(rs.getString("phone"));
                students.add(student);
            }
        }
        return students;
    }
}
//        public List<Course> getSelectedCourses(int studentId) throws SQLException {
//            List<Course> list = new ArrayList<>();
//            String sql = "SELECT c.* FROM courses c " +
//                    "JOIN student_courses sc ON c.id = sc.course_id " +
//                    "WHERE sc.student_id = ?";
//
//            try (Connection conn = JDBC.getConnection();
//                 PreparedStatement ps = conn.prepareStatement(sql)) {
//
//                ps.setInt(1, studentId);
//                ResultSet rs = ps.executeQuery();
//                while (rs.next()) {
//                    Course c = new Course();
//                    c.setId(rs.getInt("id"));
//                    c.setName(rs.getString("name"));
//                    c.setCredit(rs.getInt("credit"));
//                    list.add(c);
//                }
//            }
//            return list;
//        }