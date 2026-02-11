public class Attendance {
    private String date; // YYYY-MM-DD (simple string)
    private String status; // Present / Absent

    public Attendance(String date, String status) {
        this.date = date;
        this.status = status;
    }

    public String getDate() { return date; }
    public String getStatus() { return status; }

    public void setDate(String date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return date + ":" + status;
    }
}
