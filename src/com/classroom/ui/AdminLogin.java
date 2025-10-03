package classroom.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple Admin login window (mock authentication).
 */
public class AdminLogin extends JFrame {
    private final MainDashboard parent;

    public AdminLogin(MainDashboard parent) {
        this.parent = parent;
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

        btnLogin.addActionListener(e -> {
            // Mock authentication: any non-empty username/password allowed
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.", "Login Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // TODO: replace with real auth (DAO)
            // Open AdminDashboard
            AdminDashboard dash = new AdminDashboard(parent, user);
            dash.setVisible(true);
            this.dispose();
        });

        btnCancel.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
    }
}
