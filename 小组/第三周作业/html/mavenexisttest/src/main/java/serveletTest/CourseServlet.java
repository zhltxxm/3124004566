package serveletTest;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import serveletTest.DBUtil;

@WebServlet("/api/courses")
public class CourseServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String keyword = request.getParameter("keyword");

        try {
            List<Course> courses = searchCourses(keyword);
            response.getWriter().print(gson.toJson(courses));
        } catch (SQLException e) {
            response.getWriter().print(gson.toJson(new ArrayList<>()));
        }
    }

    private List<Course> searchCourses(String keyword) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE name LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setName(rs.getString("name"));
                course.setCredit(rs.getInt("credit"));
                courses.add(course);
            }
        }
        return courses;
    }

    private static class Course {
        private int id;
        private String name;
        private int credit;

        // getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getCredit() { return credit; }
        public void setCredit(int credit) { this.credit = credit; }
    }
}