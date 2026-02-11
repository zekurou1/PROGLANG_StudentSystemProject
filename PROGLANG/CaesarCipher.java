public class CaesarCipher {
    public static final int DEFAULT_SHIFT = 3;

    public static String encrypt(String text, int shift) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            sb.append(shiftChar(ch, shift));
        }
        return sb.toString();
    }

    public static String decrypt(String text, int shift) {
        return encrypt(text, -shift);
    }

    private static char shiftChar(char ch, int shift) {
        // Keep it simple but stable: shift letters, digits, common symbols in ASCII range 32..126
        int min = 32;
        int max = 126;
        if (ch < min || ch > max) return ch;

        int range = (max - min + 1);
        int normalized = ch - min;
        int shifted = (normalized + shift) % range;
        if (shifted < 0) shifted += range;
        return (char) (shifted + min);
    }
}
