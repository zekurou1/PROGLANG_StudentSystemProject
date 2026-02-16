import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class StudentDashboardPanel extends JPanel {
    private final App app;
    private final StudentService studentService;
    private Student student;

    // Color Palette (matching login and admin dashboard)
    private final Color DARK = Color.decode("#2F3E46");
    private final Color MID = Color.decode("#52796F");
    private final Color LIGHT = Color.decode("#84A98C");
    private final Color SOFT = Color.decode("#CAD2C5");
    private final Color DEEP = Color.decode("#354F52");
    private final Color SUCCESS = new Color(76, 175, 80);
    private final Color WARNING = new Color(255, 152, 0);
    private final Color EXCELLENT = new Color(76, 175, 80);
    private final Color GOOD = new Color(139, 195, 74);
    private final Color AVERAGE = new Color(255, 193, 7);
    private final Color POOR = new Color(255, 87, 34);

    // Profile Tab Components
    private JLabel nameLabel;
    private JLabel idLabel;
    private JLabel usernameLabel;
    private JLabel gradeCountLabel;
    private JLabel attendanceCountLabel;
    private JLabel avgGradeLabel;
    private JLabel attendanceRateLabel;

    // Grades Tab Components
    private JTable gradesTable;
    private JLabel gradesStatusLabel;

    // Attendance Tab Components
    private JTable attendanceTable;
    private JLabel attendanceStatusLabel;

    public StudentDashboardPanel(App app, StudentService studentService) {
        this.app = app;
        this.studentService = studentService;
        buildUI();
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void refreshAll() {
        if (student == null) return;

        // Reload latest record from file
        java.util.Optional<Student> optionalStudent = studentService.getByStudentId(student.getStudentId());
        if (optionalStudent.isPresent()) {
            student = optionalStudent.get();
            updateAllViews();
        }
    }

    private void updateAllViews() {
        updateProfileView();
        updateGradesView();
        updateAttendanceView();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Modern header with gradient
        add(createHeader(), BorderLayout.NORTH);

        // Tabbed content area
        add(createTabbedPane(), BorderLayout.CENTER);
    }

    // ========== HEADER SECTION ==========
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARK,
                    getWidth(), 0, DEEP
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        header.setPreferredSize(new Dimension(0, 80));

        // Left side - Title and subtitle
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(SOFT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("View Your Academic Progress");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(title);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(subtitle);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel roleLabel = new JLabel("Logged in as: Student");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(LIGHT);

        JButton logoutBtn = createHeaderButton("Logout");
        logoutBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                app.showLogin();
            }
        });

        rightPanel.add(roleLabel);
        rightPanel.add(logoutBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(SOFT);
                } else if (getModel().isRollover()) {
                    g2.setColor(LIGHT);
                } else {
                    g2.setColor(new Color(255, 255, 255, 30));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border
                g2.setColor(SOFT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(100, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    // ========== TABBED PANE SECTION ==========
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(Color.WHITE);
        tabs.setForeground(DARK);

        // Customize tab appearance
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected) {
                    g2.setColor(MID);
                } else {
                    g2.setColor(new Color(245, 245, 245));
                }
                g2.fillRoundRect(x, y, w, h - 5, 10, 10);
                g2.dispose();
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                    int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                g.setColor(isSelected ? Color.WHITE : DARK);
                g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }
        });

        tabs.addTab("Profile", buildProfileTab());
        tabs.addTab("My Grades", buildGradesTab());
        tabs.addTab("My Attendance", buildAttendanceTab());

        return tabs;
    }

    // ========== PROFILE TAB ==========
    private JPanel buildProfileTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Center container
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Profile Card
        JPanel profileCard = createProfileCard();
        profileCard.setMaximumSize(new Dimension(600, 200));
        profileCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stats Cards Row
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(600, 180));
        statsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel gradesStatsCard = createGradesStatsCard();
        JPanel attendanceStatsCard = createAttendanceStatsCard();

        statsRow.add(gradesStatsCard);
        statsRow.add(attendanceStatsCard);

        // Info Card
        JPanel infoCard = createInfoCard();
        infoCard.setMaximumSize(new Dimension(600, 150));
        infoCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(profileCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(statsRow);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(infoCard);

        centerPanel.add(contentPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createProfileCard() {
        JPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel cardTitle = new JLabel("Profile Information");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(DARK);
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Profile details
        nameLabel = createInfoLabel("", 16, Font.BOLD);
        idLabel = createInfoLabel("", 14, Font.PLAIN);
        usernameLabel = createInfoLabel("", 14, Font.PLAIN);

        card.add(cardTitle);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(createLabelRow("Full Name:", nameLabel));
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(createLabelRow("Student ID:", idLabel));
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(createLabelRow("Username:", usernameLabel));

        return card;
    }

    private JPanel createGradesStatsCard() {
        JPanel card = new RoundedPanel(15, new Color(248, 251, 249));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Academic Performance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        gradeCountLabel = new JLabel("0 Grades");
        gradeCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        gradeCountLabel.setForeground(MID);
        gradeCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        avgGradeLabel = new JLabel("Average: --");
        avgGradeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        avgGradeLabel.setForeground(DEEP);
        avgGradeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(gradeCountLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(avgGradeLabel);

        return card;
    }

    private JPanel createAttendanceStatsCard() {
        JPanel card = new RoundedPanel(15, new Color(248, 251, 249));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Attendance Record");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        attendanceCountLabel = new JLabel("0 Records");
        attendanceCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        attendanceCountLabel.setForeground(MID);
        attendanceCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        attendanceRateLabel = new JLabel("Rate: --");
        attendanceRateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        attendanceRateLabel.setForeground(DEEP);
        attendanceRateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(attendanceCountLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(attendanceRateLabel);

        return card;
    }

    private JPanel createInfoCard() {
        JPanel card = new RoundedPanel(15, new Color(250, 250, 250));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("About This Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea info = new JTextArea(
            "• This is a read-only view of your academic records\n" +
            "• Grades and attendance are updated by administrators\n" +
            "• Data is stored in a file-based system\n" +
            "• Changes are reflected immediately after admin updates"
        );
        info.setEditable(false);
        info.setOpaque(false);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.setForeground(DEEP);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(info);

        return card;
    }

    private JPanel createLabelRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(DEEP);
        lbl.setPreferredSize(new Dimension(120, 25));

        row.add(lbl);
        row.add(valueLabel);

        return row;
    }

    private JLabel createInfoLabel(String text, int size, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // ========== GRADES TAB ==========
    private JPanel buildGradesTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top info panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setOpaque(false);

        gradesStatusLabel = new JLabel("Your Grades");
        gradesStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gradesStatusLabel.setForeground(DARK);

        topPanel.add(gradesStatusLabel);

        // Table setup
        gradesTable = new JTable();
        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gradesTable.setRowHeight(35);
        gradesTable.setShowVerticalLines(false);
        gradesTable.setGridColor(new Color(240, 240, 240));
        gradesTable.setSelectionBackground(LIGHT);
        gradesTable.setSelectionForeground(Color.WHITE);
        gradesTable.setDefaultEditor(Object.class, null);

        // Alternating row colors with score coloring
        gradesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                
                // Color-code scores in the Score column (column 1)
                if (column == 1 && value != null) {
                    try {
                        int score = Integer.parseInt(value.toString());
                        if (!isSelected) {
                            if (score >= 90) c.setForeground(EXCELLENT);
                            else if (score >= 80) c.setForeground(GOOD);
                            else if (score >= 70) c.setForeground(AVERAGE);
                            else c.setForeground(POOR);
                        }
                    } catch (NumberFormatException e) {
                        c.setForeground(DARK);
                    }
                } else {
                    c.setForeground(isSelected ? Color.WHITE : DARK);
                }
                
                return c;
            }
        });

        // Style table header
        JTableHeader header = gradesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(SOFT, 1));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // ========== ATTENDANCE TAB ==========
    private JPanel buildAttendanceTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top info panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topPanel.setOpaque(false);

        attendanceStatusLabel = new JLabel("Your Attendance Records");
        attendanceStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        attendanceStatusLabel.setForeground(DARK);

        topPanel.add(attendanceStatusLabel);

        // Table setup
        attendanceTable = new JTable();
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        attendanceTable.setRowHeight(35);
        attendanceTable.setShowVerticalLines(false);
        attendanceTable.setGridColor(new Color(240, 240, 240));
        attendanceTable.setSelectionBackground(LIGHT);
        attendanceTable.setSelectionForeground(Color.WHITE);
        attendanceTable.setDefaultEditor(Object.class, null);

        // Alternating row colors with status coloring
        attendanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                
                // Color-code status in Status column (column 1)
                if (column == 1 && value != null && !isSelected) {
                    String status = value.toString();
                    if ("Present".equalsIgnoreCase(status)) {
                        c.setForeground(SUCCESS);
                    } else if ("Absent".equalsIgnoreCase(status)) {
                        c.setForeground(POOR);
                    } else {
                        c.setForeground(DARK);
                    }
                } else {
                    c.setForeground(isSelected ? Color.WHITE : DARK);
                }
                
                return c;
            }
        });

        // Style table header
        JTableHeader header = attendanceTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(SOFT, 1));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // ========== UPDATE METHODS ==========
    private void updateProfileView() {
        if (student == null) return;

        nameLabel.setText(student.getName());
        idLabel.setText(student.getStudentId());
        usernameLabel.setText(student.getUsername());

        // Update grade stats using student methods
        String gradesText = student.viewGradesAsText();
        int gradeCount = countGrades(gradesText);
        gradeCountLabel.setText(gradeCount + (gradeCount == 1 ? " Grade" : " Grades"));

        if (gradeCount > 0) {
            double avg = calculateAverageGrade(gradesText);
            avgGradeLabel.setText("Average: " + formatDecimal(avg) + "%");
            
            // Color code average
            if (avg >= 90) avgGradeLabel.setForeground(EXCELLENT);
            else if (avg >= 80) avgGradeLabel.setForeground(GOOD);
            else if (avg >= 70) avgGradeLabel.setForeground(AVERAGE);
            else avgGradeLabel.setForeground(POOR);
        } else {
            avgGradeLabel.setText("Average: --");
            avgGradeLabel.setForeground(DEEP);
        }

        // Update attendance stats
        String attendanceText = student.viewAttendanceAsText();
        int attCount = countAttendance(attendanceText);
        attendanceCountLabel.setText(attCount + (attCount == 1 ? " Record" : " Records"));

        if (attCount > 0) {
            int presentCount = countPresent(attendanceText);
            double rate = (presentCount * 100.0) / attCount;
            attendanceRateLabel.setText("Rate: " + formatDecimal(rate) + "%");
            
            // Color code rate
            if (rate >= 90) attendanceRateLabel.setForeground(EXCELLENT);
            else if (rate >= 75) attendanceRateLabel.setForeground(GOOD);
            else if (rate >= 60) attendanceRateLabel.setForeground(AVERAGE);
            else attendanceRateLabel.setForeground(POOR);
        } else {
            attendanceRateLabel.setText("Rate: --");
            attendanceRateLabel.setForeground(DEEP);
        }
    }

    private void updateGradesView() {
        if (student == null) return;

        String gradesText = student.viewGradesAsText();
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Subject", "Score", "Grade"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Parse grades text and populate table
        String[] lines = gradesText.split("\n");
        int count = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("Grades:") || line.equals("None")) continue;
            
            // Expected format: "- Subject: Score"
            if (line.startsWith("-")) {
                line = line.substring(1).trim();
                int colonIndex = line.indexOf(":");
                if (colonIndex > 0) {
                    String subject = line.substring(0, colonIndex).trim();
                    String scoreStr = line.substring(colonIndex + 1).trim();
                    try {
                        int score = Integer.parseInt(scoreStr);
                        String grade = getLetterGrade(score);
                        model.addRow(new Object[]{subject, score, grade});
                        count++;
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        }

        gradesTable.setModel(model);

        if (count == 0) {
            gradesStatusLabel.setText("No grades recorded yet");
        } else {
            gradesStatusLabel.setText("Your Grades (" + count + " subjects)");
        }
    }

    private void updateAttendanceView() {
        if (student == null) return;

        String attendanceText = student.viewAttendanceAsText();
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Date", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Parse attendance text and populate table
        String[] lines = attendanceText.split("\n");
        int count = 0;
        int presentCount = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("Attendance:") || line.equals("None")) continue;
            
            // Expected format: "- Date: Status"
            if (line.startsWith("-")) {
                line = line.substring(1).trim();
                int colonIndex = line.indexOf(":");
                if (colonIndex > 0) {
                    String date = line.substring(0, colonIndex).trim();
                    String status = line.substring(colonIndex + 1).trim();
                    model.addRow(new Object[]{date, status});
                    count++;
                    if ("Present".equalsIgnoreCase(status)) {
                        presentCount++;
                    }
                }
            }
        }

        attendanceTable.setModel(model);

        if (count == 0) {
            attendanceStatusLabel.setText("No attendance records yet");
        } else {
            int absentCount = count - presentCount;
            attendanceStatusLabel.setText(
                "Your Attendance Records (" + count + " total, " + presentCount + 
                " present, " + absentCount + " absent)"
            );
        }
    }

    // ========== HELPER METHODS ==========
    private int countGrades(String gradesText) {
        if (gradesText == null || gradesText.contains("None")) return 0;
        int count = 0;
        String[] lines = gradesText.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("-")) count++;
        }
        return count;
    }

    private double calculateAverageGrade(String gradesText) {
        if (gradesText == null || gradesText.contains("None")) return 0.0;
        
        int total = 0;
        int count = 0;
        String[] lines = gradesText.split("\n");
        
        for (String line : lines) {
            if (line.trim().startsWith("-")) {
                int colonIndex = line.indexOf(":");
                if (colonIndex > 0) {
                    String scoreStr = line.substring(colonIndex + 1).trim();
                    try {
                        total += Integer.parseInt(scoreStr);
                        count++;
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        }
        
        return count > 0 ? (double) total / count : 0.0;
    }

    private int countAttendance(String attendanceText) {
        if (attendanceText == null || attendanceText.contains("None")) return 0;
        int count = 0;
        String[] lines = attendanceText.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("-")) count++;
        }
        return count;
    }

    private int countPresent(String attendanceText) {
        if (attendanceText == null || attendanceText.contains("None")) return 0;
        int count = 0;
        String[] lines = attendanceText.split("\n");
        for (String line : lines) {
            if (line.trim().startsWith("-") && line.toLowerCase().contains("present")) {
                count++;
            }
        }
        return count;
    }

    private String formatDecimal(double value) {
        return String.format("%.1f", value);
    }

    private String getLetterGrade(int score) {
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
    }

    // ========== CUSTOM COMPONENTS ==========
    private class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color) {
            super();
            this.cornerRadius = radius;
            this.bgColor = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, cornerRadius, cornerRadius);

            // Background
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);

            // Border
            g2.setColor(SOFT);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);

            g2.dispose();
        }
    }
}