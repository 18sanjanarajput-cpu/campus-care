import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TeacherComplaint {

    // ---------------- Color Palette (fresh teal — deliberately different from the rest of the app) ----------------
    private static final Color PANEL_TOP    = new Color(0, 121, 107);
    private static final Color PANEL_BOTTOM = new Color(0, 90, 82);
    private static final Color ACCENT       = new Color(0, 150, 136);
    private static final Color ACCENT_DARK  = new Color(0, 121, 107);
    private static final Color TEXT_DARK    = new Color(30, 42, 40);
    private static final Color TEXT_MUTED   = new Color(110, 125, 122);
    private static final Color FIELD_BG     = new Color(240, 248, 246);
    private static final Color FIELD_BORDER = new Color(190, 212, 208);

    private static JFrame frame;

    private static RoundedField nameField;
    private static RoundedField cgpaField;
    private static RoundedField teacherField;
    private static RoundedTextArea complaintArea;
    private static JLabel fileLabel;
    private static final File[] selectedFile = {null};

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        frame = new JFrame("Campus Care - Teacher Complaint");
        frame.setSize(1100, 750);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());

        frame.setContentPane(root);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    // ================= LEFT: info panel (fills the space, no dead void) =================
    private static JPanel buildLeftPanel() {
        JPanel left = new JPanel(new GridBagLayout()) {
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

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel badge = new JLabel("\uD83C\uDF93", SwingConstants.CENTER) {
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

        JLabel title = new JLabel("TEACHER COMPLAINT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Only for students with CGPA 7.5 or above");
        subtitle.setForeground(new Color(210, 235, 230));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(badge);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 34)));

        content.add(infoLine("Your CGPA must be 7.5 or above to submit"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("A CGPA proof (screenshot or PDF) is required"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("Describe the issue clearly with relevant details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 55, 0, 55);
        left.add(content, gbc);

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
        label.setForeground(new Color(220, 240, 236));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        row.add(dot);
        row.add(label);
        return row;
    }

    // ================= RIGHT: the full form, all fields on one page =================
    private static JPanel buildRightPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(400, 640));

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
        form.add(Box.createRigidArea(new Dimension(0, 26)));

        form.add(fieldLabel("STUDENT NAME"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        nameField = new RoundedField();
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(400, 44));
        form.add(nameField);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        form.add(fieldLabel("CGPA"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        cgpaField = new RoundedField();
        cgpaField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cgpaField.setMaximumSize(new Dimension(400, 44));
        form.add(cgpaField);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        form.add(fieldLabel("CGPA PROOF"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel proofRow = new JPanel(new BorderLayout(12, 0));
        proofRow.setOpaque(false);
        proofRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        proofRow.setMaximumSize(new Dimension(400, 44));

        RoundedButton proofButton = new RoundedButton("CHOOSE FILE");
        proofButton.setPreferredSize(new Dimension(160, 42));

        fileLabel = new JLabel("No file selected");
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileLabel.setForeground(TEXT_MUTED);

        proofRow.add(proofButton, BorderLayout.WEST);
        proofRow.add(fileLabel, BorderLayout.CENTER);
        form.add(proofRow);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        proofButton.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();

            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {

                selectedFile[0] = fileChooser.getSelectedFile();

                fileLabel.setText(selectedFile[0].getName());
            }
        });

        form.add(fieldLabel("TEACHER NAME"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        teacherField = new RoundedField();
        teacherField.setAlignmentX(Component.LEFT_ALIGNMENT);
        teacherField.setMaximumSize(new Dimension(400, 44));
        form.add(teacherField);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        form.add(fieldLabel("COMPLAINT"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        complaintArea = new RoundedTextArea();
        complaintArea.setRows(5);
        JScrollPane scrollPane = new JScrollPane(complaintArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(400, 130));
        form.add(scrollPane);
        form.add(Box.createRigidArea(new Dimension(0, 24)));

        RoundedButton submitButton = new RoundedButton("SUBMIT COMPLAINT");
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(400, 46));
        submitButton.setPreferredSize(new Dimension(400, 46));
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

        String name = nameField.getText().trim();
        String cgpaText = cgpaField.getText().trim();
        String teacher = teacherField.getText().trim();
        String complaint = complaintArea.getText().trim();

        // Check empty fields
        if (name.isEmpty()
                || cgpaText.isEmpty()
                || teacher.isEmpty()
                || complaint.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please fill all the details!",
                    "Incomplete Details",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            double cgpa = Double.parseDouble(cgpaText);

            // CGPA requirement: 7.5 OR ABOVE
            if (cgpa < 7.5) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Access Restricted!\n\n"
                                + "Teacher complaint can only be submitted "
                                + "by students with CGPA 7.5 or above.",
                        "CGPA Requirement",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Proof required
            if (selectedFile[0] == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please upload your CGPA proof!",
                        "CGPA Proof Required",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // SQL query
            String sql = "INSERT INTO complaints "
                    + "(username, category, complaint_text, status, "
                    + "student_name, room_number) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            // Database connection
            try (Connection con = DatabaseConnection.getConnection()) {

                if (con == null) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Database connection failed!",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    return;
                }

                try (PreparedStatement pst = con.prepareStatement(sql)) {

                    pst.setString(1, name);
                    pst.setString(2, "Teacher Complaint");
                    pst.setString(
                            3,
                            "Teacher: " + teacher
                                    + "\nComplaint: " + complaint
                                    + "\nCGPA: " + cgpa
                                    + "\nCGPA Proof: "
                                    + selectedFile[0].getName()
                    );
                    pst.setString(4, "Pending");
                    pst.setString(5, name);
                    pst.setString(6, "N/A");

                    int result = pst.executeUpdate();

                    if (result > 0) {

                        JOptionPane.showMessageDialog(
                                frame,
                                "Teacher Complaint Submitted Successfully! \uD83D\uDE0A\n\n"
                                        + "Student: " + name
                                        + "\nCGPA: " + cgpa
                                        + "\nTeacher: " + teacher
                                        + "\nStatus: Pending",
                                "Campus Care",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        // Clear form
                        nameField.setText("");
                        cgpaField.setText("");
                        teacherField.setText("");
                        complaintArea.setText("");
                        fileLabel.setText("No file selected");
                        selectedFile[0] = null;
                    }
                }
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid CGPA.\nExample: 8.2",
                    "Invalid CGPA",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Database Error:\n" + ex.getMessage(),
                    "Error",
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
            g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
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
            g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(isFocusOwner() ? ACCENT : FIELD_BORDER);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(new java.awt.geom.RoundRectangle2D.Float(1, 1, getWidth() - 3, getHeight() - 3, 12, 12));
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
            g2.draw(new java.awt.geom.RoundRectangle2D.Float(x + 1, y + 1, w - 3, h - 3, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(9, 14, 9, 14);
        }
    }

    /** Solid rounded button with hover state — used for Choose File / Submit. */
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
            g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}