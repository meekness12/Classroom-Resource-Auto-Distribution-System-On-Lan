package com.classroom.ui;

import com.classroom.dao.UserDAO;

import javax.swing.*;
import java.awt.*;

/**
 * Admin login window using UserDAO.
 */
public class AdminLogin extends JFrame {
    private final MainDashboard parent;
    private final UserDAO userDAO;

    public AdminLogin(MainDashboard parent) {
        this.parent = parent;
        this.userDAO = new UserDAO(); // DAO to interact with DB
        setTitle("Admin Login");
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

        JLabel lblUser = new JLabel("Username:");
        JLabel lblPass = new JLabel("Password:");
        JTextField txtUser = new JTextField(16);
        JPasswordField txtPass = new JPasswordField(16);
        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Cancel");

        gbc.gridx = 0; gbc.gridy = 0; p.add(lblUser, gbc);
        gbc.gridx = 1; p.add(txtUser, gbc);
        gbc.gridx = 0; gbc.gridy = 1; p.add(lblPass, gbc);
        gbc.gridx = 1; p.add(txtPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnLogin);
        btnPanel.add(btnCancel);
        p.add(btnPanel, gbc);

        add(p);

        // Login action
        btnLogin.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username and password.", "Login Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Admin passwords in DB are plain text
            boolean valid = userDAO.validateLogin(username, password);

            if (valid) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                AdminDashboard dash = new AdminDashboard(parent, username);
                dash.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
    }
}
