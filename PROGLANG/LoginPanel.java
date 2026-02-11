import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final App app;
    private final AuthService authService;
    private final StudentService studentService;

    private JComboBox<String> roleCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(App app, AuthService authService, StudentService studentService) {
        this.app = app;
        this.authService = authService;
        this.studentService = studentService;

        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Student Information System");
        title.setFont(title.getFont().deriveFont(24f));

        roleCombo = new JComboBox<>(new String[]{"Admin", "Student"});
        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());

        JButton seedDemoBtn = new JButton("Add Demo Student");
        seedDemoBtn.addActionListener(e -> seedDemoStudent());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(8, 8, 8, 8);
        f.anchor = GridBagConstraints.WEST;

        f.gridx = 0; f.gridy = 0;
        form.add(new JLabel("Role:"), f);
        f.gridx = 1;
        form.add(roleCombo, f);

        f.gridx = 0; f.gridy = 1;
        form.add(new JLabel("Username:"), f);
        f.gridx = 1;
        form.add(usernameField, f);

        f.gridx = 0; f.gridy = 2;
        form.add(new JLabel("Password:"), f);
        f.gridx = 1;
        form.add(passwordField, f);

        f.gridx = 0; f.gridy = 3; f.gridwidth = 2;
        f.anchor = GridBagConstraints.CENTER;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(loginBtn);
        buttons.add(seedDemoBtn);
        form.add(buttons, f);

        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0; gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy = 1;
        add(form, gbc);

        JLabel hint = new JLabel("Default Admin: admin / admin123");
        gbc.gridy = 2;
        add(hint, gbc);
    }

    private void doLogin() {
        String role = (String) roleCombo.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        if ("Admin".equalsIgnoreCase(role)) {
            authService.authenticateAdmin(username, password).ifPresentOrElse(
                    admin -> admin.displayDashboard(app),
                    () -> JOptionPane.showMessageDialog(this, "Invalid admin credentials.")
            );
        } else {
            authService.authenticateStudent(username, password).ifPresentOrElse(
                    student -> student.displayDashboard(app),
                    () -> JOptionPane.showMessageDialog(this, "Invalid student credentials.")
            );
        }
    }

    private void seedDemoStudent() {
        // Quick helper button so you can test student login right away.
        // Username: student1, Password: pass123
        boolean ok = studentService.addStudent("S-001", "Demo Student", "student1", "pass123");
        if (ok) {
            JOptionPane.showMessageDialog(this, "Demo student added.\nLogin as Student:\nstudent1 / pass123");
        } else {
            JOptionPane.showMessageDialog(this, "Demo student already exists or cannot be added.");
        }
    }
}
