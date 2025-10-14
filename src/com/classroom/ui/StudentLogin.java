package com.classroom.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Student login window (mock).
 */
public class StudentLogin extends JFrame {
    private final MainDashboard parent;

    public StudentLogin(MainDashboard parent) {
        this.parent = parent;
        setTitle("Student Login");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel lblId = new JLabel("Student ID:");
        JLabel lblPass = new JLabel("Password:");
        JTextField txtId = new JTextField(16);
        JPasswordField txtPass = new JPasswordField(16);
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");

        gbc.gridx = 0; gbc.gridy = 0; p.add(lblId, gbc);
        gbc.gridx = 1; p.add(txtId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(lblPass, gbc);
        gbc.gridx = 1; p.add(txtPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnLogin);
        btnPanel.add(btnCancel);
        p.add(btnPanel, gbc);

        add(p);

        btnLogin.addActionListener(e -> {
            String id = txtId.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            if (id.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter student ID and password.", "Login Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: real auth
            StudentDashboard dash = new StudentDashboard(parent, id);
            dash.setVisible(true);
            this.dispose();
        });

        btnCancel.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
    }
}
