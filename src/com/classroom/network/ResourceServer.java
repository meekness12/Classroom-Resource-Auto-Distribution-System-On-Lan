package com.classroom.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ResourceServer {

    private static final int SERVER_PORT = 5000;

    // Maps className -> list of student sockets
    private static final Map<String, List<StudentHandler>> classStudents = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("🎓 Resource Server starting...");
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection: " + socket.getRemoteSocketAddress());
                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String role = in.readUTF();

            switch (role) {
                case "STUDENT_JOIN" -> {
                    String className = in.readUTF();
                    StudentHandler handler = new StudentHandler(socket, in, out, className);
                    synchronized (classStudents) {
                        classStudents.computeIfAbsent(className, k -> new ArrayList<>()).add(handler);
                    }
                    System.out.println("🎓 Student joined class: " + className);
                }

                case "LECTURER" -> {
                    String lecturerId = in.readUTF();
                    String className = in.readUTF();
                    String fileName = in.readUTF();
                    long fileSize = in.readLong();

                    File saveDir = new File("server_files");
                    if (!saveDir.exists()) saveDir.mkdirs();

                    File tempFile = new File(saveDir, fileName);
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[4096];
                        long totalRead = 0;
                        int read;
                        while (totalRead < fileSize && (read = in.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                            totalRead += read;
                        }
                    }

                    System.out.println("📤 Received file from lecturer " + lecturerId + ": " + fileName);

                    // Broadcast to all students in the class
                    broadcastToClass(className, tempFile);
                }

                default -> System.out.println("Unknown role: " + role);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastToClass(String className, File file) {
        List<StudentHandler> students;
        synchronized (classStudents) {
            students = classStudents.getOrDefault(className, Collections.emptyList());
        }

        for (StudentHandler s : students) {
            try {
                DataOutputStream out = s.getOut();
                out.writeUTF("NEW_FILE");
                out.writeUTF(file.getName());
                out.writeLong(file.length());

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
                out.flush();
                System.out.println("✅ Sent " + file.getName() + " to student " + s.socket.getRemoteSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Represents a connected student
    private static class StudentHandler {
        private final Socket socket;
        private final DataInputStream in;
        private final DataOutputStream out;
        private final String className;

        public StudentHandler(Socket socket, DataInputStream in, DataOutputStream out, String className) {
            this.socket = socket;
            this.in = in;
            this.out = out;
            this.className = className;
        }

        public DataOutputStream getOut() {
            return out;
        }
    }
}
