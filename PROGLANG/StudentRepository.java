import java.util.*;

public class StudentRepository {

    public List<Student> loadAllStudents() {
        List<String> lines = FileHandler.readAllLines(DataPaths.STUDENTS_MASTER);
        List<Student> students = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            Student s = parseStudentLine(line);
            if (s != null) students.add(s);
        }
        return students;
    }

    public void saveAllStudents(List<Student> students) {
        List<String> lines = new ArrayList<>();
        for (Student s : students) {
            lines.add(serializeStudent(s));
        }
        FileHandler.writeAllLines(DataPaths.STUDENTS_MASTER, lines);
    }

    public Optional<Student> findByUsername(String username) {
        for (Student s : loadAllStudents()) {
            if (s.getUsername().equalsIgnoreCase(username)) return Optional.of(s);
        }
        return Optional.empty();
    }

    public Optional<Student> findByStudentId(String studentId) {
        for (Student s : loadAllStudents()) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) return Optional.of(s);
        }
        return Optional.empty();
    }

    public void upsertStudent(Student student) {
        List<Student> all = loadAllStudents();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getStudentId().equalsIgnoreCase(student.getStudentId())) {
                all.set(i, student);
                updated = true;
                break;
            }
        }
        if (!updated) all.add(student);
        saveAllStudents(all);
    }

    private Student parseStudentLine(String line) {
        // studentId|name|username|encPass|gradesCSV|attendanceCSV
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;

        String studentId = parts[0];
        String name = parts[1];
        String username = parts[2];
        String encPass = parts[3];

        Student s = new Student(studentId, name, username, encPass);

        String gradesCSV = parts.length >= 5 ? parts[4] : "";
        String attendanceCSV = parts.length >= 6 ? parts[5] : "";

        s.setGrades(parseGrades(gradesCSV));
        s.setAttendanceRecords(parseAttendance(attendanceCSV));

        return s;
    }

    private List<Grade> parseGrades(String csv) {
        List<Grade> list = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return list;

        String[] items = csv.split(",", -1);
        for (String it : items) {
            it = it.trim();
            if (it.isEmpty()) continue;
            String[] kv = it.split(":", 2);
            if (kv.length != 2) continue;
            String subject = kv[0].trim();
            try {
                int score = Integer.parseInt(kv[1].trim());
                list.add(new Grade(subject, score));
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    private List<Attendance> parseAttendance(String csv) {
        List<Attendance> list = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return list;

        String[] items = csv.split(",", -1);
        for (String it : items) {
            it = it.trim();
            if (it.isEmpty()) continue;
            String[] kv = it.split(":", 2);
            if (kv.length != 2) continue;
            String date = kv[0].trim();
            String status = kv[1].trim();
            list.add(new Attendance(date, status));
        }
        return list;
    }

    private String serializeStudent(Student s) {
        // studentId|name|username|encPass|gradesCSV|attendanceCSV
        return s.getStudentId() + "|" +
                s.getName() + "|" +
                s.getUsername() + "|" +
                s.getEncryptedPassword() + "|" +
                gradesToCSV(s.getGrades()) + "|" +
                attendanceToCSV(s.getAttendanceRecords());
    }

    private String gradesToCSV(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Grade g : grades) {
            if (sb.length() > 0) sb.append(",");
            sb.append(g.getSubject()).append(":").append(g.getScore());
        }
        return sb.toString();
    }

    private String attendanceToCSV(List<Attendance> att) {
        if (att == null || att.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Attendance a : att) {
            if (sb.length() > 0) sb.append(",");
            sb.append(a.getDate()).append(":").append(a.getStatus());
        }
        return sb.toString();
    }
}
