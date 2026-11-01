
import javax.swing.*;
import java.awt.*;

public class AdminLogin {

    private static final Color PURPLE = new Color(124, 77, 220);
    private static final Color DARK_PURPLE = new Color(96, 54, 190);
    private static final Color BG_TOP = new Color(238, 233, 254);
    private static final Color BG_BOTTOM = new Color(210, 198, 250);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminLogin::showLogin);
    }

    private static void showLogin() {
        JFrame frame = new JFrame("Campus Care - Admin Login");
        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                        0, 0, BG_TOP,
                        getWidth(), getHeight(), BG_BOTTOM
                ));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        background.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(7, 0, 7, 0);

        JLabel title = new JLabel("CAMPUS CARE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(DARK_PURPLE);

        c.gridy = 0;
        card.add(title, c);

        JLabel subtitle = new JLabel("ADMIN LOGIN", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        subtitle.setForeground(PURPLE);

        c.gridy = 1;
        card.add(subtitle, c);

        JLabel userLabel = new JLabel("Admin Username");
        c.gridy = 2;
        card.add(userLabel, c);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        c.gridy = 3;
        card.add(usernameField, c);

        JLabel passLabel = new JLabel("Admin Password");
        c.gridy = 4;
        card.add(passLabel, c);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        c.gridy = 5;
        card.add(passwordField, c);

        JLabel message = new JLabel(" ", SwingConstants.CENTER);
        message.setForeground(Color.RED);

        c.gridy = 6;
        card.add(message, c);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(PURPLE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(true);

        c.gridy = 7;
        c.insets = new Insets(12, 0, 5, 0);
        card.add(loginButton, c);

        background.add(card);
        frame.setContentPane(background);

        Runnable loginAction = () -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Temporary demo login.
            // Replace with secure database authentication in the next step.
            if (username.equals("admin") &&
                    password.equals("Admin@123")) {

                frame.dispose();
                AdminDashboard.main(null);

            } else {
                message.setText("Invalid admin username or password!");
            }
        };

        loginButton.addActionListener(e -> loginAction.run());
        passwordField.addActionListener(e -> loginAction.run());

        frame.setVisible(true);
    }
}

