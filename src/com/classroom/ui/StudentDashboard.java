package classroom.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private final MainDashboard parent;
    private final String studentId;

    public StudentDashboard(MainDashboard parent, String studentId) {
        this.parent = parent;
        this.studentId = studentId;
        setTitle("Student Dashboard - " + studentId);
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));

        JLabel lblTitle = new JLabel("Student Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("Welcome, " + studentId);
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
        tabs.addTab("My Resources", createMyResourcesPanel());
        tabs.addTab("Offline Cache", createOfflineCachePanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // My Resources tab with sample data and download button
    private JPanel createMyResourcesPanel() {
        String[] columns = {"Resource ID", "Title", "Type", "Uploaded On"};
        Object[][] data = {
                {"R001", "Math Notes", "PDF", "2025-10-01"},
                {"R002", "Physics Lecture", "MP4", "2025-10-03"},
                {"R003", "Chemistry Link", "URL", "2025-10-05"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scroll = new JScrollPane(table);

        JButton btnDownload = new JButton("Download Resource");
        btnDownload.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String title = table.getValueAt(row, 1).toString();
                JOptionPane.showMessageDialog(this, "Downloaded: " + title);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnDownload);

        JPanel main = new JPanel(new BorderLayout());
        main.add(scroll, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);

        return createPanelWithPadding(main);
    }

    // Offline Cache tab with sample cached files
    private JPanel createOfflineCachePanel() {
        String[] columns = {"Resource", "Type", "Cached On"};
        Object[][] data = {
                {"Math Notes", "PDF", "2025-10-06"},
                {"Physics Lecture", "MP4", "2025-10-06"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scroll = new JScrollPane(table);

        JPanel main = new JPanel(new BorderLayout());
        main.add(scroll, BorderLayout.CENTER);

        return createPanelWithPadding(main);
    }

    // Helper: add padding
    private JPanel createPanelWithPadding(JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(comp, BorderLayout.CENTER);
        return panel;
    }
}
