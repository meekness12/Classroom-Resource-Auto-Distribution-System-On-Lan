package com.classroom.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LectureDashboard extends JFrame {

    private final MainDashboard parent;
    private final String lectureId;

    public LectureDashboard(MainDashboard parent, String lectureId) {
        this.parent = parent;
        this.lectureId = lectureId;
        setTitle("Lecture Dashboard - " + lectureId);
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));

        JLabel lblTitle = new JLabel("Lecture Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("Welcome, " + lectureId);
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
        tabs.addTab("Upload Resource", createUploadPanel());
        tabs.addTab("My Resources", createMyResourcesPanel());
        tabs.addTab("Assign to Class", createAssignPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // Upload Resource Tab
    private JPanel createUploadPanel() {
        JButton btnChooseFile = new JButton("Choose File");
        JButton btnUpload = new JButton("Upload");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnChooseFile);
        btnPanel.add(btnUpload);

        JLabel lblInfo = new JLabel("<html>Select a file (PDF, MP4, or URL) and upload it.<br>Files will be visible in 'My Resources'.</html>");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel main = new JPanel(new BorderLayout());
        main.add(lblInfo, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);

        return createPanelWithPadding(main);
    }

    // My Resources Tab with sample data
    private JPanel createMyResourcesPanel() {
        String[] columns = {"Resource ID", "Title", "Type", "Uploaded On"};
        Object[][] data = {
                {"R001", "Math Notes", "PDF", "2025-10-01"},
                {"R002", "Physics Lecture", "MP4", "2025-10-03"},
                {"R003", "Chemistry Link", "URL", "2025-10-05"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scroll = new JScrollPane(table);

        JButton btnRemove = new JButton("Remove Resource");
        JButton btnDownload = new JButton("Download Resource");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnDownload);
        btnPanel.add(btnRemove);

        JPanel main = new JPanel(new BorderLayout());
        main.add(scroll, BorderLayout.CENTER);
        main.add(btnPanel, BorderLayout.SOUTH);

        return createPanelWithPadding(main);
    }

    // Assign Resource to Class Tab with sample UI
    private JPanel createAssignPanel() {
        String[] classes = {"Class 1", "Class 2", "Class 3"};
        JComboBox<String> classCombo = new JComboBox<>(classes);

        String[] resources = {"Math Notes", "Physics Lecture", "Chemistry Link"};
        JComboBox<String> resourceCombo = new JComboBox<>(resources);

        JButton btnAssign = new JButton("Assign Resource");

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Select Class:"));
        formPanel.add(classCombo);
        formPanel.add(new JLabel("Select Resource:"));
        formPanel.add(resourceCombo);
        formPanel.add(new JLabel());
        formPanel.add(btnAssign);

        JPanel main = new JPanel(new BorderLayout());
        main.add(new JLabel("Assign uploaded resources to a class:", SwingConstants.CENTER), BorderLayout.NORTH);
        main.add(formPanel, BorderLayout.CENTER);

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
