
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class LoginPage {

    private static final Color BG_TOP = new Color(238, 233, 254);
    private static final Color BG_BOTTOM = new Color(210, 198, 250);
    private static final Color PRIMARY = new Color(124, 77, 220);
    private static final Color PRIMARY_DARK = new Color(96, 54, 190);
    private static final Color TEXT_MUTED = new Color(120, 112, 140);
    private static final Color FIELD_BG = new Color(247, 245, 252);
    private static final Color FIELD_BORDER = new Color(222, 216, 240);
    private static final Color SUCCESS_GREEN = new Color(46, 175, 110);
    private static final Color ERROR_RED = new Color(220, 68, 92);
    private static final Color WARNING_AMBER = new Color(190, 130, 20);

    private static JFrame frame;
    private static JPanel background;
    private static GridBagLayout backgroundLayout;
    private static GridBagConstraints cardConstraints;
    private static JPanel card;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            frame = new JFrame("Campus Care - Login");
            frame.setSize(950, 660);
            frame.setMinimumSize(new Dimension(700, 580));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(buildBackground());
            frame.setVisible(true);
        });
    }

    private static JPanel buildBackground() {
        backgroundLayout = new GridBagLayout();

        background = new JPanel(backgroundLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setPaint(new GradientPaint(
                        0, 0, BG_TOP,
                        getWidth(), getHeight(), BG_BOTTOM
                ));

                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        background.setOpaque(false);

        card = buildCard();
        cardConstraints = new GridBagConstraints();
        background.add(card, cardConstraints);

        return background;
    }

    private static JPanel buildCard() {
        RoundedPanel loginCard = new RoundedPanel(24, Color.WHITE);
        loginCard.setLayout(new GridBagLayout());
        loginCard.setPreferredSize(new Dimension(400, 540));
        loginCard.setBorder(
                BorderFactory.createEmptyBorder(36, 40, 32, 40)
        );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel("CC", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();

                super.paintComponent(g);
            }
        };

        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setPreferredSize(new Dimension(56, 56));

        JPanel logoWrap = new JPanel();
        logoWrap.setOpaque(false);
        logoWrap.add(logo);

        c.gridy = 0;
        c.insets = new Insets(0, 0, 12, 0);
        loginCard.add(logoWrap, c);

        JLabel title = new JLabel(
                "CAMPUS CARE", SwingConstants.CENTER
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(PRIMARY_DARK);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 4, 0);
        loginCard.add(title, c);

        JLabel subtitle = new JLabel(
                "Smart Campus Management System",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);

        c.gridy = 2;
        c.insets = new Insets(0, 0, 26, 0);
        loginCard.add(subtitle, c);

        JLabel userLabel = new JLabel("USERNAME OR EMAIL");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        userLabel.setForeground(TEXT_MUTED);

        c.gridy = 3;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 2, 4, 0);
        loginCard.add(userLabel, c);
        c.anchor = GridBagConstraints.CENTER;

        RoundedField usernameField = new RoundedField();

        JLabel checkLabel = new JLabel(" ");
        checkLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        checkLabel.setForeground(SUCCESS_GREEN);
        checkLabel.setHorizontalAlignment(SwingConstants.CENTER);
        checkLabel.setPreferredSize(new Dimension(24, 20));

        JPanel userRow = new JPanel(new BorderLayout(4, 0));
        userRow.setOpaque(false);
        userRow.setPreferredSize(new Dimension(280, 42));
        userRow.add(usernameField, BorderLayout.CENTER);
        userRow.add(checkLabel, BorderLayout.EAST);

        usernameField.getDocument().addDocumentListener(
                new DocumentListener() {
                    private void refresh() {
                        checkLabel.setText(
                                usernameField.getText().trim().isEmpty()
                                        ? " " : "\u2713"
                        );
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        refresh();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        refresh();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        refresh();
                    }
                }
        );

        c.gridy = 4;
        c.insets = new Insets(0, 0, 16, 0);
        loginCard.add(userRow, c);

        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        passLabel.setForeground(TEXT_MUTED);

        c.gridy = 5;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 2, 4, 0);
        loginCard.add(passLabel, c);
        c.anchor = GridBagConstraints.CENTER;

        RoundedPasswordField passwordField =
                new RoundedPasswordField();

        JButton toggleBtn = new JButton("SHOW");
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        toggleBtn.setForeground(PRIMARY);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setPreferredSize(new Dimension(48, 20));

        toggleBtn.addActionListener(e -> {
            if (passwordField.getEchoChar() != 0) {
                passwordField.setEchoChar((char) 0);
                toggleBtn.setText("HIDE");
            } else {
                passwordField.setEchoChar('\u2022');
                toggleBtn.setText("SHOW");
            }
        });

        JPanel passRow = new JPanel(new BorderLayout(4, 0));
        passRow.setOpaque(false);
        passRow.setPreferredSize(new Dimension(280, 42));
        passRow.add(passwordField, BorderLayout.CENTER);
        passRow.add(toggleBtn, BorderLayout.EAST);

        c.gridy = 6;
        c.insets = new Insets(0, 0, 4, 0);
        loginCard.add(passRow, c);

        JLabel capsLabel = new JLabel(" ");
        capsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        capsLabel.setForeground(WARNING_AMBER);

        c.gridy = 7;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 2, 10, 0);
        loginCard.add(capsLabel, c);
        c.anchor = GridBagConstraints.CENTER;

        Timer capsTimer = new Timer(300, e ->
                updateCapsLabel(capsLabel)
        );

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateCapsLabel(capsLabel);
                capsTimer.start();
            }

            @Override
            public void focusLost(FocusEvent e) {
                capsTimer.stop();
                capsLabel.setText(" ");
            }
        });

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR_RED);

        c.gridy = 8;
        c.insets = new Insets(0, 2, 14, 0);
        loginCard.add(errorLabel, c);

        RoundedButton loginButton = new RoundedButton("LOGIN");

        c.gridy = 9;
        c.ipady = 10;
        c.insets = new Insets(4, 0, 0, 0);
        loginCard.add(loginButton, c);

        JLabel footer = new JLabel(
                "\u00A9 Campus Care Smart System",
                SwingConstants.CENTER
        );
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(TEXT_MUTED);

        c.gridy = 10;
        c.ipady = 0;
        c.insets = new Insets(22, 0, 0, 0);
        loginCard.add(footer, c);

        Runnable doLogin = () -> attemptLogin(
                usernameField.getText().trim(),
                new String(passwordField.getPassword()),
                errorLabel,
                loginButton
        );

        loginButton.addActionListener(e -> doLogin.run());
        passwordField.addActionListener(e -> doLogin.run());
        usernameField.addActionListener(
                e -> passwordField.requestFocusInWindow()
        );

        return loginCard;
    }

    private static void updateCapsLabel(JLabel capsLabel) {
        try {
            boolean capsOn =
                    Toolkit.getDefaultToolkit().getLockingKeyState(
                            KeyEvent.VK_CAPS_LOCK
                    );

            capsLabel.setText(
                    capsOn ? "\u26A0 Caps Lock is ON" : " "
            );
        } catch (Exception ignored) {
            // Some systems do not support checking Caps Lock.
        }
    }

    private static void shakeCard() {
        int[] offsets = {-14, 12, -10, 8, -6, 4, -2, 0};
        Timer timer = new Timer(35, null);
        final int[] i = {0};

        timer.addActionListener(e -> {
            if (i[0] >= offsets.length) {
                timer.stop();
                return;
            }

            cardConstraints.insets =
                    new Insets(0, offsets[i[0]], 0, 0);

            backgroundLayout.setConstraints(card, cardConstraints);
            background.revalidate();
            background.repaint();
            i[0]++;
        });

        timer.start();
    }

    // Login as a student or admin, then open the matching dashboard.
    private static void attemptLogin(
            String username,
            String password,
            JLabel errorLabel,
            RoundedButton loginButton) {

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username/email and password");
            shakeCard();
            return;
        }

        loginButton.setEnabled(false);
        errorLabel.setText(" ");

        int[] dots = {0};
        Timer dotsTimer = new Timer(400, e -> {
            dots[0] = (dots[0] % 3) + 1;
            StringBuilder sb = new StringBuilder("LOGGING IN");
            for (int i = 0; i < dots[0]; i++) {
                sb.append('.');
            }
            loginButton.setText(sb.toString());
        });
        dotsTimer.start();

        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() {
                boolean accountExists = false;

                try (Connection con = DatabaseConnection.getConnection()) {
                    if (con == null) {
                        return new Object[]{"DB_FAIL", null, null};
                    }

                    // Check student account by username or email.
                    String studentSql =
                            "SELECT username, password FROM students " +
                                    "WHERE LOWER(TRIM(username)) = LOWER(TRIM(?)) " +
                                    "OR LOWER(TRIM(email)) = LOWER(TRIM(?))";

                    try (PreparedStatement pst = con.prepareStatement(studentSql)) {
                        pst.setString(1, username);
                        pst.setString(2, username);

                        try (ResultSet rs = pst.executeQuery()) {
                            if (rs.next()) {
                                accountExists = true;
                                String dbPassword = rs.getString("password");

                                if (dbPassword != null && dbPassword.equals(password)) {
                                    return new Object[]{
                                            "STUDENT", rs.getString("username"), null
                                    };
                                }
                            }
                        }
                    }

                    // Check admin account by username.
                    String adminSql =
                            "SELECT username, password FROM admins " +
                                    "WHERE LOWER(TRIM(username)) = LOWER(TRIM(?))";

                    try (PreparedStatement pst = con.prepareStatement(adminSql)) {
                        pst.setString(1, username);

                        try (ResultSet rs = pst.executeQuery()) {
                            if (rs.next()) {
                                accountExists = true;
                                String dbPassword = rs.getString("password");

                                if (dbPassword != null && dbPassword.equals(password)) {
                                    return new Object[]{
                                            "ADMIN", rs.getString("username"), null
                                    };
                                }
                            }
                        }
                    }

                    return new Object[]{
                            accountExists ? "WRONG_PASSWORD" : "NOT_FOUND",
                            null,
                            null
                    };

                } catch (SQLException ex) {
                    return new Object[]{"ERROR", ex.getMessage(), null};
                }
            }

            @Override
            protected void done() {
                dotsTimer.stop();
                loginButton.setEnabled(true);
                loginButton.setText("LOGIN");

                try {
                    Object[] result = get();
                    String status = (String) result[0];

                    if ("DB_FAIL".equals(status)) {
                        errorLabel.setText("Database connection failed!");
                        shakeCard();

                    } else if ("NOT_FOUND".equals(status)) {
                        errorLabel.setText("Account not found");
                        shakeCard();

                    } else if ("WRONG_PASSWORD".equals(status)) {
                        errorLabel.setText("Incorrect password");
                        shakeCard();

                    } else if ("ERROR".equals(status)) {
                        errorLabel.setText("Database error: " + result[1]);
                        shakeCard();

                    } else if ("STUDENT".equals(status)) {
                        String actualUsername = (String) result[1];

                        JOptionPane.showMessageDialog(
                                frame,
                                "Student login successful!",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        frame.dispose();
                        Dashboard.main(new String[]{actualUsername});

                    } else if ("ADMIN".equals(status)) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Admin login successful!",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        frame.dispose();
                        AdminDashboard.main(new String[]{});
                    }

                } catch (Exception ex) {
                    errorLabel.setText("Unexpected error occurred");
                    shakeCard();
                    ex.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    // ================= Custom UI Components =================

    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int shadowGap = 8;

            for (int i = 0; i < shadowGap; i++) {
                g2.setColor(new Color(70, 40, 130, 3));

                g2.fill(new RoundRectangle2D.Float(
                        i, i + 4,
                        getWidth() - i * 2,
                        getHeight() - i * 2 - 4,
                        radius, radius
                ));
            }

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0,
                    getWidth() - shadowGap,
                    getHeight() - shadowGap,
                    radius, radius
            ));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedField extends JTextField {
        RoundedField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new RoundedBorder(FIELD_BORDER, 12));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    setBorder(new RoundedBorder(PRIMARY, 12));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    setBorder(new RoundedBorder(FIELD_BORDER, 12));
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(FIELD_BG);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 12, 12
            ));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        RoundedPasswordField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new RoundedBorder(FIELD_BORDER, 12));
            setEchoChar('\u2022');

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    setBorder(new RoundedBorder(PRIMARY, 12));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    setBorder(new RoundedBorder(FIELD_BORDER, 12));
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(FIELD_BG);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 12, 12
            ));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(
                Component component,
                Graphics g,
                int x, int y, int width, int height) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));

            g2.draw(new RoundRectangle2D.Float(
                    x + 1, y + 1,
                    width - 3, height - 3,
                    radius, radius
            ));

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(8, 14, 8, 14);
        }
    }

    static class RoundedButton extends JButton {
        private boolean isHover = false;
        private boolean isPressed = false;

        RoundedButton(String text) {
            super(text);

            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(280, 46));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHover = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Color fill;

            if (!isEnabled()) {
                fill = new Color(190, 175, 225);
            } else if (isPressed) {
                fill = PRIMARY_DARK.darker();
            } else if (isHover) {
                fill = PRIMARY_DARK;
            } else {
                fill = PRIMARY;
            }

            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 14, 14
            ));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
