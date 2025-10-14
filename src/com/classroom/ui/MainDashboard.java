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
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Detect active LAN automatically
        detectLAN();

        // Initialize the main dashboard UI
        initUI();
    }

    /**
     * Auto-detect active LAN and allow user to confirm/change.
     */
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

            // Show input dialog with detected LAN prefilled
            String input = (String) JOptionPane.showInputDialog(
                    null,
                    "Confirm or select the LAN you are connected to:",
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
            // Fallback: manual input if detection fails
            selectedLAN = JOptionPane.showInputDialog("Enter LAN IP manually:");
            if (selectedLAN == null || selectedLAN.isEmpty()) {
                JOptionPane.showMessageDialog(null, "LAN selection required. Exiting.");
                System.exit(0);
            }
        }
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("Classroom Resource System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // LAN info
        JLabel lblLAN = new JLabel("Connected LAN: " + selectedLAN, SwingConstants.CENTER);
        lblLAN.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLAN.setForeground(new Color(55, 71, 79));
        gbc.gridy++;
        panel.add(lblLAN, gbc);

        // Welcome message
        JLabel lblWelcome = new JLabel("<html><center>Hey, welcome to our Classroom Auto Distribution!<br>"
                + "Please choose your role to continue.</center></html>", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setForeground(new Color(55, 71, 79));
        gbc.gridy++;
        panel.add(lblWelcome, gbc);

        // Buttons with modern look
        JButton btnAdmin = createStyledButton("Admin", new Color(33, 150, 243));
        JButton btnLecture = createStyledButton("Lecture", new Color(0, 123, 255));
        JButton btnStudent = createStyledButton("Student", new Color(40, 167, 69));

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(btnAdmin, gbc);
        gbc.gridy++;
        panel.add(btnLecture, gbc);
        gbc.gridy++;
        panel.add(btnStudent, gbc);

        add(panel);

        // Action Listeners
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
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
