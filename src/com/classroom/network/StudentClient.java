package com.classroom.network;

import com.classroom.ui.StudentDashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

public class StudentClient {

    private final String studentClass;
    private final StudentDashboard dashboard;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public StudentClient(StudentDashboard dashboard, String studentClass) {
        this.dashboard = dashboard;
        this.studentClass = studentClass;
        connectToServer();

        // 🟢 Load cached resources AFTER dashboard is fully visible
        SwingUtilities.invokeLater(this::loadCachedResources);
    }

    private void connectToServer() {
        try {
            String serverIP = "192.168.160.105"; // ✅ change to your LAN server IP
            int serverPort = 5000;

            socket = new Socket(InetAddress.getByName(serverIP), serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Identify as student
            out.writeUTF("STUDENT");
            out.writeUTF(studentClass);
            out.flush();

            System.out.println("🎓 Connected to Resource Server. Waiting for files...");

            // Start listener thread
            new Thread(this::listenForResources).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    dashboard,
                    "❌ Failed to connect to resource server.\nCheck IP, port, or firewall.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void listenForResources() {
        try {
            while (true) {
                String message = in.readUTF();
                if ("NEW_FILE".equalsIgnoreCase(message)) {
                    String fileName = in.readUTF();
                    long fileSize = in.readLong();

                    // Save to local folder by class
                    File classDir = new File("received_files/" + studentClass);
                    if (!classDir.exists()) classDir.mkdirs();

                    File outFile = new File(classDir, fileName);
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[4096];
                        long totalRead = 0;
                        int bytesRead;
                        while (totalRead < fileSize && (bytesRead = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;
                        }
                    }

                    System.out.println("✅ Received: " + fileName);

                    SwingUtilities.invokeLater(() ->
                            addResourceToTable(fileName, outFile.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Disconnected from Resource Server.");
        }
    }

    /**
     * Load cached files stored locally in received_files/className/
     */
    private void loadCachedResources() {
        File classDir = new File("received_files/" + studentClass);
        if (!classDir.exists()) {
            System.out.println("📁 No folder found for: " + studentClass);
            return;
        }

        File[] files = classDir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("📁 No cached files found for: " + studentClass);
            return;
        }

        System.out.println("📂 Loading cached files for " + studentClass + "...");
        for (File file : files) {
            if (file.isFile()) {
                addResourceToTable(file.getName(), file.getAbsolutePath());
            }
        }
        System.out.println("✅ Loaded " + files.length + " cached resources for " + studentClass);
    }

    private void addResourceToTable(String title, String path) {
        DefaultTableModel model = (DefaultTableModel) dashboard.getResourcesTable().getModel();

        // Avoid duplicate rows
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(title)) return;
        }

        Vector<String> row = new Vector<>();
        row.add(String.valueOf(model.getRowCount() + 1)); // ID
        row.add(title);
        row.add(title.endsWith(".pdf") ? "PDF" :
                title.endsWith(".mp4") ? "MP4" :
                title.endsWith(".docx") ? "DOCX" : "FILE");
        row.add(path);
        model.addRow(row);
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}
