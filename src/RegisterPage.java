
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterPage extends JFrame {

    private JTextField nameField;
    private JTextField collegeIdField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private final Color purple = new Color(105, 75, 190);
    private final Color background = new Color(238, 240, 255);

    public RegisterPage() {
        setTitle("Campus Care - Student Registration");
        setSize(850, 750);
        setMinimumSize(new Dimension(650, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(background);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 215, 245)),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel title = new JLabel(
                "CAMPUS CARE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(purple);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        formPanel.add(title, gbc);

        JLabel subtitle = new JLabel(
                "STUDENT REGISTRATION", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.BOLD, 19));
        subtitle.setForeground(new Color(75, 75, 95));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(subtitle, gbc);

        nameField = addField(
                formPanel, gbc, "Full Name", 2);

        collegeIdField = addField(
                formPanel, gbc, "College ID", 4);

        emailField = addField(
                formPanel, gbc, "Email Address", 6);

        usernameField = addField(
                formPanel, gbc, "Create Username", 8);

        passwordField = new JPasswordField();
        styleField(passwordField);

        addLabeledField(
                formPanel, gbc, "Create Password",
                passwordField, 10);

        JButton registerButton =
                new JButton("CREATE ACCOUNT");
        styleButton(registerButton);

        gbc.gridy = 12;
        gbc.insets = new Insets(20, 0, 8, 0);
        formPanel.add(registerButton, gbc);

        JButton loginButton =
                new JButton("Already registered? Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setForeground(purple);
        loginButton.setBackground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 13;
        gbc.insets = new Insets(5, 0, 0, 0);
        formPanel.add(loginButton, gbc);

        mainPanel.add(formPanel);
        setContentPane(mainPanel);

        registerButton.addActionListener(
                e -> registerStudent());

        loginButton.addActionListener(e -> {
            dispose();
            LoginPage.main(new String[]{});
        });
    }

    private JTextField addField(
            JPanel panel, GridBagConstraints gbc,
            String label, int row) {

        JTextField field = new JTextField();
        styleField(field);
        addLabeledField(panel, gbc, label, field, row);
        return field;
    }

    private void addLabeledField(
            JPanel panel, GridBagConstraints gbc,
            String label, JComponent field, int row) {

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(
                new Font("Arial", Font.BOLD, 14));
        fieldLabel.setForeground(
                new Color(65, 65, 85));

        gbc.gridy = row;
        gbc.insets = new Insets(7, 0, 3, 0);
        panel.add(fieldLabel, gbc);

        gbc.gridy = row + 1;
        gbc.insets = new Insets(3, 0, 7, 0);
        panel.add(field, gbc);
    }

    private void styleField(JComponent field) {
        field.setFont(
                new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(
                new Dimension(350, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(205, 205, 225)),
                BorderFactory.createEmptyBorder(
                        5, 10, 5, 10)
        ));
    }

    private void styleButton(JButton button) {
        button.setFont(
                new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(purple);
        button.setPreferredSize(
                new Dimension(350, 48));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
    }

    private void registerStudent() {

        String name = nameField.getText().trim();
        String collegeId =
                collegeIdField.getText().trim();
        String email = emailField.getText().trim();
        String username =
                usernameField.getText().trim();
        String password =
                new String(passwordField.getPassword());

        if (name.isEmpty() || collegeId.isEmpty()
                || email.isEmpty() || username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in all fields.");
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            JOptionPane.showMessageDialog(
                    this, "Please enter a valid email address.");
            return;
        }

        String sql = "INSERT INTO students "
                + "(name, username, password, college_id, email) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con =
                     DatabaseConnection.getConnection()) {

            if (con == null) {
                JOptionPane.showMessageDialog(
                        this, "Database connection failed.");
                return;
            }

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, collegeId);
                ps.setString(5, email);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Registration successful! "
                            + "Welcome to Campus Care!");

            dispose();

            // New student goes directly to Dashboard
            Dashboard.main(new String[]{});

        } catch (SQLException ex) {

            if (ex.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(
                        this,
                        "College ID, email or username "
                                + "already exists.");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Registration failed: "
                                + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new RegisterPage().setVisible(true));
    }
}
