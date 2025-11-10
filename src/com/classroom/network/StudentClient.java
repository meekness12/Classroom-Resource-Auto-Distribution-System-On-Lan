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
    }

    private void connectToServer() {
        try {
            // Use LAN IP or localhost dynamically
            String serverIP = "192.168.160.105"; // change to your server IP
            int serverPort = 5000;

            socket = new Socket(InetAddress.getByName(serverIP), serverPort);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Send correct role
            out.writeUTF("STUDENT"); 
            out.writeUTF(studentClass);

            System.out.println("🎓 Connected to Resource Server. Waiting for files...");

            // Start listening thread
            new Thread(this::listenForResources).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dashboard, "Failed to connect to resource server.\nCheck server IP, port, and firewall.");
        }
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

                    // Update GUI table safely on EDT
                    SwingUtilities.invokeLater(() ->
                            addResourceToTable(fileName, outputFile.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dashboard, "Connection lost. Resource server may be offline.");
        }
    }

    private void addResourceToTable(String title, String path) {
        DefaultTableModel model = (DefaultTableModel) dashboard.getResourcesTable().getModel();
        Vector<String> row = new Vector<>();
        row.add(String.valueOf(model.getRowCount() + 1)); // Resource ID
        row.add(title);
        row.add(title.endsWith(".pdf") ? "PDF" : title.endsWith(".mp4") ? "MP4" : "URL");
        row.add(path);
        model.addRow(row);
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
