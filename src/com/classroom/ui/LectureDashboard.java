package com.classroom.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LectureDashboard extends JFrame {

    private final MainDashboard parent;
    private final String lectureId;

    // Components for controller
    private JButton btnUpload, btnRemove, btnAssign;
    private JTable tblResources;
    private JComboBox<String> cmbClass, cmbResource;
    private JTabbedPane tabs;

    public LectureDashboard(MainDashboard parent, String lectureId) {
        this.parent = parent;
        this.lectureId = lectureId;

        setTitle("Lecture Dashboard - " + lectureId);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setPreferredSize(new Dimension(1000, 70));

        JLabel lblTitle = new JLabel("📘 Lecture Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("Welcome, " + lectureId);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblWelcome, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(244, 67, 54));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        // Tabs
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabs.addTab("Upload Resource", createUploadPanel());
        tabs.addTab("My Resources", createMyResourcesPanel());
        tabs.addTab("Assign to Class", createAssignPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // Upload Resource Tab
    private JPanel createUploadPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblInfo = new JLabel("<html>Select a file (PDF, MP4, or URL) and upload it.<br>Files will be visible in 'My Resources'.</html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        main.add(lblInfo, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnChooseFile = new JButton("Choose File");
        btnUpload = new JButton("Upload");
        btnUpload.setBackground(new Color(76, 175, 80));
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setFocusPainted(false);
        btnPanel.add(btnChooseFile);
        btnPanel.add(btnUpload);

        main.add(btnPanel, BorderLayout.SOUTH);
        return main;
    }

    // My Resources Tab
    private JPanel createMyResourcesPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Resource ID", "Title", "Type", "Uploaded On"};
        Object[][] data = {
                {"R001", "Math Notes", "PDF", "2025-10-01"},
                {"R002", "Physics Lecture", "MP4", "2025-10-03"},
                {"R003", "Chemistry Link", "URL", "2025-10-05"}
        };
        tblResources = new JTable(new DefaultTableModel(data, columns));
        tblResources.setFillsViewportHeight(true);
        tblResources.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblResources.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tblResources);
        main.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRemove = new JButton("Remove Resource");
        btnRemove.setBackground(new Color(244, 67, 54));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setFocusPainted(false);
        btnPanel.add(btnRemove);
        main.add(btnPanel, BorderLayout.SOUTH);

        return main;
    }

    // Assign Resource Tab
    private JPanel createAssignPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.add(new JLabel("Select Class:"));
        cmbClass = new JComboBox<>(new String[]{"Class 1", "Class 2", "Class 3"});
        formPanel.add(cmbClass);

        formPanel.add(new JLabel("Select Resource:"));
        cmbResource = new JComboBox<>(new String[]{"Math Notes", "Physics Lecture", "Chemistry Link"});
        formPanel.add(cmbResource);

        btnAssign = new JButton("Assign Resource");
        btnAssign.setBackground(new Color(76, 175, 80));
        btnAssign.setForeground(Color.WHITE);
        btnAssign.setFocusPainted(false);

        formPanel.add(new JLabel());
        formPanel.add(btnAssign);

        main.add(new JLabel("Assign uploaded resources to a class:", SwingConstants.CENTER), BorderLayout.NORTH);
        main.add(formPanel, BorderLayout.CENTER);

        return main;
    }

    // Getters for controller
    public JButton getBtnUpload() { return btnUpload; }
    public JButton getBtnRemove() { return btnRemove; }
    public JButton getBtnAssign() { return btnAssign; }
    public JTable getResourcesTable() { return tblResources; }
    public JComboBox<String> getClassCombo() { return cmbClass; }
    public JComboBox<String> getResourceCombo() { return cmbResource; }
    public JTabbedPane getTabs() { return tabs; }
}
