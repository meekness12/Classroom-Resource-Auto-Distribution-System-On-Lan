package com.classroom.ui;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private JPanel headerPanel, contentPanel;

    public MainDashboard() {
        setTitle("Classroom Resource Auto-Distribution System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center window
        setLayout(new BorderLayout());

        // Header
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(44, 62, 80));
        JLabel title = new JLabel("Classroom Resource Auto-Distribution System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(title);

        // Content Area (main workspace)
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout()); // center alignment
        contentPanel.setBackground(Color.WHITE);

        // Buttons for roles
        JButton btnAdmin = new JButton("Admin");
        JButton btnLecture = new JButton("Lecture");
        JButton btnStudent = new JButton("Student");

        btnAdmin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLecture.setFont(new Font("Arial", Font.BOLD, 16));
        btnStudent.setFont(new Font("Arial", Font.BOLD, 16));

        btnAdmin.setPreferredSize(new Dimension(180, 40));
        btnLecture.setPreferredSize(new Dimension(180, 40));
        btnStudent.setPreferredSize(new Dimension(180, 40));

        // Positioning with GridBag
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel roleLabel = new JLabel("📚 Select Your Role");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        contentPanel.add(roleLabel, gbc);

        gbc.gridy++;
        contentPanel.add(btnAdmin, gbc);

        gbc.gridy++;
        contentPanel.add(btnLecture, gbc);

        gbc.gridy++;
        contentPanel.add(btnStudent, gbc);

        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Actions: Open Login Windows
        btnAdmin.addActionListener(e -> {
            new AdminDashboard(this, "Admin01").setVisible(true); // Pass this as parent
            dispose(); // close dashboard after opening login
        });

        btnLecture.addActionListener(e -> {
            new LectureLogin(this).setVisible(true);
            dispose();
        });

        btnStudent.addActionListener(e -> {
            new StudentLogin(this).setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainDashboard().setVisible(true);
        });
    }
}
