package com.classroom.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class StudentDashboard extends JFrame {

    private final MainDashboard parent;
    private final String studentId;
    private final String studentClass;

    private JTable tblResources;
    private JTable tblCached;
    private JTable tblAnnouncements;
    private JButton btnDownload, btnCache, btnRefreshAnnouncements;
    private JTabbedPane tabs;

    // Networking
    private static final String SERVER_IP = "192.168.102.105";
    private static final int SERVER_PORT = 5000;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public StudentDashboard(MainDashboard parent, String studentId, String studentClass) {
        this.parent = parent;
        this.studentId = studentId;
        this.studentClass = studentClass;

        setTitle("hello to Student  Dashboard - " + studentId);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        loadCachedFiles(); // load cached files first
        connectToServer(); // then connect to server for live resources
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 0, 128));
        header.setPreferredSize(new Dimension(1000, 70));

        JLabel lblTitle = new JLabel("Student Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(lblTitle, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(244, 67, 54));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            disconnectFromServer();
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabs.addTab("My Resources", createResourcesTab());
        tabs.addTab("Offline Cache", createCacheTab());
        tabs.addTab("Announcements", createAnnouncementsTab()); // Added Announcements tab

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // === RESOURCES TAB ===
    private JPanel createResourcesTab() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"#", "Title", "Type", "Path"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tblResources = new JTable(model);
        tblResources.setFillsViewportHeight(true);
        tblResources.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblResources.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblResources.setRowHeight(28);

        // Double-click to open downloaded file
        tblResources.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tblResources.getSelectedRow();
                    if (row != -1) {
                        String filePath = tblResources.getValueAt(row, 3).toString();
                        openFile(filePath);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblResources);
        main.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnDownload = new JButton("Download Selected");
        btnCache = new JButton("Cache Offline");

        btnDownload.setBackground(new Color(76, 175, 80));
        btnDownload.setForeground(Color.WHITE);
        btnDownload.setFocusPainted(false);

        btnCache.setBackground(new Color(255, 193, 7));
        btnCache.setForeground(Color.BLACK);
        btnCache.setFocusPainted(false);
        btnCache.addActionListener(e -> cacheAllFiles());

        btnPanel.add(btnDownload);
        btnPanel.add(btnCache);
        main.add(btnPanel, BorderLayout.SOUTH);

        return main;
    }

    // === CACHE TAB ===
    private JPanel createCacheTab() {
        JPanel cachePanel = new JPanel(new BorderLayout(10, 10));
        cachePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"#", "File Name", "Type", "Location"};
        DefaultTableModel cacheModel = new DefaultTableModel(columns, 0);
        tblCached = new JTable(cacheModel);
        tblCached.setFillsViewportHeight(true);
        tblCached.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblCached.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblCached.setRowHeight(28);

        // Double-click to open cached file
        tblCached.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tblCached.getSelectedRow();
                    if (row != -1) {
                        String filePath = tblCached.getValueAt(row, 3).toString();
                        openFile(filePath);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblCached);
        cachePanel.add(scroll, BorderLayout.CENTER);
        return cachePanel;
    }

    // === ANNOUNCEMENTS TAB ===
    private JPanel createAnnouncementsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"#", "Message", "Sender", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tblAnnouncements = new JTable(model);
        tblAnnouncements.setFillsViewportHeight(true);
        tblAnnouncements.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblAnnouncements.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblAnnouncements.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(tblAnnouncements);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRefreshAnnouncements = new JButton("Refresh");
        btnRefreshAnnouncements.setBackground(new Color(33, 150, 243));
        btnRefreshAnnouncements.setForeground(Color.WHITE);
        btnRefreshAnnouncements.setFocusPainted(false);
        btnRefreshAnnouncements.addActionListener(e -> loadAnnouncements());
        btnPanel.add(btnRefreshAnnouncements);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Initial load
        loadAnnouncements();

        return panel;
    }

    private void loadAnnouncements() {
        DefaultTableModel model = (DefaultTableModel) tblAnnouncements.getModel();
        model.setRowCount(0);

        // Replace this with your real DAO or network call
        // Example: announcementsDAO.getAnnouncementsForStudent(studentId);
        // Dummy data for demonstration:
        model.addRow(new Object[]{"1", "Welcome to the class!", "Admin", "2025-11-15"});
        model.addRow(new Object[]{"2", "Project submission deadline approaching.", "Lecturer A", "2025-11-16"});
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Join class
            out.writeUTF("STUDENT_JOIN");
            out.writeUTF(studentClass);

            System.out.println("🎓 Connected to server. Waiting for resources...");

            new Thread(this::listenForResources).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to resource server.");
        }
    }

    private void disconnectFromServer() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForResources() {
        try {
            while (true) {
                String message = in.readUTF();
                if ("NEW_FILE".equals(message)) {
                    String fileName = in.readUTF();
                    long fileSize = in.readLong();

                    File saveDir = new File("received_files/" + studentClass);
                    if (!saveDir.exists()) saveDir.mkdirs();

                    File outputFile = new File(saveDir, fileName);
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[4096];
                        long totalRead = 0;
                        int bytesRead;
                        while (totalRead < fileSize && (bytesRead = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
                    }

                    System.out.println("✅ Received file: " + fileName);
                    SwingUtilities.invokeLater(() -> addResourceToTable(fileName, outputFile.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Disconnected from server.");
        }
    }

    private void addResourceToTable(String title, String path) {
        DefaultTableModel model = (DefaultTableModel) tblResources.getModel();
        Vector<String> row = new Vector<>();
        row.add(String.valueOf(model.getRowCount() + 1));
        row.add(title);
        row.add(title.endsWith(".pdf") ? "PDF" : title.endsWith(".mp4") ? "MP4" : "FILE");
        row.add(path);
        model.addRow(row);
    }

    private void cacheAllFiles() {
        int rows = tblResources.getRowCount();
        if (rows == 0) {
            JOptionPane.showMessageDialog(this, "No files to cache.");
            return;
        }

        File cacheDir = new File("cache_files/" + studentClass);
        if (!cacheDir.exists()) cacheDir.mkdirs();

        for (int i = 0; i < rows; i++) {
            String filePath = (String) tblResources.getValueAt(i, 3);
            File src = new File(filePath);
            File dest = new File(cacheDir, src.getName());
            try (FileInputStream fis = new FileInputStream(src);
                 FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(this, "✅ All resources cached offline!");
        loadCachedFiles(); // refresh cache tab
    }

    private void loadCachedFiles() {
        File cacheDir = new File("cache_files/" + studentClass);
        if (!cacheDir.exists()) return;

        File[] files = cacheDir.listFiles();
        if (files == null || files.length == 0) return;

        DefaultTableModel model = (DefaultTableModel) tblCached.getModel();
        model.setRowCount(0);

        int count = 1;
        for (File file : files) {
            if (file.isFile()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(count++));
                row.add(file.getName());
                row.add(file.getName().endsWith(".pdf") ? "PDF" :
                        file.getName().endsWith(".mp4") ? "MP4" : "FILE");
                row.add(file.getAbsolutePath());
                model.addRow(row);
            }
        }
        System.out.println("📦 Loaded " + (count - 1) + " cached files for " + studentClass);
    }

    private void openFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "File not found:\n" + path, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                    return;
                } catch (IOException e) {
                    System.out.println("Desktop.open failed, falling back to OS commands");
                }
            }

            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "\"\"", "\"" + file.getAbsolutePath() + "\""});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", file.getAbsolutePath()});
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            } else {
                JOptionPane.showMessageDialog(this, "Cannot open file on this OS.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening file:\n" + e.getMessage());
        }
    }

    // Getters
    public JTable getResourcesTable() { return tblResources; }
    public JButton getBtnDownload() { return btnDownload; }
    public JButton getBtnCache() { return btnCache; }
    public JTabbedPane getTabs() { return tabs; }
    public JTable getAnnouncementsTable() { return tblAnnouncements; }
    public JButton getBtnRefreshAnnouncements() { return btnRefreshAnnouncements; }
}
