import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static void ensureFileExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                File parent = f.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();
                f.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create file: " + path, e);
        }
    }

    public static List<String> readAllLines(String path) {
        ensureFileExists(path);
        try {
            return Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void writeAllLines(String path, List<String> lines) {
        ensureFileExists(path);
        try {
            Files.write(Path.of(path), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write file: " + path, e);
        }
    }

    public static void appendLine(String path, String line) {
        ensureFileExists(path);
        try (FileOutputStream fos = new FileOutputStream(path, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Cannot append file: " + path, e);
        }
    }
}
