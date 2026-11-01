
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard {

    private static DefaultTableModel model;
    private static JTable table;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::createDashboard);
    }

    private static void createDashboard() {
        JFrame frame = new JFrame("Campus Care - Admin Dashboard");
        frame.setSize(1100, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        main.setBackground(new Color(240, 235, 255));

        JLabel title = new JLabel(
                "CAMPUS CARE - ADMIN DASHBOARD",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(96, 54, 190));
        main.add(title, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Username", "Student Name", "Category",
                "Complaint", "Status", "Room",
                "Assigned To", "Admin Update"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {45, 100, 120, 100, 250, 100, 75, 130, 220};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Complaints");
        JButton updateButton = new JButton("Update Selected Complaint");

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.setOpaque(false);
        buttons.add(refreshButton);
        buttons.add(updateButton);
        main.add(buttons, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadComplaints(frame));
        updateButton.addActionListener(e -> updateComplaint(frame));

        frame.setContentPane(main);
        frame.setVisible(true);

        loadComplaints(frame);
    }

    private static void loadComplaints(JFrame frame) {
        model.setRowCount(0);

        String sql = "SELECT complaint_id, username, student_name, category, " +
                "complaint_text, status, room_number, assigned_to, admin_update " +
                "FROM complaints ORDER BY complaint_id DESC";

        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(frame,
                        "Database connection failed. Check DatabaseConnection.java.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("complaint_id"),
                            rs.getString("username"),
                            rs.getString("student_name"),
                            rs.getString("category"),
                            rs.getString("complaint_text"),
                            rs.getString("status"),
                            rs.getString("room_number"),
                            rs.getString("assigned_to"),
                            rs.getString("admin_update")
                    });
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Could not load complaints:\n" + ex.getMessage());
        }
    }

    private static void updateComplaint(JFrame frame) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame,
                    "Please select a complaint first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        int complaintId = Integer.parseInt(
                model.getValueAt(modelRow, 0).toString()
        );

        String[] statuses = {
                "Pending", "In Progress", "Resolved"
        };

        JComboBox<String> statusBox = new JComboBox<>(statuses);
        JTextField assignedField = new JTextField(
                valueOrEmpty(model.getValueAt(modelRow, 7)), 20
        );
        JTextArea updateArea = new JTextArea(
                valueOrEmpty(model.getValueAt(modelRow, 8)), 4, 25
        );
        updateArea.setLineWrap(true);
        updateArea.setWrapStyleWord(true);

        String currentStatus = valueOrEmpty(
                model.getValueAt(modelRow, 5)
        );
        statusBox.setSelectedItem(currentStatus);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Complaint ID: " + complaintId));
        panel.add(new JLabel("Status:"));
        panel.add(statusBox);
        panel.add(new JLabel("Assigned Department / Person:"));
        panel.add(assignedField);
        panel.add(new JLabel("Message for student:"));
        panel.add(new JScrollPane(updateArea));

        int choice = JOptionPane.showConfirmDialog(
                frame, panel, "Update Complaint",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String sql = "UPDATE complaints SET status = ?, assigned_to = ?, " +
                "admin_update = ? WHERE complaint_id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(frame,
                        "Database connection failed.");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, statusBox.getSelectedItem().toString());
                ps.setString(2, assignedField.getText().trim());
                ps.setString(3, updateArea.getText().trim());
                ps.setInt(4, complaintId);

                int affected = ps.executeUpdate();

                if (affected > 0) {
                    JOptionPane.showMessageDialog(frame,
                            "Complaint updated successfully!");
                    loadComplaints(frame);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Complaint was not found.");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Could not update complaint:\n" + ex.getMessage());
        }
    }

    private static String valueOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }
}
