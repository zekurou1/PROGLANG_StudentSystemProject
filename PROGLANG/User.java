public abstract class User implements UserActions {
    private String username;
    private String encryptedPassword;
    private String role;

    public User(String username, String encryptedPassword, String role) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public String getRole() { return role; }

    public void setUsername(String username) { this.username = username; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }
    public void setRole(String role) { this.role = role; }

    public abstract void displayDashboard(App app);
}
