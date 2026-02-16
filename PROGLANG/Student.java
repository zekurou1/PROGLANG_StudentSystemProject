import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private String studentId;
    private String name;
    private List<Grade> grades;
    private List<Attendance> attendanceRecords;

    public Student(String studentId, String name, String username, String encryptedPassword) {
        super(username, encryptedPassword, "Student");
        this.studentId = studentId;
        this.name = name;
        this.grades = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public List<Grade> getGrades() { return grades; }
    public List<Attendance> getAttendanceRecords() { return attendanceRecords; }

    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setName(String name) { this.name = name; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    public void setAttendanceRecords(List<Attendance> attendanceRecords) { this.attendanceRecords = attendanceRecords; }

    @Override
    public boolean login(String username, String password) {
        return true;
    }

    @Override
    public void logout() {
        // handled by UI
    }

    @Override
    public void loadDashboard(App app) {
        displayDashboard(app);
    }

    @Override
    public void displayDashboard(App app) {
        app.showStudentDashboard(this);
    }

    public String viewGradesAsText() {
        // Return a list format with leading '- ' on each line so dashboard parsing works
        if (grades == null || grades.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (Grade g : grades) sb.append("- ").append(g.getSubject()).append(": ").append(g.getScore()).append("\n");
        return sb.toString();
    }

    public String viewAttendanceAsText() {
        // Return a list format with leading '- ' on each line so dashboard parsing works
        if (attendanceRecords == null || attendanceRecords.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (Attendance a : attendanceRecords) sb.append("- ").append(a.getDate()).append(": ").append(a.getStatus()).append("\n");
        return sb.toString();
    }
}
