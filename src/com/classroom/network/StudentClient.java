package com.classroom.network;

import com.classroom.ui.StudentDashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
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
            socket = new Socket("localhost", 5000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Send join info
            out.writeUTF("STUDENT_JOIN");
            out.writeUTF(studentClass);

            System.out.println("🎓 Connected to Resource Server. Waiting for files...");

            // Start listening thread
            new Thread(this::listenForResources).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dashboard, "Failed to connect to resource server.");
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
