package classroom.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Lecture (teacher) dashboard stub.
 */
public class LectureDashboard extends JFrame {

    private final MainDashboard parent;
    private final String lectureId;

    public LectureDashboard(MainDashboard parent, String lectureId) {
        this.parent = parent;
        this.lectureId = lectureId;
        setTitle("Lecture Dashboard - " + lectureId);
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        JLabel lblTitle = new JLabel("Lecture Dashboard", SwingConstants.LEFT);
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
        tabs.addTab("Upload Resource", createPlaceholderPanel("Upload form (file chooser) here"));
        tabs.addTab("My Resources", createPlaceholderPanel("List of uploaded resources"));
        tabs.addTab("Assign to Class", createPlaceholderPanel("Assignment UI"));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }
}
