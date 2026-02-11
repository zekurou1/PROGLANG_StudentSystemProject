public interface UserActions {
    boolean login(String username, String password);
    void logout();
    void loadDashboard(App app);
}
