import java.time.LocalDateTime;
import java.util.*;

public class StudentService {
    private final StudentRepository repo;

    public StudentService() {
        this.repo = new StudentRepository();
    }

    public List<Student> getAllStudents() {
        return repo.loadAllStudents();
    }

    public Optional<Student> getByStudentId(String studentId) {
        return repo.findByStudentId(studentId);
    }

    public Optional<Student> getByUsername(String username) {
        return repo.findByUsername(username);
    }

    public boolean addStudent(String studentId, String name, String username, String rawPassword) {
        if (studentId == null || studentId.isBlank()) return false;
        if (username == null || username.isBlank()) return false;
        if (rawPassword == null || rawPassword.isBlank()) return false;

        // uniqueness
        if (repo.findByStudentId(studentId).isPresent()) return false;
        if (repo.findByUsername(username).isPresent()) return false;

        String enc = CaesarCipher.encrypt(rawPassword, CaesarCipher.DEFAULT_SHIFT);
        Student s = new Student(studentId.trim(), name == null ? "" : name.trim(), username.trim(), enc);
        repo.upsertStudent(s);
        return true;
    }

    public boolean assignGrade(String studentId, String subject, int score) {
        Optional<Student> opt = repo.findByStudentId(studentId);
        if (opt.isEmpty()) return false;

        Student s = opt.get();
        if (subject == null || subject.isBlank()) return false;
        if (score < 0 || score > 100) return false;

        // transaction log first
        String ts = LocalDateTime.now().toString();
        FileHandler.appendLine(DataPaths.GRADES_TXN, ts + "|" + studentId + "|" + subject.trim() + "|" + score);

        // update master
        List<Grade> grades = new ArrayList<>(s.getGrades());
        boolean updated = false;
        for (int i = 0; i < grades.size(); i++) {
            if (grades.get(i).getSubject().equalsIgnoreCase(subject.trim())) {
                grades.set(i, new Grade(subject.trim(), score));
                updated = true;
                break;
            }
        }
        if (!updated) grades.add(new Grade(subject.trim(), score));
        s.setGrades(grades);

        repo.upsertStudent(s);
        return true;
    }

    public boolean markAttendance(String studentId, String dateYYYYMMDD, String status) {
        Optional<Student> opt = repo.findByStudentId(studentId);
        if (opt.isEmpty()) return false;

        if (dateYYYYMMDD == null || dateYYYYMMDD.isBlank()) return false;
        if (status == null || status.isBlank()) return false;

        String normalizedStatus = status.trim();
        if (!normalizedStatus.equalsIgnoreCase("Present") && !normalizedStatus.equalsIgnoreCase("Absent")) {
            return false;
        }

        Student s = opt.get();

        // transaction log first
        String ts = java.time.LocalDateTime.now().toString();
        FileHandler.appendLine(DataPaths.ATTEND_TXN, ts + "|" + studentId + "|" + dateYYYYMMDD.trim() + "|" + normalizedStatus);

        // update master
        List<Attendance> att = new ArrayList<>(s.getAttendanceRecords());
        boolean updated = false;
        for (int i = 0; i < att.size(); i++) {
            if (att.get(i).getDate().equals(dateYYYYMMDD.trim())) {
                att.set(i, new Attendance(dateYYYYMMDD.trim(), cap(normalizedStatus)));
                updated = true;
                break;
            }
        }
        if (!updated) att.add(new Attendance(dateYYYYMMDD.trim(), cap(normalizedStatus)));
        s.setAttendanceRecords(att);

        repo.upsertStudent(s);
        return true;
    }

    public String buildStudentSummary(Student s) {
        StringBuilder sb = new StringBuilder();
        sb.append("Student ID: ").append(s.getStudentId()).append("\n");
        sb.append("Name: ").append(s.getName()).append("\n");
        sb.append("Username: ").append(s.getUsername()).append("\n\n");

        sb.append("Grades:\n");
        if (s.getGrades().isEmpty()) sb.append("- none\n");
        for (Grade g : s.getGrades()) sb.append("- ").append(g.getSubject()).append(": ").append(g.getScore()).append("\n");

        sb.append("\nAttendance:\n");
        if (s.getAttendanceRecords().isEmpty()) sb.append("- none\n");
        for (Attendance a : s.getAttendanceRecords()) sb.append("- ").append(a.getDate()).append(": ").append(a.getStatus()).append("\n");

        return sb.toString();
    }

    private String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        String lower = s.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}
