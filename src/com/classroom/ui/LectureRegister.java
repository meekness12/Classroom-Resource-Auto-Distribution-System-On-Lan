package com.classroom.ui;

import com.classroom.dao.UserDAO;

import javax.swing.*;
import java.awt.*;

/**
 * Lecture registration window.
 */
public class LectureRegister extends JFrame {
    private final MainDashboard parent;
    private final UserDAO userDAO;

    public LectureRegister(MainDashboard parent) {
        this.parent = parent;
        this.userDAO = new UserDAO();
        setTitle("Lecture Registration");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblFullName = new JLabel("Full Name:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblConfirm = new JLabel("Confirm Password:");

        JTextField txtUsername = new JTextField(16);
        JTextField txtFullName = new JTextField(16);
        JPasswordField txtPassword = new JPasswordField(16);
        JPasswordField txtConfirm = new JPasswordField(16);

        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblUsername, gbc);
        gbc.gridx = 1; panel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblFullName, gbc);
        gbc.gridx = 1; panel.add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblPassword, gbc);
        gbc.gridx = 1; panel.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblConfirm, gbc);
        gbc.gridx = 1; panel.add(txtConfirm, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnRegister);
        btnPanel.add(btnCancel);
        panel.add(btnPanel, gbc);

        add(panel);

        // Register button action
        btnRegister.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String fullName = txtFullName.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String confirm = new String(txtConfirm.getPassword()).trim();

            if (username.isEmpty() || fullName.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Hash password (simple for demo)
            String passwordHash = Integer.toString(password.hashCode());

            boolean success = userDAO.addUser(username, fullName, "LECTURE", passwordHash);
            if (success) {
                JOptionPane.showMessageDialog(this, "Lecture registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                parent.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to register. Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
    }
}
