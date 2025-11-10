package com.classroom.network;

import java.io.*;
import java.net.Socket;

public class LecturerClient {

    private static final String SERVER_IP = "192.168.160.105"; // Change to your server LAN IP
    private static final int SERVER_PORT = 5000;

    /**
     * Sends a file to the server for a specific class.
     *
     * @param file      The file to send
     * @param lecturerId Lecturer's ID
     * @param className Target class name
     * @throws IOException If file or network error occurs
     */
    public static void sendResourceToServer(File file, String lecturerId, String className) throws IOException {
        if (!file.exists()) {
            System.err.println("File does not exist: " + file.getAbsolutePath());
            return;
        }

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             FileInputStream fis = new FileInputStream(file);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            // Send role and details
            dos.writeUTF("LECTURE");  // Must match the server
            dos.writeUTF(lecturerId);
            dos.writeUTF(className);
            dos.writeUTF(file.getName());
            dos.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, read);
            }
            dos.flush();

            System.out.println("📤 File sent successfully: " + file.getName() + " for class: " + className);
        } catch (IOException e) {
            System.err.println("❌ Failed to send file: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        // Example usage
        File file = new File("example.docx"); // Replace with your file
        String lecturerId = "3";              // Replace with actual lecturer ID
        String className = "IT Y2";           // Replace with target class

        try {
            sendResourceToServer(file, lecturerId, className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
