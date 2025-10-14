package com.classroom.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private final MainDashboard parent;
    private final String studentId;

    // Components exposed to controller
    private JTable tblResources;
    private JButton btnDownload, btnCache;
    private JTabbedPane tabs;

    public StudentDashboard(MainDashboard parent, String studentId) {
        this.parent = parent;
        this.studentId = studentId;

        setTitle("Student Dashboard - " + studentId);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 0, 128)); // Navy blue
        header.setPreferredSize(new Dimension(1000, 70));

        JLabel lblTitle = new JLabel("Student Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(244, 67, 54));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        // Tabs
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabs.addTab("My Resources", createResourcesTab());
        tabs.addTab("Offline Cache", createPlaceholderPanel("Cached files & status"));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createResourcesTab() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table
        String[] columns = {"Resource ID", "Title", "Type", "Assigned On"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tblResources = new JTable(model);
        tblResources.setFillsViewportHeight(true);
        tblResources.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblResources.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tblResources);
        main.add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnDownload = new JButton("Download");
        btnDownload.setBackground(new Color(76, 175, 80));
        btnDownload.setForeground(Color.WHITE);
        btnDownload.setFocusPainted(false);

        btnCache = new JButton("Cache Offline");
        btnCache.setBackground(new Color(255, 193, 7));
        btnCache.setForeground(Color.BLACK);
        btnCache.setFocusPainted(false);

        btnPanel.add(btnDownload);
        btnPanel.add(btnCache);
        main.add(btnPanel, BorderLayout.SOUTH);

        return main;
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ✅ Getters for controller
    public JTable getResourcesTable() { return tblResources; }
    public JButton getBtnDownload() { return btnDownload; }
    public JButton getBtnCache() { return btnCache; }
    public JTabbedPane getTabs() { return tabs; }
}
