import javax.swing.*;
import java.awt.*;

public class App {
    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel root;

    private final AuthService authService;
    private final StudentService studentService;

    public static final String CARD_LOGIN = "login";
    public static final String CARD_ADMIN = "admin";
    public static final String CARD_STUDENT = "student";

    public App() {
        DataPaths.ensureDataFiles();

        this.authService = new AuthService();
        this.studentService = new StudentService();

        this.frame = new JFrame("Student Information System (File-Based)");
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setSize(1000, 650);
        this.frame.setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.root = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(this, authService, studentService);

        AdminDashboardPanel adminPanel = new AdminDashboardPanel(
                this, studentService
        );

        StudentDashboardPanel studentPanel = new StudentDashboardPanel(
                this, studentService
        );

        root.add(loginPanel, CARD_LOGIN);
        root.add(adminPanel, CARD_ADMIN);
        root.add(studentPanel, CARD_STUDENT);

        frame.setContentPane(root);
    }

    public void start() {
        showLogin();
        frame.setVisible(true);
    }

    public void showLogin() {
        cardLayout.show(root, CARD_LOGIN);
    }

    public void showAdminDashboard(Admin admin) {
        // refresh admin panel each time we enter
        for (Component c : root.getComponents()) {
            if (c instanceof AdminDashboardPanel p) {
                p.setAdmin(admin);
                p.refreshAll();
            }
        }
        cardLayout.show(root, CARD_ADMIN);
    }

    public void showStudentDashboard(Student student) {
        // refresh student panel each time we enter
        for (Component c : root.getComponents()) {
            if (c instanceof StudentDashboardPanel p) {
                p.setStudent(student);
                p.refreshAll();
            }
        }
        cardLayout.show(root, CARD_STUDENT);
    }

    public JFrame getFrame() {
        return frame;
    }
}
