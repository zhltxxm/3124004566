// ========== 主程序入口 ==========
import java.util.List;
import java.util.Scanner;
import java.sql.SQLException;

/**
 * 系统主入口类
 * 处理所有用户交互逻辑
 */
public class CourseSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser;  // 当前登录用户
    private static UserDAO userDAO = new UserDAO();
    private static StudentDAO studentDAO = new StudentDAO();
    private static CourseDAO courseDAO = new CourseDAO();

    public static void main(String[] args) {
        showMainMenu();
    }

    // 主菜单
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== 学生选课管理系统 ===");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 修改密码");
            System.out.println("4. 退出系统");
            System.out.print("请输入选项(1-4): ");

            int choice = getIntInput(1, 4);
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    changePassword();
                    break;
                case 4:
                    System.out.println("感谢使用，再见！");
                    System.exit(0);
            }
        }
    }

    private static void changePassword() {
        System.out.print("\n请输入用户名: ");
        String username = scanner.next();
        System.out.print("请输入原密码: ");
        String password = scanner.next();
        try {
            currentUser = userDAO.login(username, password);
            if (currentUser != null) {
                System.out.println("密码正确！");
                System.out.println("请输入新的密码：");
                String newPassword = scanner.next();
                if (userDAO.uodatePassword(username,newPassword)) {
                    System.out.println("修改成功！");
                } else {
                    System.out.println("用户名或密码错误！");
                }
            }
        } catch (SQLException e) {
            System.out.println("登录失败: " + e.getMessage());
        }
    }
//try {
//        if (courseDAO.deleteCourse(needDeleteCourse)) {
//            System.out.println("修改成功！");
//        } else {
//            System.out.println("修改失败！");
//        }
//    } catch (SQLException e) {
//        System.out.println("修改失败: " + e.getMessage());
//    }
    // 处理登录
    private static void handleLogin() {
        System.out.print("\n请输入用户名: ");
        String username = scanner.next();
        System.out.print("请输入密码: ");
        String password = scanner.next();

        try {
            currentUser = userDAO.login(username, password);
            if (currentUser != null) {
                System.out.println("登录成功！");
                showRoleMenu();
            } else {
                System.out.println("用户名或密码错误！");
            }
        } catch (SQLException e) {
            System.out.println("登录失败: " + e.getMessage());
        }
    }

    // 处理注册
    private static void handleRegister() {
        System.out.print("\n请输入用户名: ");
        String username = scanner.next();
        System.out.print("请输入密码: ");
        String password = scanner.next();
        System.out.print("请选择角色(1-学生 2-管理员): ");
        int roleChoice = getIntInput(1, 2);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(roleChoice == 1 ? "student" : "admin");

        try {
            if (userDAO.register(newUser)) {
                System.out.println("注册成功！");
                if ("student".equals(newUser.getRole())) {
                    handleStudentRegistration(newUser.getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("注册失败: " + e.getMessage());
        }
    }

    // 处理学生信息补充
    private static void handleStudentRegistration(int userId) {
        System.out.print("\n请输入学生姓名: ");
        String name = scanner.next();
        System.out.print("请输入手机号: ");
        String phone = scanner.next();

        Student student = new Student();
        student.setUserId(userId);
        student.setName(name);
        student.setPhone(phone);

        try {
            studentDAO.addStudent(student);
            System.out.println("学生信息补充完成！");
        } catch (SQLException e) {
            System.out.println("信息保存失败: " + e.getMessage());
        }
    }

    // 显示角色菜单
    private static void showRoleMenu() {
        while (true) {
            if ("admin".equals(currentUser.getRole())) {
                showAdminMenu();
            } else {
                showStudentMenu();
            }
        }
    }

    // 管理员菜单
    private static void showAdminMenu() {
        while (true) {
            System.out.println("\n=== 管理员功能菜单 ===");
            System.out.println("1. 学生管理");
            System.out.println("2. 课程管理");
            System.out.println("3. 选课管理");
            System.out.println("4. 返回上级");
            System.out.print("请输入选项(1-4): ");

            int choice = getIntInput(1, 4);
            switch (choice) {
                case 1:
                    manageStudents();
                    break;
                case 2:
                    manageCourses();
                    break;
                case 3:
                    manageChoiceCourses();
                    break;
                case 4:
                    return;
            }
        }
    }

    private static void manageChoiceCourses() {
        while (true) {
            System.out.println("\n=== 选课管理 ===");
            System.out.println("1. 查询某一门课程的选课学生");
            System.out.println("2. 查询某个学生的选课信息");
            System.out.println("3. 返回上级");
            System.out.print("请输入选项(1-3): ");

            int choice = getIntInput(1, 3);
            switch (choice) {
                case 1:
                    showCourseStudents();
                    break;
                case 2:
                    getCourses();
                    break;
                case 3:
                    return;
            }
        }
    }
    //列出所有选择该课程的学生=======================================================

    public static void showCourseStudents() {
        System.out.print("请输入要查询的课程ID：");
        int courseId = scanner.nextInt();

        try {
            List<Student> students = courseDAO.getStudentsByCourse(courseId);

            if (students.isEmpty()) {
                System.out.println("该课程暂无学生选课！");
            } else {
                System.out.println("\n=== 课程选课学生列表 ===");
                System.out.println("ID\t姓名\t\t手机号");
                for (Student s : students) {
                    System.out.printf("%d\t%s\t\t%s\n",
                            s.getId(), s.getName(), s.getPhone());
                }
            }
        } catch (SQLException e) {
            System.out.println("查询失败：" + e.getMessage());
        }
    }

    //课程管理
    private static void manageCourses() {
        while (true) {
            System.out.println("\n=== 课程管理 ===");
            System.out.println("1. 查询所有课程");
            System.out.println("2. 修改课程信息");
            System.out.println("3. 返回上级");
            System.out.print("请输入选项(1-3): ");

            int choice = getIntInput(1, 3);
            switch (choice) {
                case 1:
                    listAllCourse();
                    break;
                case 2:
                    updateCourse();
                    break;
                case 3:
                    return;
            }
        }
    }
    //增删课程
    private static void updateCourse() {
        while (true) {
            System.out.println("\n=== 课程管理 ===");
            System.out.println("1. 增加新课程");
            System.out.println("2. 删除课程");
            System.out.println("3. 返回上级");
            System.out.print("请输入选项(1-3): ");

            int choice = getIntInput(1, 3);
            switch (choice) {
                case 1:
                    addNewCourse();
                    break;
                case 2:
                    deleteCourse();
                    break;
                case 3:
                    return;
            }
        }
    }
    //删除课程
    private static void deleteCourse() {
        System.out.print("\n请输入需要删除的课程名称 ");
        String needDeleteCourse = scanner.next();

        try {
            if (courseDAO.deleteCourse(needDeleteCourse)) {
                System.out.println("修改成功！");
            } else {
                System.out.println("修改失败！");
            }
        } catch (SQLException e) {
            System.out.println("修改失败: " + e.getMessage());
        }
    }
    //添加新课程
    private static void addNewCourse() {
        System.out.print("\n请输入新课程名称 ");
        String newCourse = scanner.next();
        System.out.print("请输入新课程学分: ");
        int newCredit = Integer.parseInt(scanner.next());

        try {
            if (courseDAO.addCourse(newCourse, newCredit)) {
                System.out.println("修改成功！");
            } else {
                System.out.println("学生不存在！");
            }
        } catch (SQLException e) {
            System.out.println("修改失败: " + e.getMessage());
        }
    }
    //列出所有课程
    private static void listAllCourse() {
        try {
            System.out.println("\n=== 学生列表 ===");
            courseDAO.getAllCourses().forEach(course ->
                    System.out.printf("课程序号:%-5d 课程名称:%-5s 课程学分:%s%n",
                            course.getId(), course.getName(), course.getCredit()));
        } catch (SQLException e) {
            System.out.println("查询失败: " + e.getMessage());
        }
    }

    // 学生管理子菜单
    private static void manageStudents() {
        while (true) {
            System.out.println("\n=== 学生管理 ===");
            System.out.println("1. 查询所有学生");
            System.out.println("2. 修改手机号");
            System.out.println("3. 返回上级");
            System.out.print("请输入选项(1-3): ");

            int choice = getIntInput(1, 3);
            switch (choice) {
                case 1:
                    listAllStudents();
                    break;
                case 2:
                    updateStudentPhone();
                    break;
                case 3:
                    return;
            }
        }
    }

    // 列出所有学生
    private static void listAllStudents() {
        try {
            System.out.println("\n=== 学生列表 ===");
            studentDAO.getAllStudents().forEach(student ->
                    System.out.printf("学号:%-5d 姓名:%-5s 手机号:%s%n",
                            student.getId(), student.getName(), student.getPhone()));
        } catch (SQLException e) {
            System.out.println("查询失败: " + e.getMessage());
        }
    }

    // 修改学生手机号
    private static void updateStudentPhone() {
        System.out.print("\n请输入学生ID: ");
        int studentId = getIntInput(1, Integer.MAX_VALUE);
        System.out.print("请输入新手机号: ");
        String newPhone = scanner.next();

        try {
            if (studentDAO.updatePhone(studentId, newPhone)) {
                System.out.println("修改成功！");
            } else {
                System.out.println("学生不存在！");
            }
        } catch (SQLException e) {
            System.out.println("修改失败: " + e.getMessage());
        }
    }

    // 学生菜单
    private static void showStudentMenu() {
        while (true) {
            System.out.println("\n=== 学生功能菜单 ===");
            System.out.println("1. 添加课程");
            System.out.println("2. 删除课程");
            System.out.println("3. 查询课程");
            System.out.println("4. 返回上级");
            System.out.print("请输入选项(1-4): ");

            int choice = getIntInput(1, 4);
            switch (choice) {
                case 1:
                    studentSelectCourse();
                    break;
                case 2:
                    studentElectCourse();
                case 3:
                    getCourses();
                    break;
                case 4:
                    return;
            }
        }
    }

    private static void getCourses() {
        System.out.print("\n请输入学生ID: ");
        int studentId = getIntInput(1, Integer.MAX_VALUE);
        CourseDAO courseDAO = new CourseDAO();
        try {
            List<Course>list=courseDAO.getSelectedCourses(studentId);
            for (Course course : list) {
                System.out.println("Course ID: " + course.getId() + ", Name: " + course.getName() +"Credit:" + course.getCredit());
            }
            } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

    //退选课程：删除
    private static void studentElectCourse() {
        System.out.print("\n请输入学生ID: ");
        int studentId = getIntInput(1, Integer.MAX_VALUE);
        System.out.print("请输入退选的课程id: ");
        int newCourse_id = Integer.parseInt(scanner.next());
        try {
            if (courseDAO.dropCourse(studentId, newCourse_id)) {
                System.out.println("退选成功！");
            } else {
                System.out.println("退选失败！");
            }
        } catch (SQLException e) {
            System.out.println("修改失败: " + e.getMessage());
        }
    }

    //学生添加课程
    private static void studentSelectCourse() {
        System.out.print("\n请输入学生ID: ");
        int studentId = getIntInput(1, Integer.MAX_VALUE);
        System.out.print("请输入添加的课程: ");
        int newCourse_id = Integer.parseInt(scanner.next());
        try {
            if (courseDAO.selectCourse(studentId, newCourse_id)) {
                System.out.println("修改成功！");
            } else {
                System.out.println("添加失败！");
            }
        } catch (SQLException e) {
            System.out.println("修改失败: " + e.getMessage());
        }
    }

    // 获取有效整数输入
    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) return input;
                System.out.printf("请输入%d-%d之间的数字: ", min, max);
            } catch (Exception e) {
                scanner.nextLine(); // 清空缓冲区
                System.out.print("输入无效，请重新输入: ");
            }
        }
    }
}