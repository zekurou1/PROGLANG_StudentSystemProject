import java.util.List;
import java.util.Optional;

public class AuthService {
    private final StudentRepository studentRepo;

    public AuthService() {
        this.studentRepo = new StudentRepository();
    }

    public Optional<Admin> authenticateAdmin(String username, String password) {
        String encInput = CaesarCipher.encrypt(password, CaesarCipher.DEFAULT_SHIFT);
        List<String> lines = FileHandler.readAllLines(DataPaths.ADMINS_MASTER);

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\|", -1);
            if (parts.length < 2) continue;
            String u = parts[0].trim();
            String enc = parts[1].trim();

            if (u.equalsIgnoreCase(username) && enc.equals(encInput)) {
                return Optional.of(new Admin(u, enc));
            }
        }
        return Optional.empty();
    }

    public Optional<Student> authenticateStudent(String username, String password) {
        String encInput = CaesarCipher.encrypt(password, CaesarCipher.DEFAULT_SHIFT);
        Optional<Student> sOpt = studentRepo.findByUsername(username);
        if (sOpt.isEmpty()) return Optional.empty();

        Student s = sOpt.get();
        if (encInput.equals(s.getEncryptedPassword())) {
            return Optional.of(s);
        }
        return Optional.empty();
    }
}
