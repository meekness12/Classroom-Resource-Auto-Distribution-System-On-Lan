package com.classroom.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class StudentDashboard extends JFrame {

    private final MainDashboard parent;
    private final String studentId;
    private final String studentClass;

    private JTable tblResources;
    private JButton btnDownload, btnCache;
    private JTabbedPane tabs;

    // Networking
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5000;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public StudentDashboard(MainDashboard parent, String studentId, String studentClass) {
        this.parent = parent;
        this.studentId = studentId;
        this.studentClass = studentClass;

        setTitle("Student Dashboard - " + studentId);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        connectToServer();
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
        tabs.addTab("Offline Cache", createPlaceholderPanel("Cached files & status"));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createResourcesTab() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Resource ID", "Title", "Type", "File Path"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tblResources = new JTable(model);
        tblResources.setFillsViewportHeight(true);
        tblResources.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblResources.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

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

    private JPanel createPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);
        return p;
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
        try { if (socket != null) socket.close(); } catch (IOException e) { e.printStackTrace(); }
    }

    private void listenForResources() {
        try {
            while (true) {
                String message = in.readUTF();
                if ("NEW_FILE".equals(message)) {
                    String fileName = in.readUTF();
                    long fileSize = in.readLong();

                    File saveDir = new File("received_files");
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
        row.add(title.endsWith(".pdf") ? "PDF" : title.endsWith(".mp4") ? "MP4" : "URL");
        row.add(path);
        model.addRow(row);
    }

    private void cacheAllFiles() {
        int rows = tblResources.getRowCount();
        if (rows == 0) {
            JOptionPane.showMessageDialog(this, "No files to cache.");
            return;
        }
        for (int i = 0; i < rows; i++) {
            String filePath = (String) tblResources.getValueAt(i, 3);
            File src = new File(filePath);
            File cacheDir = new File("cache_files");
            if (!cacheDir.exists()) cacheDir.mkdirs();

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
        JOptionPane.showMessageDialog(this, "All resources cached offline!");
    }

    // Getters for controller
    public JTable getResourcesTable() { return tblResources; }
    public JButton getBtnDownload() { return btnDownload; }
    public JButton getBtnCache() { return btnCache; }
    public JTabbedPane getTabs() { return tabs; }
}
