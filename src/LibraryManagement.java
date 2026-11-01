import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibraryManagement {

    static final int FINE_PER_DAY = 5;

    // ---------------- Color Palette (deep blue — a library-appropriate, distinct identity) ----------------
    private static final Color PANEL_TOP    = new Color(20, 60, 120);
    private static final Color PANEL_BOTTOM = new Color(10, 38, 86);
    private static final Color ACCENT       = new Color(32, 96, 168);
    private static final Color ACCENT_DARK  = new Color(20, 74, 138);
    private static final Color TEXT_DARK    = new Color(32, 38, 48);
    private static final Color TEXT_MUTED   = new Color(112, 122, 138);
    private static final Color FIELD_BG     = new Color(240, 245, 252);
    private static final Color FIELD_BORDER = new Color(196, 210, 230);
    private static final Color SUCCESS_GREEN = new Color(46, 160, 110);
    private static final Color WARN_AMBER    = new Color(200, 140, 20);

    private static JFrame frame;

    private static RoundedField searchField;
    private static JLabel resultLabel;
    private static RoundedField nameField;
    private static RoundedField bookField;
    private static RoundedTextArea updateArea;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        frame = new JFrame("Campus Care - Library Management");
        frame.setSize(1080, 740);
        frame.setMinimumSize(new Dimension(880, 640));
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

        JLabel badge = new JLabel("\uD83D\uDCDA", SwingConstants.CENTER) {
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

        JLabel title = new JLabel("LIBRARY MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Search, issue & return books");
        subtitle.setForeground(new Color(210, 225, 245));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(badge);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 34)));

        content.add(infoLine("Search any book to check availability"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("Books are issued for 14 days from today"));
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(infoLine("Late returns are fined \u20B9" + FINE_PER_DAY + " per day"));

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
        dot.setForeground(new Color(190, 220, 250));
        dot.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel label = new JLabel("<html><div style='width:230px;'>" + text + "</div></html>");
        label.setForeground(new Color(220, 232, 248));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        row.add(dot);
        row.add(label);
        return row;
    }

    // ================= RIGHT: search + issue/return + update log =================
    private static JPanel buildRightPanel() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setMaximumSize(new Dimension(430, 680));

        JLabel heading = new JLabel("Search & Manage Books");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(TEXT_DARK);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(heading);
        form.add(Box.createRigidArea(new Dimension(0, 22)));

        // ---------------- SEARCH ----------------
        form.add(fieldLabel("SEARCH BOOK"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchRow.setMaximumSize(new Dimension(430, 44));

        searchField = new RoundedField();
        RoundedButton searchButton = new RoundedButton("SEARCH");
        searchButton.setPreferredSize(new Dimension(110, 42));

        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchButton, BorderLayout.EAST);
        form.add(searchRow);
        form.add(Box.createRigidArea(new Dimension(0, 8)));

        resultLabel = new JLabel("Search result will appear here.");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        resultLabel.setForeground(TEXT_MUTED);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(resultLabel);
        form.add(Box.createRigidArea(new Dimension(0, 26)));

        // ---------------- STUDENT NAME ----------------
        form.add(fieldLabel("STUDENT NAME"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        nameField = new RoundedField();
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(430, 44));
        form.add(nameField);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        // ---------------- BOOK NAME ----------------
        form.add(fieldLabel("BOOK NAME"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        bookField = new RoundedField();
        bookField.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookField.setMaximumSize(new Dimension(430, 44));
        form.add(bookField);
        form.add(Box.createRigidArea(new Dimension(0, 20)));

        // ---------------- ISSUE / RETURN ----------------
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 14, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setMaximumSize(new Dimension(430, 46));

        RoundedButton issueButton = new RoundedButton("ISSUE BOOK");
        RoundedButton returnButton = new RoundedButton("RETURN BOOK");

        buttonRow.add(issueButton);
        buttonRow.add(returnButton);
        form.add(buttonRow);
        form.add(Box.createRigidArea(new Dimension(0, 24)));

        // ---------------- LIBRARY UPDATE LOG ----------------
        form.add(fieldLabel("LIBRARY UPDATE"));
        form.add(Box.createRigidArea(new Dimension(0, 6)));

        updateArea = new RoundedTextArea();
        updateArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(updateArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(430, 140));
        scrollPane.setPreferredSize(new Dimension(430, 140));
        form.add(scrollPane);

        right.add(form, new GridBagConstraints());

        // ================= SEARCH ACTION (logic unchanged) =================
        searchButton.addActionListener(e -> {

            String search = searchField.getText().trim();

            if (search.isEmpty()) {
                resultLabel.setText("Please enter a book name.");
                resultLabel.setForeground(WARN_AMBER);
                return;
            }

            String sql =
                    "SELECT book_name, available FROM books " +
                            "WHERE LOWER(book_name) LIKE LOWER(?)";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, "%" + search + "%");

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {

                    String book = rs.getString("book_name");
                    boolean available = rs.getBoolean("available");

                    if (available) {
                        resultLabel.setText(book + " - AVAILABLE");
                        resultLabel.setForeground(SUCCESS_GREEN);
                    } else {
                        resultLabel.setText(book + " - ISSUED");
                        resultLabel.setForeground(WARN_AMBER);
                    }

                } else {
                    resultLabel.setText("Book not found.");
                    resultLabel.setForeground(TEXT_MUTED);
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database Error:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // ================= ISSUE BOOK (logic unchanged) =================
        issueButton.addActionListener(e -> {

            String student = nameField.getText().trim();
            String book = bookField.getText().trim();

            if (student.isEmpty() || book.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter Student Name and Book Name.",
                        "Incomplete Details",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            try (Connection con = DatabaseConnection.getConnection()) {

                // Find book
                String findSql =
                        "SELECT book_name, available FROM books " +
                                "WHERE LOWER(book_name) LIKE LOWER(?)";

                String actualBook = null;
                boolean available = false;

                try (PreparedStatement pst =
                             con.prepareStatement(findSql)) {

                    pst.setString(1, "%" + book + "%");

                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        actualBook = rs.getString("book_name");
                        available = rs.getBoolean("available");
                    }
                }

                if (actualBook == null) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Book not found in library.",
                            "Book Not Found",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                if (!available) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "This book is already issued.",
                            "Book Unavailable",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                // Automatic dates
                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(14);

                // Insert issue record
                String issueSql =
                        "INSERT INTO book_issues " +
                                "(student_name, book_name, issue_date, due_date) " +
                                "VALUES (?, ?, ?, ?)";

                try (PreparedStatement pst =
                             con.prepareStatement(issueSql)) {

                    pst.setString(1, student);
                    pst.setString(2, actualBook);
                    pst.setDate(3, Date.valueOf(issueDate));
                    pst.setDate(4, Date.valueOf(dueDate));

                    pst.executeUpdate();
                }

                // Make book unavailable
                String updateSql =
                        "UPDATE books SET available = FALSE " +
                                "WHERE book_name = ?";

                try (PreparedStatement pst =
                             con.prepareStatement(updateSql)) {

                    pst.setString(1, actualBook);
                    pst.executeUpdate();
                }

                updateArea.setText(
                        "Book issued successfully!\n"
                                + "Student: " + student
                                + "\nBook: " + actualBook
                                + "\nIssue Date: " + issueDate
                                + "\nDue Date: " + dueDate
                );

                JOptionPane.showMessageDialog(
                        frame,
                        "\uD83D\uDCDA Book Issued Successfully!\n\n"
                                + "Book: " + actualBook
                                + "\nIssue Date: " + issueDate
                                + "\nDue Date: " + dueDate,
                        "Library Update",
                        JOptionPane.INFORMATION_MESSAGE
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
        });

        // ================= RETURN BOOK (logic unchanged) =================
        returnButton.addActionListener(e -> {

            String book = bookField.getText().trim();

            if (book.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter Book Name.",
                        "Missing Book",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            try (Connection con = DatabaseConnection.getConnection()) {

                // Find currently issued book
                String sql =
                        "SELECT issue_id, book_name, due_date " +
                                "FROM book_issues " +
                                "WHERE LOWER(book_name) LIKE LOWER(?) " +
                                "AND return_date IS NULL";

                int issueId = -1;
                String actualBook = null;
                LocalDate dueDate = null;

                try (PreparedStatement pst =
                             con.prepareStatement(sql)) {

                    pst.setString(1, "%" + book + "%");

                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {

                        issueId = rs.getInt("issue_id");
                        actualBook = rs.getString("book_name");

                        dueDate =
                                rs.getDate("due_date").toLocalDate();
                    }
                }

                if (issueId == -1) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "This book is not currently issued.",
                            "Return Error",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                // Automatic return date
                LocalDate returnDate = LocalDate.now();

                long lateDays =
                        ChronoUnit.DAYS.between(
                                dueDate,
                                returnDate
                        );

                if (lateDays < 0) {
                    lateDays = 0;
                }

                long fine = lateDays * FINE_PER_DAY;

                // Update issue record
                String returnSql =
                        "UPDATE book_issues SET " +
                                "return_date = ?, late_days = ?, fine = ? " +
                                "WHERE issue_id = ?";

                try (PreparedStatement pst =
                             con.prepareStatement(returnSql)) {

                    pst.setDate(
                            1,
                            Date.valueOf(returnDate)
                    );
                    pst.setInt(2, (int) lateDays);
                    pst.setInt(3, (int) fine);
                    pst.setInt(4, issueId);

                    pst.executeUpdate();
                }

                // Make book available
                String bookSql =
                        "UPDATE books SET available = TRUE " +
                                "WHERE book_name = ?";

                try (PreparedStatement pst =
                             con.prepareStatement(bookSql)) {

                    pst.setString(1, actualBook);
                    pst.executeUpdate();
                }

                updateArea.setText(
                        "Book returned successfully!\n"
                                + "Book: " + actualBook
                                + "\nReturn Date: " + returnDate
                                + "\nLate Days: " + lateDays
                                + "\nFine: \u20B9" + fine
                );

                JOptionPane.showMessageDialog(
                        frame,
                        "\uD83D\uDCD5 Book Returned Successfully!\n\n"
                                + "Book: " + actualBook
                                + "\nReturn Date: " + returnDate
                                + "\nLate Days: " + lateDays
                                + "\nFine: \u20B9" + fine,
                        "Library Update",
                        JOptionPane.INFORMATION_MESSAGE
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
        });

        return right;
    }

    private static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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

    /** Rounded multi-line text area, styled like RoundedField — used for the Library Update log. */
    static class RoundedTextArea extends JTextArea {
        RoundedTextArea() {
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(TEXT_DARK);
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(FIELD_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(FIELD_BORDER);
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

    /** Solid rounded button with hover state — used for Search / Issue / Return. */
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