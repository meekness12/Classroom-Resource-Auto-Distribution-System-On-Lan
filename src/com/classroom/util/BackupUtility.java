package com.classroom.util;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupUtility {

    // ================= DATABASE BACKUP =================
    public static String backupDatabase(String dbName, String dbUser, String dbPass, String backupDir) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFile = backupDir + File.separator + dbName + "_backup_" + timestamp + ".sql";

        try {
            String mysqldumpPath = findMySQLDump();
            if (mysqldumpPath == null) {
                System.err.println("mysqldump executable not found. Please ensure MySQL is installed and PATH is correct.");
                return null;
            }

            String command = String.format("\"%s\" -u%s -p%s %s -r \"%s\"",
                    mysqldumpPath, dbUser, dbPass, dbName, backupFile);

            Process process = Runtime.getRuntime().exec(command);

            int processComplete = process.waitFor();
            if (processComplete == 0) {
                System.out.println("Database backup created successfully: " + backupFile);
                return backupFile;
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String findMySQLDump() {
        String[] possiblePaths = {
                "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe",
                "C:\\Program Files (x86)\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe"
        };

        for (String path : possiblePaths) {
            if (new File(path).exists()) return path;
        }

        // Fallback: check PATH
        try {
            Process process = Runtime.getRuntime().exec("mysqldump --version");
            if (process.waitFor() == 0) return "mysqldump";
        } catch (Exception ignored) {}

        return null;
    }

    // ================= FILES BACKUP =================
    public static String backupFiles(String sourceDir, String backupDir) {
        File source = new File(sourceDir);
        if (!source.exists()) {
            System.err.println("Source folder does not exist: " + sourceDir);
            return null;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String destDir = backupDir + File.separator + "files_backup_" + timestamp;

        try {
            Files.createDirectories(Paths.get(destDir));

            Files.walk(source.toPath())
                    .forEach(sourcePath -> {
                        try {
                            Path targetPath = Paths.get(destDir, source.toPath().relativize(sourcePath).toString());
                            if (Files.isDirectory(sourcePath)) {
                                Files.createDirectories(targetPath);
                            } else {
                                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            System.out.println("Files backup created successfully at: " + destDir);
            return destDir;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
