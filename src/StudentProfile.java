import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentProfile {

    // ---------------- Color Palette (warm amber — a distinct, personal identity) ----------------
    private static final Color PANEL_TOP    = new Color(196, 108, 24);
    private static final Color PANEL_BOTTOM = new Color(150, 78, 14);
    private static final Color ACCENT       = new Color(219, 130, 40);
    private static final Color ACCENT_DARK  = new Color(180, 100, 24);
    private static final Color TEXT_DARK    = new Color(40, 34, 28);
    private static final Color TEXT_MUTED   = new Color(128, 116, 104);
    private static final Color FIELD_BG     = new Color(252, 246, 240);
    private static final Color FIELD_BORDER = new Color(230, 208, 184);

    private static JFrame frame;

    private static RoundedField nameField;
    private static RoundedField rollField;
    private static RoundedField courseField;
    private static JComboBox<String> semesterBox;
    private static RoundedField cgpaField;
    private static JLabel cgpaFileLabel;
    private static RoundedTextArea achievementArea;
    private static JLabel achievementFileLabel;

    private static final File[] cgpaFile = {null};
    private static final File[] achievementFile = {null};

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        frame = new JFrame("Campus Care - Student Profile");
        frame.setSize(1100, 800);
        frame.setMinimumSize(new Dimension(900, 700));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());

        frame.setContentPane(root);
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

        JLabel title = new JLabel("STUDENT PROFILE");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Manage your academic details");
        subtitle.setForeground(new Color(250, 230, 210));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(badge);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 34)));

        content.add(infoLine("Keep your name, roll number & course up to date"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("Upload proof for your CGPA verification"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("Add achievements with certificates to stand out"));

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
        dot.setForeground(new Color(255, 224, 190));
        dot.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel label = new JLabel("<html><div style='width:230px;'>" + text + "</div></html>");
        label.setForeground(new Color(250, 232, 214));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        row.add(dot);
        row.add(label);
        return row;
    }

    // ================= RIGHT: the full profile form =================
    private static JPanel buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JPanel centeredWrap = new JPanel();
        centeredWrap.setOpaque(false);
        centeredWrap.setLayout(new BoxLayout(centeredWrap, BoxLayout.Y_AXIS));
        centeredWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        centeredWrap.setMaximumSize(new Dimension(440, 2000));

        JLabel heading = new JLabel("Your Profile");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        centeredWrap.add(heading);
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 22)));

        centeredWrap.add(fieldLabel("STUDENT NAME"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));
        nameField = new RoundedField();
        addField(centeredWrap, nameField);

        centeredWrap.add(fieldLabel("ROLL NUMBER"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));
        rollField = new RoundedField();
        addField(centeredWrap, rollField);

        centeredWrap.add(fieldLabel("COURSE"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));
        courseField = new RoundedField();
        addField(centeredWrap, courseField);

        centeredWrap.add(fieldLabel("SEMESTER"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));

        String[] semesters = {
                "1st Semester", "2nd Semester",
                "3rd Semester", "4th Semester",
                "5th Semester", "6th Semester",
                "7th Semester", "8th Semester"
        };
        semesterBox = new JComboBox<>(semesters);
        semesterBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        semesterBox.setBackground(FIELD_BG);
        semesterBox.setBorder(new RoundedBorder(FIELD_BORDER, 12));
        semesterBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        semesterBox.setMaximumSize(new Dimension(440, 44));
        centeredWrap.add(semesterBox);
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 16)));

        centeredWrap.add(fieldLabel("CGPA"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));
        cgpaField = new RoundedField();
        addField(centeredWrap, cgpaField);

        centeredWrap.add(fieldLabel("CGPA PROOF"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel cgpaProofRow = new JPanel(new BorderLayout(12, 0));
        cgpaProofRow.setOpaque(false);
        cgpaProofRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cgpaProofRow.setMaximumSize(new Dimension(440, 44));

        RoundedButton cgpaProofButton = new RoundedButton("UPLOAD PROOF");
        cgpaProofButton.setPreferredSize(new Dimension(150, 42));

        cgpaFileLabel = new JLabel("No file selected");
        cgpaFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cgpaFileLabel.setForeground(TEXT_MUTED);

        cgpaProofRow.add(cgpaProofButton, BorderLayout.WEST);
        cgpaProofRow.add(cgpaFileLabel, BorderLayout.CENTER);
        centeredWrap.add(cgpaProofRow);
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 16)));

        cgpaProofButton.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();

            int result =
                    chooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {

                cgpaFile[0] =
                        chooser.getSelectedFile();

                cgpaFileLabel.setText(
                        cgpaFile[0].getName()
                );
            }
        });

        centeredWrap.add(fieldLabel("ACHIEVEMENT (OPTIONAL)"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));

        achievementArea = new RoundedTextArea();
        JScrollPane achievementScroll = new JScrollPane(achievementArea);
        achievementScroll.setBorder(null);
        achievementScroll.setOpaque(false);
        achievementScroll.getViewport().setOpaque(false);
        achievementScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        achievementScroll.setMaximumSize(new Dimension(440, 90));
        achievementScroll.setPreferredSize(new Dimension(440, 90));
        centeredWrap.add(achievementScroll);
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 16)));

        centeredWrap.add(fieldLabel("ACHIEVEMENT PROOF (OPTIONAL)"));
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel achievementProofRow = new JPanel(new BorderLayout(12, 0));
        achievementProofRow.setOpaque(false);
        achievementProofRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        achievementProofRow.setMaximumSize(new Dimension(440, 44));

        RoundedButton achievementButton = new RoundedButton("UPLOAD CERTIFICATE");
        achievementButton.setPreferredSize(new Dimension(180, 42));

        achievementFileLabel = new JLabel("No file selected");
        achievementFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        achievementFileLabel.setForeground(TEXT_MUTED);

        achievementProofRow.add(achievementButton, BorderLayout.WEST);
        achievementProofRow.add(achievementFileLabel, BorderLayout.CENTER);
        centeredWrap.add(achievementProofRow);
        centeredWrap.add(Box.createRigidArea(new Dimension(0, 26)));

        achievementButton.addActionListener(e -> {

            JFileChooser chooser =
                    new JFileChooser();

            int result =
                    chooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {

                achievementFile[0] =
                        chooser.getSelectedFile();

                achievementFileLabel.setText(
                        achievementFile[0].getName()
                );
            }
        });

        RoundedButton saveButton = new RoundedButton("SAVE PROFILE");
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(440, 46));
        saveButton.setPreferredSize(new Dimension(440, 46));
        centeredWrap.add(saveButton);

        saveButton.addActionListener(e -> saveProfile());

        form.add(centeredWrap);

        JScrollPane pageScroll = new JScrollPane(form);
        pageScroll.setBorder(null);
        pageScroll.getViewport().setBackground(Color.WHITE);
        pageScroll.getVerticalScrollBar().setUnitIncrement(14);

        right.add(pageScroll, BorderLayout.CENTER);
        return right;
    }

    private static void addField(JPanel parent, JComponent field) {
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(440, 44));
        parent.add(field);
        parent.add(Box.createRigidArea(new Dimension(0, 16)));
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // =====================================================
    // SAVE ACTION  (logic 100% unchanged from your code)
    // =====================================================

    private static void saveProfile() {

        String name =
                nameField.getText().trim();

        String roll =
                rollField.getText().trim();

        String course =
                courseField.getText().trim();

        String cgpaText =
                cgpaField.getText().trim();

        String achievement =
                achievementArea.getText().trim();

        // Basic validation
        if (name.isEmpty()
                || roll.isEmpty()
                || course.isEmpty()
                || cgpaText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please fill all basic profile details.",
                    "Incomplete Details",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            double cgpa =
                    Double.parseDouble(cgpaText);

            if (cgpa < 0 || cgpa > 10) {

                JOptionPane.showMessageDialog(
                        frame,
                        "CGPA must be between 0 and 10.",
                        "Invalid CGPA",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (cgpaFile[0] == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please upload your CGPA proof.",
                        "CGPA Proof Required",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Achievement is optional
            String achievementText;

            if (achievement.isEmpty()) {
                achievementText =
                        "No achievement added.";
            } else {
                achievementText =
                        achievement;
            }

            String certificateText;

            if (achievementFile[0] == null) {
                certificateText =
                        "No achievement certificate uploaded.";
            } else {
                certificateText =
                        achievementFile[0].getName();
            }

            // DATABASE CONNECTION
            Connection con =
                    DatabaseConnection.getConnection();

            if (con == null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database connection failed!",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            /*
             * Login ke liye students table mein
             * username 'sanjana' already hai.
             *
             * Isliye profile ko usi username ke
             * record mein update kar rahe hain.
             */

            String sql =
                    "UPDATE students SET " +
                            "name = ?, " +
                            "roll_number = ?, " +
                            "course = ?, " +
                            "semester = ?, " +
                            "cgpa = ?, " +
                            "cgpa_proof = ?, " +
                            "achievement = ?, " +
                            "achievement_proof = ? " +
                            "WHERE username = ?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setString(1, name);
            pst.setString(2, roll);
            pst.setString(3, course);

            // "1st Semester" se 1 extract karna
            String semesterText =
                    semesterBox.getSelectedItem().toString();

            int semester =
                    Integer.parseInt(
                            semesterText.substring(0, 1)
                    );

            pst.setInt(4, semester);
            pst.setDouble(5, cgpa);

            pst.setString(
                    6,
                    cgpaFile[0].getName()
            );

            pst.setString(
                    7,
                    achievementText
            );

            pst.setString(
                    8,
                    certificateText
            );

            // Existing login username
            pst.setString(
                    9,
                    "sanjana"
            );

            int rows =
                    pst.executeUpdate();

            pst.close();
            con.close();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        frame,
                        "\uD83C\uDF89 PROFILE SAVED SUCCESSFULLY!\n\n"
                                + "Student: " + name
                                + "\nRoll Number: " + roll
                                + "\nCourse: " + course
                                + "\nSemester: "
                                + semesterBox.getSelectedItem()
                                + "\nCGPA: " + cgpa
                                + "\nCGPA Proof: "
                                + cgpaFile[0].getName()
                                + "\n\nAchievement: "
                                + achievementText
                                + "\nAchievement Proof: "
                                + certificateText
                                + "\n\nDatabase: Saved Successfully!",
                        "Campus Care",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        frame,
                        "Profile could not be saved.\n"
                                + "Student username not found.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter a valid CGPA.\nExample: 8.2",
                    "Invalid CGPA",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Database Error:\n"
                            + ex.getMessage(),
                    "SQL Error",
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

    /** Rounded multi-line text area, styled like RoundedField — used for Achievement. */
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
            g2.setStroke(new BasicStroke(1.5f));
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

    /** Solid rounded button with hover state — used for uploads and Save. */
    static class RoundedButton extends JButton {
        private boolean isHover = false;

        RoundedButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(160, 44));

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
}