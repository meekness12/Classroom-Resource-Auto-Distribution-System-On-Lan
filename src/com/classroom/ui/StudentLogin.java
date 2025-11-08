package com.classroom.ui;

import com.classroom.dao.UserDAO;

import javax.swing.*;
import java.awt.*;

/**
 * Student login window connected to DB.
 */
public class StudentLogin extends JFrame {
    private final MainDashboard parent;
    private final UserDAO userDAO;

    public StudentLogin(MainDashboard parent) {
        this.parent = parent;
        this.userDAO = new UserDAO(); // DAO to interact with DB
        setTitle("Student Login");
        setSize(420, 300); // increased height for new button
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel lblId = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");
        JTextField txtId = new JTextField(16);
        JPasswordField txtPass = new JPasswordField(16);
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");
        JButton btnRegister = new JButton("Register"); // new button

        gbc.gridx = 0; gbc.gridy = 0; p.add(lblId, gbc);
        gbc.gridx = 1; p.add(txtId, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(lblPass, gbc);
        gbc.gridx = 1; p.add(txtPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnLogin);
        btnPanel.add(btnCancel);
        btnPanel.add(btnRegister); // add register button
        p.add(btnPanel, gbc);

        add(p);

        // Login button action
        btnLogin.addActionListener(e -> {
            String username = txtId.getText().trim();
            String password = new String(txtPass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username and password.", "Login Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Hash password same as registration
            String passwordHash = Integer.toString(password.hashCode());

            boolean valid = userDAO.validateLogin(username, passwordHash);

            if (valid) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Open student dashboard
                StudentDashboard dash = new StudentDashboard(parent, username);
                dash.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel button action
        btnCancel.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });

        // Register button action
        btnRegister.addActionListener(e -> {
            this.dispose();
            new StudentRegister(parent).setVisible(true);
        });
    }
}
