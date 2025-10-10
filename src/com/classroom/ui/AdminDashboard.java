package classroom.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final MainDashboard parent;
    private final String username;

    public AdminDashboard(MainDashboard parent, String username) {
        this.parent = parent;
        this.username = username;
        setTitle("Admin Dashboard - " + username);
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));

        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("Welcome, " + username);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 16));
        header.add(lblWelcome, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Resources", createResourcesPanel());
        tabs.addTab("Backups", createBackupsPanel());
        tabs.addTab("Reports", createReportsPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // Users Tab with sample data
    private JPanel createUsersPanel() {
        String[] columns = {"ID", "Name", "Role", "Email"};
        Object[][] data = {
                {"U001", "Alice", "Lecture", "alice@school.com"},
                {"U002", "Bob", "Student", "bob@student.com"},
                {"U003", "Charlie", "Admin", "charlie@school.com"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scroll = new JScrollPane(table);

        JButton btnAdd = new JButton("Add User");
        JButton btnEdit = new JButton("Edit User");
        JButton btnRemove = new JButton("Remove User");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnRemove);

        JPanel main = new JPanel(new BorderLayout());
        main.add(scroll, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        return createPanelWithPadding(main);
    }

    // Resources Tab with sample data
    private JPanel createResourcesPanel() {
        String[] columns = {"Resource ID", "Title", "Type", "Assigned To"};
        Object[][] data = {
                {"R001", "Math Notes", "PDF", "Class 1"},
                {"R002", "Physics Video", "MP4", "Class 2"},
                {"R003", "Chemistry Link", "URL", "Class 1"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scroll = new JScrollPane(table);

        JButton btnUpload = new JButton("Upload Resource");
        JButton btnAssign = new JButton("Assign Resource");
        JButton btnRemove = new JButton("Remove Resource");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnUpload);
        btnPanel.add(btnAssign);
        btnPanel.add(btnRemove);

        JPanel main = new JPanel(new BorderLayout());
        main.add(scroll, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        return createPanelWithPadding(main);
    }

    // Backups Tab with sample data
    private JPanel createBackupsPanel() {
        String[] columns = {"Backup ID", "Date", "Type", "Status"};
        Object[][] data = {
                {"B001", "2025-10-01", "Full", "Completed"},
                {"B002", "2025-10-05", "Incremental", "Pending"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scroll = new JScrollPane(table);

        JButton btnBackupNow = new JButton("Backup Now");
        JButton btnRestore = new JButton("Restore Backup");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnBackupNow);
        btnPanel.add(btnRestore);

        JPanel main = new JPanel(new BorderLayout());
        main.add(scroll, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);
        return createPanelWithPadding(main);
    }

    // Reports Tab (sample)
    private JPanel createReportsPanel() {
        JTextArea reportsArea = new JTextArea(
                "Sample Report:\n" +
                        "Users: 150\n" +
                        "Resources: 75\n" +
                        "Backups: 10\n" +
                        "Last Backup: 2025-10-05"
        );
        reportsArea.setEditable(false);
        reportsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        return createPanelWithPadding(new JScrollPane(reportsArea));
    }

    // Helper: add padding
    private JPanel createPanelWithPadding(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }
}
