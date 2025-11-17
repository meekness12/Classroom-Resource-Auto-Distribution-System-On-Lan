package com.classroom.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class MainDashboard extends JFrame {

    private String selectedLAN;

    public MainDashboard() {
        setTitle("Classroom Resource Distribution System");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        detectLAN();
        initUI();
    }

    private void detectLAN() {
        String detectedLAN = null;
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            ArrayList<String> lanList = new ArrayList<>();
            for (NetworkInterface netIf : Collections.list(nets)) {
                if (!netIf.isLoopback() && netIf.isUp()) {
                    Enumeration<InetAddress> addresses = netIf.getInetAddresses();
                    for (InetAddress addr : Collections.list(addresses)) {
                        if (!addr.isLoopbackAddress() && addr.getHostAddress().contains(".")) {
                            detectedLAN = netIf.getName() + " - " + netIf.getDisplayName() + " (" + addr.getHostAddress() + ")";
                            lanList.add(detectedLAN);
                        }
                    }
                }
            }

            String input = (String) JOptionPane.showInputDialog(
                    null,
                    "Confirm or select your LAN:",
                    "LAN Selection",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    lanList.toArray(),
                    detectedLAN
            );

            if (input == null || input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "LAN selection is required. Exiting.");
                System.exit(0);
            }

            selectedLAN = input;

        } catch (Exception e) {
            selectedLAN = JOptionPane.showInputDialog("Enter LAN IP manually:");
            if (selectedLAN == null || selectedLAN.isEmpty()) {
                JOptionPane.showMessageDialog(null, "LAN selection required. Exiting.");
                System.exit(0);
            }
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title
        JLabel lblTitle = new JLabel("Classroom Resource System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setForeground(new Color(33, 37, 41));
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        // LAN Info
        JLabel lblLAN = new JLabel("Connected LAN: " + selectedLAN, SwingConstants.CENTER);
        lblLAN.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblLAN.setForeground(new Color(55, 71, 79));
        lblLAN.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblLAN);
        mainPanel.add(Box.createVerticalStrut(20));

        // Welcome message
        JLabel lblWelcome = new JLabel(
                "<html><center>Welcome to Classroom Auto Distribution!<br>Choose your role to continue.</center></html>",
                SwingConstants.CENTER
        );
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblWelcome.setForeground(new Color(55, 71, 79));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblWelcome);
        mainPanel.add(Box.createVerticalStrut(40));

        // Role buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new GridLayout(3, 1, 0, 15));

        JButton btnAdmin = createStyledButton("Admin", new Color(33, 150, 243));
        JButton btnLecture = createStyledButton("Lecture", new Color(0, 123, 255));
        JButton btnStudent = createStyledButton("Student", new Color(40, 167, 69));

        btnPanel.add(btnAdmin);
        btnPanel.add(btnLecture);
        btnPanel.add(btnStudent);

        mainPanel.add(btnPanel);
        add(mainPanel);

        // Button actions
        btnAdmin.addActionListener(e -> {
            this.setVisible(false);
            new AdminLogin(this).setVisible(true);
        });

        btnLecture.addActionListener(e -> {
            this.setVisible(false);
            new LectureLogin(this).setVisible(true);
        });

        btnStudent.addActionListener(e -> {
            this.setVisible(false);
            new StudentLogin(this).setVisible(true);
        });
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboard().setVisible(true));
    }
}
