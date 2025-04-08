package serveletTest;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import com.google.gson.Gson;
import serveletTest.DBUtil;

import java.util.logging.Logger;

/**
 * 用户认证Servlet - 处理登录和注册请求
 * 访问路径: /api/auth
 */
@WebServlet("/api/auth")
public class AuthServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AuthServlet.class.getName());
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 设置响应类型和编码
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 添加CORS头
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // 获取请求参数
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        logger.info("收到请求 - action: " + action + ", username: " + username);

        try {
            // 参数基础验证
            if (action == null || username == null || password == null) {
                sendResponse(response, false, "请求参数不完整");
                return;
            }

            // 根据action参数路由处理
            switch (action) {
                case "login":
                    handleLogin(username, password, response);
                    break;
                case "register":
                    handleRegistration(username, password, response);
                    break;
                default:
                    sendResponse(response, false, "无效的操作类型");
            }
        } catch (Exception e) {
            logger.severe("处理请求时发生错误: " + e.getMessage());
            e.printStackTrace();
            sendResponse(response, false, "服务器内部错误");
        }
    }

    /**
     * 处理用户登录
     */
    private void handleLogin(String username, String password, HttpServletResponse response)
            throws SQLException, IOException {
        // 输入验证
        if (username.length() < 4 || username.length() > 20) {
            sendResponse(response, false, "用户名长度需在4-20个字符之间");
            return;
        }

        logger.info("处理登录请求 - 用户名: " + username);

        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            logger.info("执行SQL: " + ps.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("用户验证成功 - 用户ID: " + rs.getInt("id"));
                    sendResponse(response, true, "登录成功");
                } else {
                    logger.warning("用户名或密码错误 - 用户名: " + username);
                    sendResponse(response, false, "用户名或密码错误");
                }
            }
        } catch (SQLException e) {
            logger.severe("数据库查询错误: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 处理用户注册
     */
    private void handleRegistration(String username, String password, HttpServletResponse response)
            throws SQLException, IOException {
        // 输入验证
        if (username.length() < 4 || username.length() > 20) {
            sendResponse(response, false, "用户名长度需在4-20个字符之间");
            return;
        }

        if (password.length() < 6) {
            sendResponse(response, false, "密码长度至少6个字符");
            return;
        }

        logger.info("处理注册请求 - 用户名: " + username);

        // 获取数据库连接（设置为手动提交事务）
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // 开始事务

            try {
                // 1. 检查用户名是否存在
                if (isUsernameExists(conn, username)) {
                    logger.warning("用户名已存在: " + username);
                    sendResponse(response, false, "用户名已存在");
                    return;
                }

                // 2. 创建用户
                int userId = createUser(conn, username, password);
                logger.info("用户创建成功 - 用户ID: " + userId);

                // 3. 创建关联的学生记录
                createStudentRecord(conn, userId);

                // 提交事务
                conn.commit();

                logger.info("注册成功 - 用户名: " + username);
                sendResponse(response, true, "注册成功");

            } catch (SQLException e) {
                // 回滚事务
                conn.rollback();
                logger.severe("数据库操作失败，已回滚: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            logger.severe("数据库连接错误: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 检查用户名是否存在
     */
    private boolean isUsernameExists(Connection conn, String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * 创建用户
     * @return 新创建的用户ID
     */
    private int createUser(Connection conn, String username, String password)
            throws SQLException {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("创建用户失败，没有行被影响");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("创建用户失败，无法获取ID");
                }
            }
        }
    }

    /**
     * 创建学生记录
     */
    private void createStudentRecord(Connection conn, int userId) throws SQLException {
        String sql = "INSERT INTO students(user_id, name) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, "新用户"); // 默认名称
            ps.executeUpdate();
        }
    }

    /**
     * 发送JSON格式响应
     */
    private void sendResponse(HttpServletResponse response, boolean success, String message)
            throws IOException {
        ResponseData data = new ResponseData(success, message);
        response.getWriter().print(gson.toJson(data));
    }

    /**
     * 响应数据封装类
     */
    private static class ResponseData {
        boolean success;
        String message;

        ResponseData(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}