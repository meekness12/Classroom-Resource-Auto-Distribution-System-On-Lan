package com.classroom.util;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupUtility {

    /**
     * Backup MySQL database to a .sql file
     */
    public static String backupDatabase(String dbName, String dbUser, String dbPass, String backupDir) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFile = backupDir + File.separator + dbName + "_backup_" + timestamp + ".sql";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://192.168.102.105:3306/" + dbName + "?useSSL=false", dbUser, dbPass);
             PrintWriter pw = new PrintWriter(new FileWriter(backupFile))) {

            try (Statement stmtTables = conn.createStatement();
                 ResultSet rsTables = stmtTables.executeQuery("SHOW TABLES")) {

                while (rsTables.next()) {
                    String tableName = rsTables.getString(1);

                    // CREATE TABLE
                    try (Statement stmtCreate = conn.createStatement();
                         ResultSet rsCreate = stmtCreate.executeQuery("SHOW CREATE TABLE " + tableName)) {
                        if (rsCreate.next()) {
                            pw.println(rsCreate.getString(2) + ";");
                            pw.println();
                        }
                    }

                    // INSERT DATA
                    try (Statement stmtData = conn.createStatement();
                         ResultSet rsData = stmtData.executeQuery("SELECT * FROM " + tableName)) {

                        ResultSetMetaData metaData = rsData.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        while (rsData.next()) {
                            StringBuilder sb = new StringBuilder("INSERT INTO `" + tableName + "` VALUES (");
                            for (int i = 1; i <= columnCount; i++) {
                                Object value = rsData.getObject(i);
                                if (value == null) sb.append("NULL");
                                else sb.append("'").append(value.toString().replace("'", "\\'")).append("'");
                                if (i < columnCount) sb.append(", ");
                            }
                            sb.append(");");
                            pw.println(sb.toString());
                        }
                        pw.println();
                    }
                }
            }

            System.out.println("Database backup completed: " + backupFile);
            return backupFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Backup a folder (all files and subfolders)
     */
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
            Files.walk(source.toPath()).forEach(sourcePath -> {
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

    /**
     * Zip a folder into a single .zip file
     */
    public static String zipFolder(String folderPath) throws IOException {
        String zipPath = folderPath + ".zip";
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            Path sourcePath = Paths.get(folderPath);

            Files.walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        System.out.println("Folder zipped successfully: " + zipPath);
        return zipPath;
    }

}
