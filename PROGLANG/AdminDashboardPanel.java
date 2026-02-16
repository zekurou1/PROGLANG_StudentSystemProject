import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class AdminDashboardPanel extends JPanel {
    private final App app;
    private final StudentService studentService;
    private Admin admin;

    // Color Palette (matching login)
    private final Color DARK = Color.decode("#2F3E46");
    private final Color MID = Color.decode("#52796F");
    private final Color LIGHT = Color.decode("#84A98C");
    private final Color SOFT = Color.decode("#CAD2C5");
    private final Color DEEP = Color.decode("#354F52");
    private final Color SUCCESS = new Color(76, 175, 80);
    private final Color ERROR = new Color(176, 0, 32);
    private final Color WARNING = new Color(255, 152, 0);

    // Student Management Tab Components
    private JTextField addStudentId, addName, addUsername;
    private JPasswordField addPassword;
    private JTable studentsTable;
    private JLabel studentCountLabel;
    private JLabel studentStatusLabel;

    // Grades Management Tab Components
    private JTextField gradeStudentId, gradeSubject, gradeScore;
    private JLabel gradeStatusLabel;

    // Attendance Management Tab Components
    private JTextField attStudentId, attDate;
    private JComboBox<String> attStatus;
    private JLabel attStatusLabel;

    // Reports Tab Components
    private JTextField reportStudentId;
    private JTextArea reportArea;

    public AdminDashboardPanel(App app, StudentService studentService) {
        this.app = app;
        this.studentService = studentService;
        buildUI();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void refreshAll() {
        refreshStudentTable();
        clearAllStatusLabels();
        reportArea.setText("");
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

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(SOFT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage Students, Grades & Attendance");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(title);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(subtitle);

        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel roleLabel = new JLabel("Logged in as: Admin");
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

        tabs.addTab("📚 Student Management", buildStudentManagementTab());
        tabs.addTab("📊 Grades", buildGradesTab());
        tabs.addTab("📅 Attendance", buildAttendanceTab());
        tabs.addTab("📈 Reports", buildReportsTab());

        return tabs;
    }

    // ========== STUDENT MANAGEMENT TAB ==========
    private JPanel buildStudentManagementTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Left side - Add Student Form (Card style)
        JPanel formCard = createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(25, 25, 25, 25));
        formCard.setPreferredSize(new Dimension(350, 0));

        JLabel formTitle = new JLabel("Add New Student");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(DARK);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        addStudentId = createStyledTextField();
        addName = createStyledTextField();
        addUsername = createStyledTextField();
        addPassword = createStyledPasswordField();

        JButton addBtn = createPrimaryButton("Add Student");
        addBtn.addActionListener(e -> addStudentAction());

        studentStatusLabel = createStatusLabel();

        // Assembly
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        formCard.add(createFieldGroup("Student ID", addStudentId));
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));
        formCard.add(createFieldGroup("Full Name", addName));
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));
        formCard.add(createFieldGroup("Username", addUsername));
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));
        formCard.add(createFieldGroup("Password", addPassword));
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        formCard.add(addBtn);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        formCard.add(studentStatusLabel);
        formCard.add(Box.createVerticalGlue());

        // Right side - Student Table
        JPanel tablePanel = createTablePanel();

        mainPanel.add(formCard, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Top bar with count
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false);

        studentCountLabel = new JLabel("Total Students: 0");
        studentCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentCountLabel.setForeground(DARK);

        topBar.add(studentCountLabel);

        // Table setup
        studentsTable = new JTable();
        studentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentsTable.setRowHeight(35);
        studentsTable.setShowVerticalLines(false);
        studentsTable.setGridColor(new Color(240, 240, 240));
        studentsTable.setSelectionBackground(LIGHT);
        studentsTable.setSelectionForeground(Color.WHITE);
        studentsTable.setDefaultEditor(Object.class, null); // Non-editable

        // Alternating row colors
        studentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                return c;
            }
        });

        // Style table header
        JTableHeader header = studentsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(SOFT, 1));

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshStudentTable();

        return panel;
    }

    // ========== GRADES TAB ==========
    private JPanel buildGradesTab() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel formCard = createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        formCard.setPreferredSize(new Dimension(450, 400));

        JLabel formTitle = new JLabel("Assign Grade");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(DARK);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        gradeStudentId = createStyledTextField();
        gradeSubject = createStyledTextField();
        gradeScore = createStyledTextField();

        JButton assignBtn = createPrimaryButton("Assign Grade");
        assignBtn.addActionListener(e -> assignGradeAction());

        gradeStatusLabel = createStatusLabel();

        // Add Enter key support
        ActionListener enterAction = e -> assignGradeAction();
        gradeStudentId.addActionListener(enterAction);
        gradeSubject.addActionListener(enterAction);
        gradeScore.addActionListener(enterAction);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));
        formCard.add(createFieldGroup("Student ID", gradeStudentId));
        formCard.add(Box.createRigidArea(new Dimension(0, 18)));
        formCard.add(createFieldGroup("Subject", gradeSubject));
        formCard.add(Box.createRigidArea(new Dimension(0, 18)));
        formCard.add(createFieldGroup("Score (0-100)", gradeScore));
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));
        formCard.add(assignBtn);
        formCard.add(Box.createRigidArea(new Dimension(0, 12)));
        formCard.add(gradeStatusLabel);
        formCard.add(Box.createVerticalGlue());

        mainPanel.add(formCard);

        return mainPanel;
    }

    // ========== ATTENDANCE TAB ==========
    private JPanel buildAttendanceTab() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel formCard = createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(30, 30, 30, 30));
        formCard.setPreferredSize(new Dimension(450, 420));

        JLabel formTitle = new JLabel("Mark Attendance");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(DARK);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        attStudentId = createStyledTextField();
        attDate = createStyledTextField();
        
        // Placeholder for date field
        attDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        attDate.setForeground(Color.GRAY);
        attDate.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (attDate.getForeground() == Color.GRAY) {
                    attDate.setText("");
                    attDate.setForeground(DARK);
                }
            }
        });

        attStatus = createStyledComboBox(new String[]{"Present", "Absent"});

        JButton markBtn = createPrimaryButton("Mark Attendance");
        markBtn.addActionListener(e -> markAttendanceAction());

        attStatusLabel = createStatusLabel();

        // Add Enter key support
        ActionListener enterAction = e -> markAttendanceAction();
        attStudentId.addActionListener(enterAction);
        attDate.addActionListener(enterAction);

        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));
        formCard.add(createFieldGroup("Student ID", attStudentId));
        formCard.add(Box.createRigidArea(new Dimension(0, 18)));
        formCard.add(createFieldGroup("Date (YYYY-MM-DD)", attDate));
        formCard.add(Box.createRigidArea(new Dimension(0, 18)));
        formCard.add(createFieldGroup("Status", attStatus));
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));
        formCard.add(markBtn);
        formCard.add(Box.createRigidArea(new Dimension(0, 12)));
        formCard.add(attStatusLabel);
        formCard.add(Box.createVerticalGlue());

        mainPanel.add(formCard);

        return mainPanel;
    }

    // ========== REPORTS TAB ==========
    private JPanel buildReportsTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top search bar
        JPanel searchPanel = createCardPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setPreferredSize(new Dimension(0, 70));

        JLabel searchLabel = new JLabel("Student ID:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(DARK);

        reportStudentId = createStyledTextField();
        reportStudentId.setPreferredSize(new Dimension(200, 35));

        JButton viewBtn = createPrimaryButton("View Report");
        viewBtn.setPreferredSize(new Dimension(150, 35));
        viewBtn.addActionListener(e -> viewReportAction());

        reportStudentId.addActionListener(e -> viewReportAction());

        searchPanel.add(searchLabel);
        searchPanel.add(reportStudentId);
        searchPanel.add(viewBtn);

        // Report display area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setMargin(new Insets(15, 15, 15, 15));
        reportArea.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SOFT, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // ========== ACTION HANDLERS ==========
    private void addStudentAction() {
        String id = addStudentId.getText().trim();
        String name = addName.getText().trim();
        String username = addUsername.getText().trim();
        String password = new String(addPassword.getPassword());

        // Validation
        if (id.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showStatus(studentStatusLabel, "All fields are required", ERROR);
            return;
        }

        if (password.length() < 6) {
            showStatus(studentStatusLabel, "Password must be at least 6 characters", ERROR);
            return;
        }

        boolean success = studentService.addStudent(id, name, username, password);
        
        if (success) {
            showStatus(studentStatusLabel, "✓ Student added successfully", SUCCESS);
            clearStudentForm();
            refreshStudentTable();
        } else {
            showStatus(studentStatusLabel, "Failed to add student. Check for duplicates", ERROR);
        }
    }

    private void assignGradeAction() {
        String studentId = gradeStudentId.getText().trim();
        String subject = gradeSubject.getText().trim();
        String scoreText = gradeScore.getText().trim();

        // Validation
        if (studentId.isEmpty() || subject.isEmpty() || scoreText.isEmpty()) {
            showStatus(gradeStatusLabel, "All fields are required", ERROR);
            return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException e) {
            showStatus(gradeStatusLabel, "Score must be a valid number", ERROR);
            return;
        }

        if (score < 0 || score > 100) {
            showStatus(gradeStatusLabel, "Score must be between 0 and 100", ERROR);
            return;
        }

        boolean success = studentService.assignGrade(studentId, subject, score);
        
        if (success) {
            showStatus(gradeStatusLabel, "✓ Grade assigned successfully", SUCCESS);
            clearGradeForm();
            refreshStudentTable();
        } else {
            showStatus(gradeStatusLabel, "Failed to assign grade. Check student ID", ERROR);
        }
    }

    private void markAttendanceAction() {
        String studentId = attStudentId.getText().trim();
        String date = attDate.getText().trim();
        String status = (String) attStatus.getSelectedItem();

        // Validation
        if (studentId.isEmpty() || date.isEmpty()) {
            showStatus(attStatusLabel, "Student ID and Date are required", ERROR);
            return;
        }

        // Validate date format
        if (!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date)) {
            showStatus(attStatusLabel, "Date must be in YYYY-MM-DD format", ERROR);
            return;
        }

        boolean success = studentService.markAttendance(studentId, date, status);
        
        if (success) {
            showStatus(attStatusLabel, "✓ Attendance marked successfully", SUCCESS);
            clearAttendanceForm();
            refreshStudentTable();
        } else {
            showStatus(attStatusLabel, "Failed to mark attendance. Check student ID", ERROR);
        }
    }

    private void viewReportAction() {
        String studentId = reportStudentId.getText().trim();
        
        if (studentId.isEmpty()) {
            reportArea.setText("Please enter a Student ID");
            return;
        }

        studentService.getByStudentId(studentId).ifPresentOrElse(
            student -> reportArea.setText(studentService.buildStudentSummary(student)),
            () -> reportArea.setText("⚠ Student not found with ID: " + studentId)
        );
    }

    // ========== UTILITY METHODS ==========
    private JPanel createCardPanel() {
        return new RoundedPanel(15, Color.WHITE);
    }

    private JPanel createFieldGroup(String label, JComponent field) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(DEEP);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(lbl);
        group.add(Box.createRigidArea(new Dimension(0, 6)));
        group.add(field);

        return group;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };

        Dimension size = new Dimension(300, 35);
        field.setMaximumSize(size);
        field.setPreferredSize(size);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(new Color(250, 250, 250));
        field.setForeground(DARK);
        field.setBorder(new RoundedBorder(LIGHT, 12));
        field.setMargin(new Insets(0, 12, 0, 12));
        field.setOpaque(false);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(MID, 12));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(LIGHT, 12));
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };

        Dimension size = new Dimension(300, 35);
        field.setMaximumSize(size);
        field.setPreferredSize(size);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(new Color(250, 250, 250));
        field.setForeground(DARK);
        field.setBorder(new RoundedBorder(LIGHT, 12));
        field.setMargin(new Insets(0, 12, 0, 12));
        field.setOpaque(false);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(MID, 12));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(LIGHT, 12));
            }
        });

        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        
        Dimension size = new Dimension(300, 35);
        combo.setMaximumSize(size);
        combo.setPreferredSize(size);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(new Color(250, 250, 250));
        combo.setForeground(DARK);
        combo.setBorder(new RoundedBorder(LIGHT, 12));

        return combo;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isEnabled()) {
                    g2.setColor(new Color(132, 169, 140, 150));
                } else if (getModel().isPressed()) {
                    g2.setColor(DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(DEEP);
                } else {
                    g2.setColor(MID);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(Color.WHITE);
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        Dimension size = new Dimension(300, 40);
        btn.setMaximumSize(size);
        btn.setPreferredSize(size);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        return btn;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(300, 20));
        return label;
    }

    private void showStatus(JLabel label, String message, Color color) {
        label.setText(message);
        label.setForeground(color);
        
        // Auto-clear after 5 seconds
        Timer timer = new Timer(5000, e -> label.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }

    private void clearAllStatusLabels() {
        if (studentStatusLabel != null) studentStatusLabel.setText(" ");
        if (gradeStatusLabel != null) gradeStatusLabel.setText(" ");
        if (attStatusLabel != null) attStatusLabel.setText(" ");
    }

    private void clearStudentForm() {
        addStudentId.setText("");
        addName.setText("");
        addUsername.setText("");
        addPassword.setText("");
    }

    private void clearGradeForm() {
        gradeStudentId.setText("");
        gradeSubject.setText("");
        gradeScore.setText("");
    }

    private void clearAttendanceForm() {
        attStudentId.setText("");
        attDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        attDate.setForeground(Color.GRAY);
    }

    private void refreshStudentTable() {
        List<Student> students = studentService.getAllStudents();
        
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Student ID", "Name", "Username", "Grades", "Attendance"}, 0
        );

        for (Student student : students) {
            model.addRow(new Object[]{
                student.getStudentId(),
                student.getName(),
                student.getUsername(),
                student.getGrades().size(),
                student.getAttendanceRecords().size()
            });
        }

        studentsTable.setModel(model);
        
        if (studentCountLabel != null) {
            studentCountLabel.setText("Total Students: " + students.size());
        }
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

    private class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 8;
            return insets;
        }
    }
}