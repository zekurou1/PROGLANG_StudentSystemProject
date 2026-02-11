public class Admin extends User {
    public Admin(String username, String encryptedPassword) {
        super(username, encryptedPassword, "Admin");
    }

    @Override
    public boolean login(String username, String password) {
        // validation handled by AuthService; keep method for abstraction compliance
        return true;
    }

    @Override
    public void logout() {
        // no-op here; UI controls logout routing
    }

    @Override
    public void loadDashboard(App app) {
        displayDashboard(app);
    }

    @Override
    public void displayDashboard(App app) {
        app.showAdminDashboard(this);
    }

    // Admin-specific actions are provided by StudentService methods (manageStudents/manageGrades/manageAttendance)
}
