import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminPanel {

    // ---------------- Color Palette (matches the rest of Campus Care) ----------------
    private static final Color HEADER_TOP    = new Color(108, 63, 205);
    private static final Color HEADER_BOTTOM = new Color(124, 77, 220);
    private static final Color SIDEBAR_TOP    = new Color(46, 24, 92);
    private static final Color SIDEBAR_BOTTOM = new Color(30, 15, 68);
    private static final Color PRIMARY       = new Color(124, 77, 220);
    private static final Color PRIMARY_DARK  = new Color(96, 54, 190);
    private static final Color BG_LIGHT      = new Color(244, 242, 251);
    private static final Color TEXT_DARK     = new Color(45, 40, 60);
    private static final Color TEXT_MUTED    = new Color(120, 112, 140);
    private static final Color FIELD_BG      = new Color(247, 245, 252);
    private static final Color CARD_BORDER   = new Color(232, 228, 245);

    private static JFrame frame;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        frame = new JFrame("Campus Care - Admin Panel");
        frame.setSize(980, 640);
        frame.setMinimumSize(new Dimension(800, 560));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JButton complaintsButton = new JButton();
        JButton studentsButton = new JButton();
        JButton booksButton = new JButton();
        JButton logoutButton = new JButton();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        root.add(buildSidebar(complaintsButton, studentsButton, booksButton, logoutButton), BorderLayout.WEST);
        root.add(buildContent(complaintsButton, studentsButton, booksButton), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);

        // VIEW COMPLAINTS
        complaintsButton.addActionListener(e -> {

            JFrame complaintFrame =
                    new JFrame("Campus Care - Complaints");

            complaintFrame.setSize(850, 500);
            complaintFrame.setLocationRelativeTo(frame);

            JTextArea complaintArea =
                    new JTextArea();

            complaintArea.setEditable(false);
            complaintArea.setFont(
                    new Font("Monospaced", Font.PLAIN, 13)
            );

            JScrollPane scrollPane =
                    new JScrollPane(complaintArea);

            try {

                Connection con =
                        DatabaseConnection.getConnection();

                if (con == null) {

                    JOptionPane.showMessageDialog(
                            complaintFrame,
                            "Database connection failed!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

                String sql =
                        "SELECT complaint_id, student_name, " +
                                "room_number, category, complaint_text, status " +
                                "FROM complaints";

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery();

                StringBuilder data =
                        new StringBuilder();

                data.append(
                        "================ CAMPUS CARE COMPLAINTS ================\n\n"
                );

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    data.append("Complaint ID : ")
                            .append(rs.getInt("complaint_id"))
                            .append("\n");

                    data.append("Student Name : ")
                            .append(rs.getString("student_name"))
                            .append("\n");

                    data.append("Room Number  : ")
                            .append(rs.getString("room_number"))
                            .append("\n");

                    data.append("Category     : ")
                            .append(rs.getString("category"))
                            .append("\n");

                    data.append("Complaint    : ")
                            .append(rs.getString("complaint_text"))
                            .append("\n");

                    data.append("Status       : ")
                            .append(rs.getString("status"))
                            .append("\n");

                    data.append(
                            "--------------------------------------------------------\n"
                    );
                }

                if (!found) {
                    data.append("No complaints found.");
                }

                complaintArea.setText(data.toString());

                rs.close();
                pst.close();
                con.close();

                styleReportWindow(complaintFrame, "Complaints", complaintArea, scrollPane);
                complaintFrame.setVisible(true);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        complaintFrame,
                        "Database Error:\n"
                                + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                ex.printStackTrace();
            }
        });

        // VIEW STUDENTS
        studentsButton.addActionListener(e -> {

            try {

                Connection con =
                        DatabaseConnection.getConnection();

                String sql =
                        "SELECT id, name, username, cgpa, " +
                                "semester, attendance FROM students";

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery();

                StringBuilder data =
                        new StringBuilder();

                data.append(
                        "================ CAMPUS CARE STUDENTS ================\n\n"
                );

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    data.append("ID         : ")
                            .append(rs.getInt("id"))
                            .append("\n");

                    data.append("Name       : ")
                            .append(rs.getString("name"))
                            .append("\n");

                    data.append("Username   : ")
                            .append(rs.getString("username"))
                            .append("\n");

                    data.append("CGPA       : ")
                            .append(rs.getDouble("cgpa"))
                            .append("\n");

                    data.append("Semester   : ")
                            .append(rs.getInt("semester"))
                            .append("\n");

                    data.append("Attendance : ")
                            .append(rs.getDouble("attendance"))
                            .append("\n");

                    data.append(
                            "--------------------------------------------------------\n"
                    );
                }

                if (!found) {
                    data.append("No students found.");
                }

                JTextArea area =
                        new JTextArea(data.toString());

                area.setEditable(false);
                area.setFont(
                        new Font("Monospaced", Font.PLAIN, 13)
                );

                JScrollPane scroll =
                        new JScrollPane(area);

                showReportFrame("Students", area, scroll);

                rs.close();
                pst.close();
                con.close();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database Error:\n"
                                + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                ex.printStackTrace();
            }
        });

        // VIEW BOOKS
        booksButton.addActionListener(e -> {

            try {

                Connection con =
                        DatabaseConnection.getConnection();

                String sql =
                        "SELECT book_id, book_name, available " +
                                "FROM books";

                PreparedStatement pst =
                        con.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery();

                StringBuilder data =
                        new StringBuilder();

                data.append(
                        "================ CAMPUS CARE BOOKS ================\n\n"
                );

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    data.append("Book ID   : ")
                            .append(rs.getInt("book_id"))
                            .append("\n");

                    data.append("Book Name : ")
                            .append(rs.getString("book_name"))
                            .append("\n");

                    data.append("Available  : ")
                            .append(
                                    rs.getBoolean("available")
                                            ? "Yes"
                                            : "No"
                            )
                            .append("\n");

                    data.append(
                            "------------------------------------------------\n"
                    );
                }

                if (!found) {
                    data.append("No books found.");
                }

                JTextArea area =
                        new JTextArea(data.toString());

                area.setEditable(false);
                area.setFont(
                        new Font("Monospaced", Font.PLAIN, 13)
                );

                JScrollPane scroll =
                        new JScrollPane(area);

                showReportFrame("Books", area, scroll);

                rs.close();
                pst.close();
                con.close();

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database Error:\n"
                                + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );

                ex.printStackTrace();
            }
        });

        // LOGOUT
        logoutButton.addActionListener(e -> {
            frame.dispose();
            LoginPage.main(new String[]{});
        });
    }

    // ================= SIDEBAR (dark violet, persistent nav — distinct from the student-facing pages) =================
    private static JPanel buildSidebar(JButton complaintsButton, JButton studentsButton,
                                       JButton booksButton, JButton logoutButton) {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, SIDEBAR_TOP, 0, getHeight(), SIDEBAR_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(230, 100));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(28, 24, 30, 24));

        JLabel brand = new JLabel("CAMPUS CARE");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandSub = new JLabel("Admin Console");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSub.setForeground(new Color(200, 190, 235));
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        top.add(brand);
        top.add(Box.createRigidArea(new Dimension(0, 2)));
        top.add(brandSub);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        nav.add(new SidebarItem("\uD83D\uDDC2", "Complaints", e -> complaintsButton.doClick()));
        nav.add(Box.createRigidArea(new Dimension(0, 4)));
        nav.add(new SidebarItem("\uD83C\uDF93", "Students", e -> studentsButton.doClick()));
        nav.add(Box.createRigidArea(new Dimension(0, 4)));
        nav.add(new SidebarItem("\uD83D\uDCDA", "Books", e -> booksButton.doClick()));

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 12, 20, 12));
        bottom.add(new JSeparator() {{
            setForeground(new Color(255, 255, 255, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        }});
        bottom.add(Box.createRigidArea(new Dimension(0, 10)));
        bottom.add(new SidebarItem("\u23FB", "Logout", e -> logoutButton.doClick()));

        JPanel navWrap = new JPanel(new BorderLayout());
        navWrap.setOpaque(false);
        navWrap.add(nav, BorderLayout.NORTH);

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(navWrap, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    // ================= CONTENT: welcome + quick-access rows (list style, not card-grid) =================
    private static JPanel buildContent(JButton complaintsButton, JButton studentsButton, JButton booksButton) {
        JPanel wrapper = new JPanel();
        wrapper.setBackground(BG_LIGHT);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(40, 46, 30, 46));

        JLabel heading = new JLabel("Welcome, Admin");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subheading = new JLabel("Pick a section from the sidebar, or jump in below.");
        subheading.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subheading.setForeground(TEXT_MUTED);
        subheading.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(heading);
        wrapper.add(Box.createRigidArea(new Dimension(0, 4)));
        wrapper.add(subheading);
        wrapper.add(Box.createRigidArea(new Dimension(0, 30)));

        wrapper.add(new OverviewRow(
                "\uD83D\uDDC2", new Color(230, 126, 34),
                "Complaints", "See all hostel & general complaints submitted by students.",
                e -> complaintsButton.doClick()
        ));
        wrapper.add(Box.createRigidArea(new Dimension(0, 14)));

        wrapper.add(new OverviewRow(
                "\uD83C\uDF93", new Color(41, 121, 255),
                "Students", "Browse student records, CGPA, semester & attendance.",
                e -> studentsButton.doClick()
        ));
        wrapper.add(Box.createRigidArea(new Dimension(0, 14)));

        wrapper.add(new OverviewRow(
                "\uD83D\uDCDA", new Color(26, 188, 156),
                "Books", "Check library book availability at a glance.",
                e -> booksButton.doClick()
        ));

        wrapper.add(Box.createVerticalGlue());

        JLabel footerLabel = new JLabel("Campus Care \u00A9 2026 | Admin Console");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(TEXT_MUTED);
        footerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(footerLabel);

        return wrapper;
    }

    // ================= Report window styling (presentation only) =================

    /** Builds a brand-new styled report frame around an already-populated JTextArea. */
    private static void showReportFrame(String title, JTextArea area, JScrollPane scroll) {
        JFrame reportFrame = new JFrame("Campus Care - " + title);
        reportFrame.setSize(800, 520);
        reportFrame.setLocationRelativeTo(frame);
        reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        styleReportWindow(reportFrame, title, area, scroll);
        reportFrame.setVisible(true);
    }

    /** Applies the shared violet-header + white-card look to a report frame (Complaints/Students/Books). */
    private static void styleReportWindow(JFrame reportFrame, String title, JTextArea area, JScrollPane scroll) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, HEADER_TOP, getWidth(), getHeight(), HEADER_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(100, 66));
        header.setBorder(BorderFactory.createEmptyBorder(0, 26, 0, 20));

        JLabel headerTitle = new JLabel(title);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(Color.WHITE);
        header.add(headerTitle, BorderLayout.WEST);

        JPanel closeWrap = new JPanel(new GridBagLayout());
        closeWrap.setOpaque(false);
        PillOutlineButton closeBtn = new PillOutlineButton("\u2715 CLOSE");
        closeBtn.addActionListener(e -> reportFrame.dispose());
        closeWrap.add(closeBtn);
        header.add(closeWrap, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel(16, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        area.setBackground(FIELD_BG);
        area.setForeground(TEXT_DARK);
        area.setCaretPosition(0);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FIELD_BG);

        card.add(scroll, BorderLayout.CENTER);

        JPanel cardWrap = new JPanel(new BorderLayout());
        cardWrap.setBackground(BG_LIGHT);
        cardWrap.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
        cardWrap.add(card, BorderLayout.CENTER);

        root.add(cardWrap, BorderLayout.CENTER);

        reportFrame.setContentPane(root);
    }

    // =====================================================================
    //  Custom UI Components
    // =====================================================================

    /** A dark-sidebar navigation row: icon + label, highlights on hover. */
    static class SidebarItem extends JPanel {
        private boolean isHover = false;

        SidebarItem(String iconText, String label, ActionListener onClick) {
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(BorderFactory.createEmptyBorder(11, 12, 11, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel icon = new JLabel(iconText);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

            JLabel text = new JLabel(label);
            text.setFont(new Font("Segoe UI", Font.BOLD, 13));
            text.setForeground(Color.WHITE);

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            left.setOpaque(false);
            left.add(icon);
            left.add(Box.createRigidArea(new Dimension(10, 0)));
            left.add(text);

            add(left, BorderLayout.WEST);

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
                    onClick.actionPerformed(null);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isHover) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** A full-width overview row for the content area: icon badge, title+description, and a chevron. */
    static class OverviewRow extends JPanel {
        private boolean isHover = false;
        private final Color accent;

        OverviewRow(String iconText, Color accent, String title, String description, ActionListener onOpen) {
            this.accent = accent;
            setOpaque(false);
            setLayout(new BorderLayout(18, 0));
            setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            JLabel badge = new JLabel(iconText, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            badge.setPreferredSize(new Dimension(48, 48));

            JPanel textBlock = new JPanel();
            textBlock.setOpaque(false);
            textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            titleLabel.setForeground(TEXT_DARK);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descLabel.setForeground(TEXT_MUTED);
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            textBlock.add(Box.createVerticalGlue());
            textBlock.add(titleLabel);
            textBlock.add(Box.createRigidArea(new Dimension(0, 3)));
            textBlock.add(descLabel);
            textBlock.add(Box.createVerticalGlue());

            JLabel chevron = new JLabel("\u2192");
            chevron.setFont(new Font("Segoe UI", Font.BOLD, 18));
            chevron.setForeground(accent);

            add(badge, BorderLayout.WEST);
            add(textBlock, BorderLayout.CENTER);
            add(chevron, BorderLayout.EAST);

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
                    onOpen.actionPerformed(null);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

            g2.setColor(isHover ? accent : CARD_BORDER);
            g2.setStroke(new BasicStroke(isHover ? 1.8f : 1f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 14, 14));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** White rounded panel with a soft drop shadow. */
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int shadowGap = 6;
            for (int i = 0; i < shadowGap; i++) {
                g2.setColor(new Color(70, 40, 130, 3));
                g2.fill(new RoundRectangle2D.Float(
                        i, i + 3, getWidth() - i * 2, getHeight() - i * 2 - 3, radius, radius));
            }

            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth() - shadowGap, getHeight() - shadowGap, radius, radius));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** White-outlined pill button — used for Logout (header) and Close (report windows). */
    static class PillOutlineButton extends JButton {
        private boolean isHover = false;

        PillOutlineButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 34));

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
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isHover) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
            }

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, getHeight(), getHeight()));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}