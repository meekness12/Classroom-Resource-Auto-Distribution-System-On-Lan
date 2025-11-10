package com.classroom.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResourceServer {

    private static final int SERVER_PORT = 5000;

    // Maps className -> list of connected students
    private static final Map<String, List<StudentHandler>> classStudents = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("🎓 Resource Server starting on port " + SERVER_PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection: " + socket.getRemoteSocketAddress());
                new Thread(new Runnable() {
                    public void run() {
                        handleClient(socket);
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String role = in.readUTF().trim().toUpperCase();

            switch (role) {

                case "STUDENT":
                case "STUDENT_JOIN": {
                    String className = in.readUTF();
                    StudentHandler handler = new StudentHandler(socket, in, out, className);
                    synchronized (classStudents) {
                        classStudents.computeIfAbsent(className, k -> new CopyOnWriteArrayList<>()).add(handler);
                    }
                    System.out.println("🎓 Student joined class: " + className);

                    // Keep the connection alive
                    while (!socket.isClosed()) {
                        try {
                            Thread.sleep(5000); // simple heartbeat
                        } catch (InterruptedException ignored) { }
                    }

                    removeStudent(handler);
                    break;
                }

                case "LECTURE":
                case "LECTURER":
                case "LECTURER_SEND": {
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
                    break;
                }

                default:
                    System.out.println("⚠️ Unknown role: " + role + " from " + socket.getRemoteSocketAddress());
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

        for (final StudentHandler s : students) {
            new Thread(new Runnable() {
                public void run() {
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
                        System.err.println("❌ Failed to send file to student " + s.socket.getRemoteSocketAddress());
                        removeStudent(s);
                    }
                }
            }).start();
        }
    }

    private static void removeStudent(StudentHandler student) {
        synchronized (classStudents) {
            List<StudentHandler> list = classStudents.get(student.className);
            if (list != null) list.remove(student);
        }
        try {
            if (!student.socket.isClosed()) student.socket.close();
            System.out.println("🗑️ Removed student from class " + student.className);
        } catch (IOException e) {
            e.printStackTrace();
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
