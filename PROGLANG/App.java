import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

        // Use an undecorated frame so we can draw a custom title bar
        this.frame = new JFrame();
        this.frame.setUndecorated(true);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setSize(1000, 650);
        this.frame.setLocationRelativeTo(null);

        this.cardLayout = new CardLayout();
        this.root = new JPanel(cardLayout);

        // Build application panels
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

        // Create custom title bar with window controls
        JPanel titleBar = buildTitleBar();

        // Container holds title bar (NORTH) and card root (CENTER)
        JPanel container = new JPanel(new BorderLayout());
        container.add(titleBar, BorderLayout.NORTH);
        container.add(root, BorderLayout.CENTER);

        frame.setContentPane(container);
    }

    private JPanel buildTitleBar() {
        // Theme colors - match LoginPanel palette
        Color DARK = Color.decode("#2F3E46");
        Color MID = Color.decode("#52796F");
        Color SOFT = Color.decode("#CAD2C5");
        Color ERROR_RED = new Color(176, 0, 32);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(DARK);
        titleBar.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel titleLabel = new JLabel("Student Information System");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(SOFT);

        // Buttons panel
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btns.setOpaque(false);

        JButton minBtn = new JButton("—");
        JButton maxBtn = new JButton("☐");
        JButton closeBtn = new JButton("✕");

        // Common styling
        for (JButton b : new JButton[]{minBtn, maxBtn, closeBtn}) {
            b.setFocusPainted(false);
            b.setBorder(null);
            b.setContentAreaFilled(false);
            b.setPreferredSize(new Dimension(36, 26));
            b.setFont(new Font("Dialog", Font.BOLD, 12));
            b.setOpaque(true);
            b.setBackground(DARK);
            b.setForeground(SOFT);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        // Specific styles & hover effects
        minBtn.setToolTipText("Minimize");
        minBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { minBtn.setBackground(MID); }
            public void mouseExited(java.awt.event.MouseEvent e) { minBtn.setBackground(DARK); }
        });

        maxBtn.setToolTipText("Maximize/Restore");
        maxBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { maxBtn.setBackground(MID); }
            public void mouseExited(java.awt.event.MouseEvent e) { maxBtn.setBackground(DARK); }
        });

        closeBtn.setToolTipText("Close");
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { closeBtn.setBackground(ERROR_RED); closeBtn.setForeground(Color.WHITE); }
            public void mouseExited(java.awt.event.MouseEvent e) { closeBtn.setBackground(DARK); closeBtn.setForeground(SOFT); }
        });

        // Actions
        minBtn.addActionListener(e -> frame.setState(JFrame.ICONIFIED));
        maxBtn.addActionListener(e -> {
            int state = frame.getExtendedState();
            if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                frame.setExtendedState(Frame.NORMAL);
            } else {
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });
        closeBtn.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

        btns.add(minBtn);
        btns.add(maxBtn);
        btns.add(closeBtn);

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(btns, BorderLayout.EAST);

        // Enable dragging the undecorated window by the title bar
        final Point[] mouseDown = new Point[1];
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown[0] = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // double-click toggles maximize
                if (e.getClickCount() == 2) {
                    maxBtn.doClick();
                }
            }
        });
        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point curr = e.getLocationOnScreen();
                frame.setLocation(curr.x - mouseDown[0].x, curr.y - mouseDown[0].y);
            }
        });

        return titleBar;
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
