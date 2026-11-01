import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class HostelComplaint {

    // ---------------- Color Palette ----------------
    private static final Color PANEL_TOP     = new Color(108, 63, 205);
    private static final Color PANEL_BOTTOM  = new Color(76, 40, 165);
    private static final Color ACCENT        = new Color(124, 77, 220);
    private static final Color ACCENT_DARK   = new Color(96, 54, 190);
    private static final Color TEXT_DARK     = new Color(40, 35, 55);
    private static final Color TEXT_MUTED    = new Color(115, 108, 135);
    private static final Color FIELD_BG      = new Color(247, 245, 252);
    private static final Color FIELD_BORDER  = new Color(196, 184, 228);

    private static JFrame frame;

    private static RoundedField studentNameField;
    private static RoundedField roomNumberField;
    private static JComboBox<String> complaintTypeBox;
    private static RoundedTextArea complaintArea;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        frame = new JFrame("Campus Care - Hostel Complaint");
        frame.setSize(1020, 680);
        frame.setMinimumSize(new Dimension(820, 600));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ================= LEFT: violet info panel (fills the space, no dead void) =================
    private static JPanel buildLeftPanel() {
        JPanel left = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PANEL_TOP, getWidth(), getHeight(), PANEL_BOTTOM));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillOval(-70, -70, 240, 240);
                g2.fillOval(getWidth() - 140, getHeight() - 170, 280, 280);

                g2.dispose();
            }
        };

        // ---- Top bar: back button, pinned top-right ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 24));

        PillOutlineButton back = new PillOutlineButton("\u2190 BACK");
        back.addActionListener(e -> {
            frame.dispose();
            try {
                Dashboard.main(new String[]{});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Dashboard open nahi ho raha.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JPanel backWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        backWrap.setOpaque(false);
        backWrap.add(back);
        topBar.add(backWrap, BorderLayout.EAST);
        left.add(topBar, BorderLayout.NORTH);

        // ---- Center: title + info lines, vertically centered ----
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel badge = new JLabel("\uD83C\uDFE0", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        badge.setPreferredSize(new Dimension(58, 58));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("HOSTEL COMPLAINT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Raise your hostel-related issue");
        subtitle.setForeground(new Color(222, 213, 250));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(badge);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 34)));

        content.add(infoLine("Electricity, water, cleanliness, food & maintenance issues"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("Warden usually responds within 24\u201348 hours"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("You can track the status from your Dashboard"));

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 55, 40, 55);
        centerWrap.add(content, gbc);
        left.add(centerWrap, BorderLayout.CENTER);

        return left;
    }

    private static JPanel infoLine(String text) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(320, 40));

        JLabel dot = new JLabel("\u2713");
        dot.setForeground(new Color(190, 240, 220));
        dot.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel label = new JLabel("<html><div style='width:230px;'>" + text + "</div></html>");
        label.setForeground(new Color(228, 220, 250));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        row.add(dot);
        row.add(label);
        return row;
    }

    // ================= RIGHT: the actual form =================
    private static JPanel buildRightPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(340, 600));

        JLabel heading = new JLabel("Complaint Details");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Fill in the details below and submit");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(heading);
        form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(hint);
        form.add(Box.createRigidArea(new Dimension(0, 28)));

        form.add(fieldLabel("STUDENT NAME"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        studentNameField = new RoundedField();
        studentNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        studentNameField.setMaximumSize(new Dimension(340, 44));
        form.add(studentNameField);
        form.add(Box.createRigidArea(new Dimension(0, 18)));

        form.add(fieldLabel("ROOM NUMBER"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        roomNumberField = new RoundedField();
        roomNumberField.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomNumberField.setMaximumSize(new Dimension(340, 44));
        form.add(roomNumberField);
        form.add(Box.createRigidArea(new Dimension(0, 18)));

        form.add(fieldLabel("COMPLAINT TYPE"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        String[] complaintTypes = {
                "Electricity",
                "Water",
                "Cleanliness",
                "Food",
                "Room Maintenance",
                "Other"
        };
        complaintTypeBox = new JComboBox<>(complaintTypes);
        complaintTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        complaintTypeBox.setBackground(FIELD_BG);
        complaintTypeBox.setBorder(new RoundedBorder(FIELD_BORDER, 12));
        complaintTypeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        complaintTypeBox.setMaximumSize(new Dimension(340, 44));
        form.add(complaintTypeBox);
        form.add(Box.createRigidArea(new Dimension(0, 18)));

        form.add(fieldLabel("COMPLAINT"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        complaintArea = new RoundedTextArea();
        complaintArea.setRows(5);
        JScrollPane scrollPane = new JScrollPane(complaintArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(340, 130));
        form.add(scrollPane);
        form.add(Box.createRigidArea(new Dimension(0, 26)));

        RoundedButton submitButton = new RoundedButton("SUBMIT COMPLAINT");
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(340, 46));
        submitButton.setPreferredSize(new Dimension(340, 46));
        form.add(submitButton);

        submitButton.addActionListener(e -> submitComplaint());

        right.add(form, new GridBagConstraints());
        return right;
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // =====================================================
    // SUBMIT COMPLAINT  (logic 100% unchanged from your code)
    // =====================================================

    private static void submitComplaint() {

        String studentName =
                studentNameField
                        .getText()
                        .trim();

        String roomNumber =
                roomNumberField
                        .getText()
                        .trim();

        String complaintType =
                complaintTypeBox
                        .getSelectedItem()
                        .toString();

        String complaint =
                complaintArea
                        .getText()
                        .trim();

        // ---------------- VALIDATION ----------------

        if (studentName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter Student Name.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (roomNumber.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter Room Number.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (complaint.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter your complaint.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // =================================================
        // DATABASE CONNECTION
        // =================================================

        String sql =
                "INSERT INTO complaints " +
                        "(username, category, complaint_text, status, " +
                        "student_name, room_number) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try {

            Connection con =
                    DatabaseConnection.getConnection();

            if (con == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database connection failed.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setString(
                    1,
                    "sanjana"
            );

            pst.setString(
                    2,
                    complaintType
            );

            pst.setString(
                    3,
                    complaint
            );

            pst.setString(
                    4,
                    "Pending"
            );

            pst.setString(
                    5,
                    studentName
            );

            pst.setString(
                    6,
                    roomNumber
            );

            pst.executeUpdate();

            pst.close();
            con.close();

            // ---------------- SUCCESS ----------------

            JOptionPane.showMessageDialog(
                    frame,
                    "Hostel Complaint Submitted Successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Clear fields

            studentNameField.setText("");

            roomNumberField.setText("");

            complaintArea.setText("");

            complaintTypeBox.setSelectedIndex(0);

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Complaint save nahi hui.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =====================================================================
    //  Custom UI Components (visual only — no business logic here)
    // =====================================================================

    /** Rounded, flat-style text field with a strong-contrast border and focus highlight. */
    static class RoundedField extends JTextField {
        RoundedField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(new RoundedBorder(FIELD_BORDER, 12));
            setPreferredSize(new Dimension(280, 44));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    setBorder(new RoundedBorder(ACCENT, 12));
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Rounded multi-line text area, styled like RoundedField. */
    static class RoundedTextArea extends JTextArea {
        RoundedTextArea() {
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(isFocusOwner() ? ACCENT : FIELD_BORDER);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Rounded-rect border with strong, clearly visible stroke color. */
    static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, w - 3, h - 3, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(9, 14, 9, 14);
        }
    }

    /** Solid rounded button with hover state — used for Submit. */
    static class RoundedButton extends JButton {
        private boolean isHover = false;

        RoundedButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

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

            g2.setColor(isHover ? ACCENT_DARK : ACCENT);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** White-outlined pill button — used for Back, on the violet panel. */
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
            setPreferredSize(new Dimension(90, 32));

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