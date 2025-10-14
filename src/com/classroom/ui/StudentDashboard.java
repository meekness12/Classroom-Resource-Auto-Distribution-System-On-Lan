package com.classroom.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Student dashboard stub.
 * Build resource list, download and offline access UI here.
 */
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
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        JLabel lblTitle = new JLabel("Student Dashboard", SwingConstants.LEFT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Resources", createPlaceholderPanel("List of assigned resources"));
        tabs.addTab("Announcements", createPlaceholderPanel("Class announcements"));
        tabs.addTab("Offline Cache", createPlaceholderPanel("Cached files & status"));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }
}
