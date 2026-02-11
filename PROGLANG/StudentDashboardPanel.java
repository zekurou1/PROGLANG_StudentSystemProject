import javax.swing.*;
import java.awt.*;

public class StudentDashboardPanel extends JPanel {
    private final App app;
    private final StudentService studentService;

    private Student student;

    private JLabel profileLabel;
    private JTextArea gradesArea;
    private JTextArea attendanceArea;

    public StudentDashboardPanel(App app, StudentService studentService) {
        this.app = app;
        this.studentService = studentService;
        buildUI();
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void refreshAll() {
        if (student == null) return;

        // reload latest record from file (so student sees updates immediately)
        studentService.getByStudentId(student.getStudentId()).ifPresent(updated -> student = updated);

        profileLabel.setText("Student: " + student.getName() + " (ID: " + student.getStudentId() + ", username: " + student.getUsername() + ")");
        gradesArea.setText(student.viewGradesAsText());
        attendanceArea.setText(student.viewAttendanceAsText());
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Student Dashboard");
        title.setFont(title.getFont().deriveFont(18f));

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> app.showLogin());

        top.add(title, BorderLayout.WEST);
        top.add(logout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Profile", buildProfileTab());
        tabs.addTab("Grades", buildGradesTab());
        tabs.addTab("Attendance", buildAttendanceTab());

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildProfileTab() {
        JPanel panel = new JPanel(new BorderLayout());
        profileLabel = new JLabel("Student: ");
        profileLabel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(profileLabel, BorderLayout.NORTH);

        JTextArea note = new JTextArea(
                "This is a file-based system.\n" +
                "Grades and attendance are view-only for students.\n"
        );
        note.setEditable(false);
        note.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(note, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildGradesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        gradesArea = new JTextArea();
        gradesArea.setEditable(false);
        panel.add(new JScrollPane(gradesArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAttendanceTab() {
        JPanel panel = new JPanel(new BorderLayout());
        attendanceArea = new JTextArea();
        attendanceArea.setEditable(false);
        panel.add(new JScrollPane(attendanceArea), BorderLayout.CENTER);
        return panel;
    }
}
