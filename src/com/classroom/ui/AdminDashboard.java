package classroom.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Basic Admin dashboard placeholder.
 * Build on this: add tabs for Users, Backups, Logs, Reports, etc.
 */
public class AdminDashboard extends JFrame {

    private final MainDashboard parent;
    private final String username; // who logged in

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
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        JLabel lblTitle = new JLabel("Admin Dashboard", SwingConstants.LEFT);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        // Main content: tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users", createPlaceholderPanel("Manage users here"));
        tabs.addTab("Resources", createPlaceholderPanel("Resource inventory / upload logs"));
        tabs.addTab("Backups", createPlaceholderPanel("Backup management & logs"));
        tabs.addTab("Reports", createPlaceholderPanel("Usage & distribution reports"));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }
}
