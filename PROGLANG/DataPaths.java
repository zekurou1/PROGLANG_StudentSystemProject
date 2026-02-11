import java.io.File;

public class DataPaths {
    public static final String DATA_DIR = "data";
    public static final String STUDENTS_MASTER = DATA_DIR + File.separator + "students_master.txt";
    public static final String ADMINS_MASTER = DATA_DIR + File.separator + "admins_master.txt";
    public static final String GRADES_TXN = DATA_DIR + File.separator + "grades_transactions.txt";
    public static final String ATTEND_TXN = DATA_DIR + File.separator + "attendance_transactions.txt";

    public static void ensureDataFiles() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        FileHandler.ensureFileExists(STUDENTS_MASTER);
        FileHandler.ensureFileExists(GRADES_TXN);
        FileHandler.ensureFileExists(ATTEND_TXN);

        // Ensure admins file exists with a default admin
        if (!new File(ADMINS_MASTER).exists()) {
            FileHandler.ensureFileExists(ADMINS_MASTER);
            // default admin: admin / admin123
            String enc = CaesarCipher.encrypt("admin123", CaesarCipher.DEFAULT_SHIFT);
            FileHandler.appendLine(ADMINS_MASTER, "admin|" + enc);
        } else {
            // if exists but empty, also add default
            if (FileHandler.readAllLines(ADMINS_MASTER).isEmpty()) {
                String enc = CaesarCipher.encrypt("admin123", CaesarCipher.DEFAULT_SHIFT);
                FileHandler.appendLine(ADMINS_MASTER, "admin|" + enc);
            }
        }
    }
}
