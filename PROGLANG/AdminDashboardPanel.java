import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardPanel extends JPanel {
    private final App app;
    private final StudentService studentService;

    private Admin admin;
    private JTabbedPane tabs;

    // Student Management UI
    private JTextField addStudentId;
    private JTextField addName;
    private JTextField addUsername;
    private JPasswordField addPassword;
    private JTable studentsTable;

    // Grades UI
    private JTextField gradeStudentId;
    private JTextField gradeSubject;
    private JTextField gradeScore;

    // Attendance UI
    private JTextField attStudentId;
    private JTextField attDate;
    private JComboBox<String> attStatus;

    // Reports UI
    private JTextField reportStudentId;
    private JTextArea reportArea;

    public AdminDashboardPanel(App app, StudentService studentService) {
        this.app = app;
        this.studentService = studentService;
        buildUI();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void refreshAll() {
        refreshStudentTable();
        reportArea.setText("");
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(title.getFont().deriveFont(18f));

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> app.showLogin());

        top.add(title, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        tabs = new JTabbedPane();

        tabs.addTab("Student Management", buildStudentManagementTab());
        tabs.addTab("Grades Management", buildGradesTab());
        tabs.addTab("Attendance Management", buildAttendanceTab());
        tabs.addTab("Reports", buildReportsTab());

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildStudentManagementTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        addStudentId = new JTextField(12);
        addName = new JTextField(16);
        addUsername = new JTextField(14);
        addPassword = new JPasswordField(14);

        JButton addBtn = new JButton("Add Student");
        addBtn.addActionListener(e -> addStudentAction());

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        addPanel.add(addStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        addPanel.add(addName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        addPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        addPanel.add(addUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        addPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        addPanel.add(addPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        addPanel.add(addBtn, gbc);

        studentsTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(studentsTable);

        panel.add(addPanel, BorderLayout.WEST);
        panel.add(tableScroll, BorderLayout.CENTER);

        refreshStudentTable();
        return panel;
    }

    private JPanel buildGradesTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gradeStudentId = new JTextField(14);
        gradeSubject = new JTextField(14);
        gradeScore = new JTextField(6);

        JButton assignBtn = new JButton("Assign/Update Grade");
        assignBtn.addActionListener(e -> assignGradeAction());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        panel.add(gradeStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        panel.add(gradeSubject, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Score (0-100):"), gbc);
        gbc.gridx = 1;
        panel.add(gradeScore, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(assignBtn, gbc);

        return panel;
    }

    private JPanel buildAttendanceTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        attStudentId = new JTextField(14);
        attDate = new JTextField(10);
        attStatus = new JComboBox<>(new String[]{"Present", "Absent"});

        JButton markBtn = new JButton("Mark/Update Attendance");
        markBtn.addActionListener(e -> markAttendanceAction());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        panel.add(attStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(attDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        panel.add(attStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(markBtn, gbc);

        return panel;
    }

    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reportStudentId = new JTextField(14);
        JButton viewBtn = new JButton("View Summary");
        viewBtn.addActionListener(e -> viewReportAction());

        top.add(new JLabel("Student ID:"));
        top.add(reportStudentId);
        top.add(viewBtn);

        reportArea = new JTextArea();
        reportArea.setEditable(false);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        return panel;
    }

    private void addStudentAction() {
        String id = addStudentId.getText().trim();
        String name = addName.getText().trim();
        String username = addUsername.getText().trim();
        String pass = new String(addPassword.getPassword());

        boolean ok = studentService.addStudent(id, name, username, pass);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Student added successfully.");
            addStudentId.setText("");
            addName.setText("");
            addUsername.setText("");
            addPassword.setText("");
            refreshStudentTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add student. Check duplicates and required fields.");
        }
    }

    private void assignGradeAction() {
        String id = gradeStudentId.getText().trim();
        String subject = gradeSubject.getText().trim();
        String scoreStr = gradeScore.getText().trim();

        int score;
        try {
            score = Integer.parseInt(scoreStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Score must be a number.");
            return;
        }

        boolean ok = studentService.assignGrade(id, subject, score);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Grade saved (transaction logged + master updated).");
            refreshStudentTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign grade. Check Student ID, subject, score range.");
        }
    }

    private void markAttendanceAction() {
        String id = attStudentId.getText().trim();
        String date = attDate.getText().trim();
        String status = (String) attStatus.getSelectedItem();

        boolean ok = studentService.markAttendance(id, date, status);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Attendance saved (transaction logged + master updated).");
            refreshStudentTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to mark attendance. Check Student ID, date format, status.");
        }
    }

    private void viewReportAction() {
        String id = reportStudentId.getText().trim();
        studentService.getByStudentId(id).ifPresentOrElse(
                s -> reportArea.setText(studentService.buildStudentSummary(s)),
                () -> reportArea.setText("Student not found.")
        );
    }

    private void refreshStudentTable() {
        List<Student> list = studentService.getAllStudents();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Student ID", "Name", "Username", "Grades Count", "Attendance Count"},
                0
        );
        for (Student s : list) {
            model.addRow(new Object[]{
                    s.getStudentId(),
                    s.getName(),
                    s.getUsername(),
                    s.getGrades().size(),
                    s.getAttendanceRecords().size()
            });
        }
        studentsTable.setModel(model);
    }
}
