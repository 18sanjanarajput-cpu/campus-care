
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Dashboard {

    private static final Color PRIMARY = new Color(124, 77, 220);
    private static final Color PRIMARY_DARK = new Color(96, 54, 190);
    private static final Color BG_LIGHT = new Color(244, 242, 251);
    private static final Color TEXT_DARK = new Color(45, 40, 60);
    private static final Color TEXT_MUTED = new Color(120, 112, 140);
    private static final Color CARD_BORDER = new Color(232, 228, 245);

    private static JFrame frame;
    private static String currentUsername = "";

    public static void main(String[] args) {

        currentUsername = "";

        if (args != null && args.length > 0 && args[0] != null) {
            currentUsername = args[0].trim();
        }

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Campus Care - Dashboard");
            frame.setSize(950, 680);
            frame.setMinimumSize(new Dimension(760, 560));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(BG_LIGHT);

            root.add(buildHeader(), BorderLayout.NORTH);
            root.add(buildContent(), BorderLayout.CENTER);
            root.add(buildFooter(), BorderLayout.SOUTH);

            frame.setContentPane(root);
            frame.setVisible(true);
        });
    }

    // ================= HEADER =================

    private static JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setPaint(new GradientPaint(
                        0, 0, PRIMARY_DARK,
                        getWidth(), getHeight(), PRIMARY
                ));

                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        header.setPreferredSize(new Dimension(100, 100));
        header.setBorder(
                BorderFactory.createEmptyBorder(0, 36, 0, 30)
        );

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(
                new BoxLayout(titleBlock, BoxLayout.Y_AXIS)
        );

        JLabel title = new JLabel("CAMPUS CARE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Student Dashboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(230, 225, 250));

        titleBlock.add(Box.createVerticalGlue());
        titleBlock.add(title);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 3)));
        titleBlock.add(subtitle);
        titleBlock.add(Box.createVerticalGlue());

        header.add(titleBlock, BorderLayout.WEST);

        JButton logout = new JButton("LOGOUT");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.setContentAreaFilled(false);
        logout.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.addActionListener(e -> handleLogout());

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.add(logout);

        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ================= CONTENT =================

    private static JPanel buildContent() {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(
                BorderFactory.createEmptyBorder(26, 40, 26, 40)
        );

        JLabel welcome = new JLabel(
                "Welcome back! Here's what you can do today."
        );
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcome.setForeground(TEXT_MUTED);
        welcome.setBorder(
                BorderFactory.createEmptyBorder(0, 2, 20, 0)
        );

        wrapper.add(welcome, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 24, 24));
        grid.setOpaque(false);

        grid.add(new FeatureCard(
                "SP", new Color(41, 121, 255),
                "Student Profile",
                "View and update your personal and academic details.",
                e -> openModule(
                        "StudentProfile",
                        "Student Profile could not be opened."
                )
        ));

        grid.add(new FeatureCard(
                "LM", new Color(26, 188, 156),
                "Library Management",
                "Browse, issue and track your borrowed books.",
                e -> openModule(
                        "LibraryManagement",
                        "Library Management could not be opened."
                )
        ));

        grid.add(new FeatureCard(
                "HC", new Color(230, 126, 34),
                "Hostel Complaint",
                "Raise and track hostel-related complaints.",
                e -> openModule(
                        "HostelComplaint",
                        "Hostel Complaint could not be opened."
                )
        ));

        grid.add(new FeatureCard(
                "TC", new Color(155, 89, 182),
                "Teacher Complaint",
                "Report academic issues directly to faculty.",
                e -> openModule(
                        "TeacherComplaint",
                        "Teacher Complaint could not be opened."
                )
        ));

        grid.add(new FeatureCard(
                "UM", new Color(231, 76, 60),
                "Update Message",
                "Check your complaint status and admin replies.",
                e -> showComplaintUpdates()
        ));

        grid.add(new FeatureCard(
                "AP", new Color(52, 73, 94),
                "Admin Panel",
                "Open the campus administration panel.",
                e -> openAdminPanel()
        ));

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    // ================= STUDENT COMPLAINT UPDATES =================

    private static void showComplaintUpdates() {

        if (currentUsername == null || currentUsername.isEmpty()) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Student username is missing. Please log in again.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql =
                "SELECT complaint_id, category, complaint_text, " +
                        "status, assigned_to, admin_update " +
                        "FROM complaints " +
                        "WHERE LOWER(TRIM(username)) = LOWER(TRIM(?)) " +
                        "ORDER BY complaint_id DESC";

        StringBuilder message = new StringBuilder();
        message.append("YOUR COMPLAINT UPDATES\n\n");

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Database connection failed.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, currentUsername);

                try (ResultSet rs = ps.executeQuery()) {

                    boolean found = false;

                    while (rs.next()) {
                        found = true;

                        message.append("Complaint ID: ")
                                .append(rs.getInt("complaint_id"))
                                .append("\n");

                        message.append("Category: ")
                                .append(nullToText(rs.getString("category")))
                                .append("\n");

                        message.append("Complaint: ")
                                .append(nullToText(rs.getString("complaint_text")))
                                .append("\n");

                        message.append("Status: ")
                                .append(nullToText(rs.getString("status")))
                                .append("\n");

                        message.append("Assigned To: ")
                                .append(nullToText(rs.getString("assigned_to")))
                                .append("\n");

                        message.append("Admin Message: ")
                                .append(nullToText(rs.getString("admin_update")))
                                .append("\n");

                        message.append(
                                "\n--------------------------------\n\n"
                        );
                    }

                    if (!found) {
                        message.append(
                                "No complaint records found in the database."
                        );
                    }
                }
            }

            JTextArea area = new JTextArea(message.toString(), 18, 45);
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            area.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(area);
            scrollPane.setPreferredSize(new Dimension(550, 350));

            JOptionPane.showMessageDialog(
                    frame,
                    scrollPane,
                    "My Complaint Updates",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to load complaint updates:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static String nullToText(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not available"
                : value;
    }

    // ================= ADMIN PANEL =================

    private static void openAdminPanel() {
        try {
            AdminPanel.main(new String[]{});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to open Admin Panel.\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= FOOTER =================

    private static JPanel buildFooter() {

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_LIGHT);
        footer.setBorder(
                BorderFactory.createMatteBorder(
                        1, 0, 0, 0, CARD_BORDER
                )
        );

        JLabel footerLabel = new JLabel(
                "Campus Care © 2026 | Student Support System",
                SwingConstants.CENTER
        );

        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_MUTED);
        footerLabel.setBorder(
                BorderFactory.createEmptyBorder(12, 0, 12, 0)
        );

        footer.add(footerLabel, BorderLayout.CENTER);
        return footer;
    }

    // ================= NAVIGATION =================

    private static void openModule(
            String className,
            String errorMessage
    ) {
        try {
            switch (className) {

                case "StudentProfile":
                    StudentProfile.main(new String[]{});
                    break;

                case "LibraryManagement":
                    LibraryManagement.main(new String[]{});
                    break;

                case "HostelComplaint":
                    HostelComplaint.main(new String[]{});
                    break;

                case "TeacherComplaint":
                    TeacherComplaint.main(new String[]{});
                    break;

                default:
                    JOptionPane.showMessageDialog(
                            frame,
                            "Requested module was not found.",
                            "Module Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    break;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    errorMessage + "\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= LOGOUT =================

    private static void handleLogout() {

        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
            LoginPage.main(new String[]{});
        }
    }

    // ================= FEATURE CARD =================

    static class FeatureCard extends JPanel {

        private boolean isHover = false;
        private final Color accent;

        FeatureCard(
                String iconText,
                Color accent,
                String title,
                String description,
                ActionListener onOpen
        ) {
            this.accent = accent;

            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(
                    BorderFactory.createEmptyBorder(22, 18, 20, 18)
            );
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel badge = new JLabel(iconText, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );
                    g2.setColor(accent);
                    g2.fillRoundRect(
                            0, 0, getWidth(), getHeight(), 14, 14
                    );
                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            badge.setForeground(Color.WHITE);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 15));
            badge.setPreferredSize(new Dimension(52, 52));

            JPanel top = new JPanel(
                    new FlowLayout(FlowLayout.CENTER, 0, 0)
            );
            top.setOpaque(false);
            top.add(badge);

            JPanel textBlock = new JPanel();
            textBlock.setOpaque(false);
            textBlock.setLayout(
                    new BoxLayout(textBlock, BoxLayout.Y_AXIS)
            );
            textBlock.setBorder(
                    BorderFactory.createEmptyBorder(14, 0, 10, 0)
            );

            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
            titleLabel.setForeground(TEXT_DARK);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel descLabel = new JLabel(
                    "<html><div style='text-align:center;width:170px;'>"
                            + description + "</div></html>"
            );
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descLabel.setForeground(TEXT_MUTED);
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            textBlock.add(titleLabel);
            textBlock.add(Box.createRigidArea(new Dimension(0, 6)));
            textBlock.add(descLabel);

            JLabel openPill = new JLabel("OPEN →");
            openPill.setFont(new Font("Segoe UI", Font.BOLD, 12));
            openPill.setForeground(accent);

            JPanel bottom = new JPanel(
                    new FlowLayout(FlowLayout.CENTER, 0, 0)
            );
            bottom.setOpaque(false);
            bottom.add(openPill);

            JPanel center = new JPanel(new BorderLayout());
            center.setOpaque(false);
            center.add(textBlock, BorderLayout.CENTER);
            center.add(bottom, BorderLayout.SOUTH);

            add(top, BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);

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
                public void mouseClicked(MouseEvent e) {
                    onOpen.actionPerformed(
                            new ActionEvent(
                                    FeatureCard.this,
                                    ActionEvent.ACTION_PERFORMED,
                                    title
                            )
                    );
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

            int shadowGap = 6;

            g2.setColor(new Color(70, 40, 130, 12));
            g2.fillRoundRect(
                    3, 4, getWidth() - shadowGap,
                    getHeight() - shadowGap, 18, 18
            );

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(
                    0, 0, getWidth() - shadowGap,
                    getHeight() - shadowGap, 18, 18
            );

            g2.setColor(isHover ? accent : CARD_BORDER);
            g2.setStroke(new BasicStroke(isHover ? 2f : 1f));
            g2.drawRoundRect(
                    1, 1, getWidth() - shadowGap - 2,
                    getHeight() - shadowGap - 2, 18, 18
            );

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
