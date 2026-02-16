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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class LoginPanel extends JPanel {

    private final App app;
    private final AuthService authService;
    private final StudentService studentService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JCheckBox rememberMeCheck;
    private JLabel statusLabel;
    private JButton loginBtn;

    // Color Palette
    private final Color DARK = Color.decode("#2F3E46");
    private final Color MID = Color.decode("#52796F");
    private final Color LIGHT = Color.decode("#84A98C");
    private final Color SOFT = Color.decode("#CAD2C5");
    private final Color DEEP = Color.decode("#354F52");
    private final Color ERROR_RED = new Color(176, 0, 32);

    public LoginPanel(App app, AuthService authService, StudentService studentService) {
        this.app = app;
        this.authService = authService;
        this.studentService = studentService;

        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.75);
        split.setDividerSize(0);
        split.setEnabled(false);
        split.setBorder(null);

        split.setLeftComponent(createImagePanel());
        split.setRightComponent(createLoginSide());

        add(split, BorderLayout.CENTER);
    }

    // 75% IMAGE PANEL WITH BRANDING
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background gradient for depth
                GradientPaint gradient = new GradientPaint(
                    0, 0, DARK,
                    getWidth(), getHeight(), DEEP
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle abstract shapes (mountain-inspired)
                g2.setColor(new Color(52, 79, 82, 30));
                int[] xPoints = {0, getWidth() / 3, getWidth() * 2 / 3, getWidth()};
                int[] yPoints = {getHeight(), getHeight() - 200, getHeight() - 150, getHeight()};
                g2.fillPolygon(xPoints, yPoints, 4);
                
                g2.dispose();
            }
        };

        // Branding overlay
        JPanel brandingPanel = new JPanel();
        brandingPanel.setOpaque(false);
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setBorder(new EmptyBorder(100, 80, 80, 80));

        JLabel title = new JLabel("Verdant Academy");
        title.setForeground(SOFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Rooted in Excellence, Growing Together");
        tagline.setForeground(LIGHT);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Decorative divider
        JPanel divider = new JPanel();
        divider.setBackground(LIGHT);
        divider.setPreferredSize(new Dimension(100, 2));
        divider.setMaximumSize(new Dimension(500, 2));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandingPanel.add(title);
        brandingPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        brandingPanel.add(divider);
        brandingPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        brandingPanel.add(tagline);

        panel.add(brandingPanel, BorderLayout.NORTH);

        return panel;
    }

    // 25% LOGIN SIDE
    private JPanel createLoginSide() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Color.WHITE);

        JPanel card = createLoginCard();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20);

        container.add(card, gbc);
        return container;
    }

    private JPanel createLoginCard() {
        JPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Adjusted card size (shorter without role selection)
        Dimension cardSize = new Dimension(380, 450);
        card.setPreferredSize(cardSize);
        card.setMinimumSize(cardSize);
        card.setMaximumSize(cardSize);

        // Card title
        JLabel cardTitle = new JLabel("Welcome Back");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        cardTitle.setForeground(DARK);
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cardSubtitle = new JLabel("Sign in to continue");
        cardSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardSubtitle.setForeground(DEEP);
        cardSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel userLabel = createLabel("Username");
        usernameField = createRoundedField();
        usernameField.addActionListener(e -> passwordField.requestFocus());

        // Password
        JLabel passLabel = createLabel("Password");
        passwordField = createRoundedPasswordField();
        passwordField.addActionListener(e -> doLogin());

        // Options panel
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        optionsPanel.setOpaque(false);
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsPanel.setMaximumSize(new Dimension(300, 30));

        showPasswordCheck = new JCheckBox("Show password");
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheck.setForeground(DEEP);
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setFocusPainted(false);
        showPasswordCheck.addActionListener(e -> togglePasswordVisibility());

        rememberMeCheck = new JCheckBox("Remember me");
        rememberMeCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheck.setForeground(DEEP);
        rememberMeCheck.setOpaque(false);
        rememberMeCheck.setFocusPainted(false);

        optionsPanel.add(showPasswordCheck);
        optionsPanel.add(rememberMeCheck);

        // Login button
        loginBtn = createRoundedButton("Login");
        loginBtn.addActionListener(e -> doLogin());

        // Status label (inline error/success messages)
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ERROR_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setMaximumSize(new Dimension(300, 20));

        // Footer
        JLabel footer = new JLabel("© 2026 Student Information System");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(new Color(150, 150, 150));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Assembly with proper spacing
        card.add(cardTitle);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(cardSubtitle);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        card.add(userLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(usernameField);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        card.add(optionsPanel);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(statusLabel);
        
        card.add(Box.createVerticalGlue());
        card.add(footer);

        // Auto-focus username on display
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());

        return card;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(DEEP);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JTextField createRoundedField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        Dimension fieldSize = new Dimension(300, 38);
        field.setMaximumSize(fieldSize);
        field.setPreferredSize(fieldSize);
        field.setMinimumSize(fieldSize);
        
        field.setBorder(new RoundedBorder(LIGHT, 18));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(250, 250, 250));
        field.setForeground(DARK);
        field.setMargin(new Insets(0, 12, 0, 12));
        field.setOpaque(false);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(MID, 18));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(LIGHT, 18));
            }
        });
        
        return field;
    }

    private JPasswordField createRoundedPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        Dimension fieldSize = new Dimension(300, 38);
        field.setMaximumSize(fieldSize);
        field.setPreferredSize(fieldSize);
        field.setMinimumSize(fieldSize);
        
        field.setBorder(new RoundedBorder(LIGHT, 18));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(250, 250, 250));
        field.setForeground(DARK);
        field.setMargin(new Insets(0, 12, 0, 12));
        field.setOpaque(false);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Focus effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(MID, 18));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(LIGHT, 18));
            }
        });
        
        return field;
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(132, 169, 140, 150)); // Disabled state
                } else if (getModel().isPressed()) {
                    g2.setColor(DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(DEEP);
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), textX, textY);
                
                g2.dispose();
            }
        };
        
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBackground(MID);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        Dimension btnSize = new Dimension(300, 42);
        btn.setMaximumSize(btnSize);
        btn.setPreferredSize(btnSize);
        btn.setMinimumSize(btnSize);
        
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        return btn;
    }

    private void togglePasswordVisibility() {
        if (showPasswordCheck.isSelected()) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('•');
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setForeground(isError ? ERROR_RED : MID);
    }

    private void clearStatus() {
        statusLabel.setText(" ");
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        clearStatus();

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter username and password", true);
            return;
        }

        // Disable button during authentication
        loginBtn.setEnabled(false);
        loginBtn.setText("Authenticating...");

        // Attempt authentication - try admin first, then student
        SwingUtilities.invokeLater(() -> {
            try {
                // Try admin authentication first
                var adminResult = authService.authenticateAdmin(username, password);
                if (adminResult.isPresent()) {
                    showStatus("Login successful!", false);
                    adminResult.get().displayDashboard(app);
                    return;
                }
                
                // If admin fails, try student authentication
                var studentResult = authService.authenticateStudent(username, password);
                if (studentResult.isPresent()) {
                    showStatus("Login successful!", false);
                    studentResult.get().displayDashboard(app);
                } else {
                    // Both failed
                    showStatus("Invalid credentials", true);
                    loginBtn.setEnabled(true);
                    loginBtn.setText("Login");
                }
            } catch (Exception ex) {
                showStatus("Authentication error occurred", true);
                loginBtn.setEnabled(true);
                loginBtn.setText("Login");
            }
        });
    }

    // Custom rounded panel class
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
            
            // Subtle shadow effect
            g2.setColor(new Color(0, 0, 0, 10));
            g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, cornerRadius, cornerRadius);
            
            // Card background
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
            
            // Subtle border
            g2.setColor(SOFT);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, cornerRadius, cornerRadius);
            
            g2.dispose();
        }
    }

    // Custom rounded border class
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
            return new Insets(10, 12, 10, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 10;
            return insets;
        }
    }
}