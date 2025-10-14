package com.classroom.ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final MainDashboard parent;
    private final String username;
    private JTabbedPane tabs;
    private JButton btnLogout;

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

        btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        header.add(btnLogout, BorderLayout.EAST);

        // Tabs
        tabs = new JTabbedPane();
        // Initially empty, Controller will populate
        tabs.addTab("Users", new JPanel());
        tabs.addTab("Classes", new JPanel());
        tabs.addTab("Resources", new JPanel());
        tabs.addTab("Backups", new JPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // Getters for Controller
    public JTabbedPane getTabs() {
        return tabs;
    }

    public JButton getBtnLogout() {
        return btnLogout;
    }
}
