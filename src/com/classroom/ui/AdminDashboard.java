package com.classroom.ui;

import com.classroom.dao.UserDAO;
import com.classroom.dao.UserLogDAO;
import com.classroom.model.BackupLog;
import com.classroom.model.UserLog;
import com.classroom.dao.ClassDAO;
import com.classroom.dao.StudentClassDAO;
import com.classroom.dao.BackupLogDAO;
import com.classroom.util.BackupUtility;
import com.classroom.util.GoogleDriveBackup;
import com.classroom.util.ZipUtility;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

/**
 * Improved AdminDashboard with schedule backup UI and scheduling support.
 * Put this file at: src/com/classroom/ui/AdminDashboard.java
 */
public class AdminDashboard extends JFrame {

    private final MainDashboard parent;
    private final String username;

    private JTabbedPane tabs;
    private JButton btnLogout;

    // Users tab
    private JTable userTable;
    private DefaultTableModel userModel;
    private UserDAO userDAO;
    private static final String DEFAULT_ADMIN_USERNAME = "classroomadmin";

    // Classes tab
    private JTable classTable;
    private DefaultTableModel classModel;
    private ClassDAO classDAO;
    // Backup logs
private JTable backupTable;
private DefaultTableModel backupModel;
//user logs tabs
private JTable userLogsTable;
private DefaultTableModel userLogsModel;

    // Announcements tab
    private JTable announcementTable;
    private DefaultTableModel announcementModel;

    // Scheduling
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "backup-scheduler");
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> scheduledFuture;

    // Schedule UI components
    private JComboBox<String> dayCombo;
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JButton btnStartSchedule;
    private JButton btnStopSchedule;
    private JLabel lblScheduleStatus;

    public AdminDashboard(MainDashboard parent, String username) {
        this.parent = parent;
        this.username = username;
        this.userDAO = new UserDAO();
        this.classDAO = new ClassDAO();

        setTitle("Admin Dashboard - " + username);
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(parent);

        initUI();
        loadUsers();
        loadClasses();
        loadAnnouncements();

        // Clean up executor on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownScheduler();
                super.windowClosing(e);
            }
        });
    }

    private void initUI() {
        // Main header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 247, 250));
        header.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setForeground(new Color(33, 37, 41));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(lblTitle, BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            shutdownScheduler();
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        // Tabs
        tabs = new JTabbedPane();
        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Classes", createClassesPanel());
        tabs.addTab("Announcements", createAnnouncementsPanel());
        tabs.addTab("Backups", createBackupPanel());
        tabs.addTab("User Logs", createUserLogsPanel());

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ================= USERS TAB =================
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        userModel = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Role"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        userTable = new JTable(userModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                String uname = (String) getValueAt(row, 1);
                c.setBackground(DEFAULT_ADMIN_USERNAME.equals(uname) ? new Color(255, 250, 240) : Color.WHITE);
                return c;
            }
        };
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add User");
        JButton btnEdit = new JButton("Edit User");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnRefresh = new JButton("Refresh");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addUser());
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteSelectedUser());
        btnRefresh.addActionListener(e -> loadUsers());

        return panel;
    }

    private void loadUsers() {
        userModel.setRowCount(0);
        List<String> users = userDAO.getAllUsers(); // "id - username - fullName (role)"
        for (String u : users) {
            try {
                int firstDash = u.indexOf(" - ");
                int secondDash = u.indexOf(" - ", firstDash + 3);
                String id = u.substring(0, firstDash).trim();
                String uname = u.substring(firstDash + 3, secondDash).trim();
                String fullRolePart = u.substring(secondDash + 3).trim();
                int openParen = fullRolePart.lastIndexOf(" (");
                String fullName = fullRolePart.substring(0, openParen).trim();
                String role = fullRolePart.substring(openParen + 2, fullRolePart.length() - 1).trim();
                userModel.addRow(new Object[]{id, uname, fullName, role});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addUser() {
        JTextField usernameField = new JTextField();
        JTextField fullNameField = new JTextField();
        JTextField roleField = new JTextField();
        JTextField passwordField = new JTextField();
        Object[] message = {
                "Username:", usernameField,
                "Full Name:", fullNameField,
                "Role:", roleField,
                "Password:", passwordField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (userDAO.addUser(usernameField.getText(), fullNameField.getText(), roleField.getText(), passwordField.getText())) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) userTable.getValueAt(row, 0);
        String uname = (String) userTable.getValueAt(row, 1);
        String fullName = (String) userTable.getValueAt(row, 2);
        String role = (String) userTable.getValueAt(row, 3);
        if (DEFAULT_ADMIN_USERNAME.equals(uname)) {
            JOptionPane.showMessageDialog(this, "Cannot edit default admin!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTextField fullNameField = new JTextField(fullName);
        JTextField roleField = new JTextField(role);
        JTextField passwordField = new JTextField();
        Object[] message = {
                "Full Name:", fullNameField,
                "Role:", roleField,
                "Password (leave blank to keep):", passwordField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String pwd = passwordField.getText().isEmpty() ? null : passwordField.getText();
            if (userDAO.updateUser(Integer.parseInt(id), fullNameField.getText(), roleField.getText(), pwd)) {
                JOptionPane.showMessageDialog(this, "User updated successfully!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String usernameToDelete = (String) userTable.getValueAt(row, 1);
        if (DEFAULT_ADMIN_USERNAME.equals(usernameToDelete)) {
            JOptionPane.showMessageDialog(this, "Cannot delete default admin!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete user: " + usernameToDelete + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String id = (String) userTable.getValueAt(row, 0);
            if (userDAO.deleteUser(Integer.parseInt(id))) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================= CLASSES TAB =================
    private JPanel createClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        classModel = new DefaultTableModel(new String[]{"ID", "Class Name", "Created At"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        classTable = new JTable(classModel);
        panel.add(new JScrollPane(classTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Class");
        JButton btnEdit = new JButton("Edit Class");
        JButton btnDelete = new JButton("Delete Class");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnEnroll = new JButton("Enroll Students");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnEnroll);
        panel.add(btnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addClass());
        btnEdit.addActionListener(e -> editClass());
        btnDelete.addActionListener(e -> deleteClass());
        btnRefresh.addActionListener(e -> loadClasses());
        btnEnroll.addActionListener(e -> enrollStudents());

        return panel;
    }

    private void loadClasses() {
        classModel.setRowCount(0);
        List<String[]> classes = classDAO.getAllClasses(); // id, name, created_at
        for (String[] c : classes) {
            classModel.addRow(c);
        }
    }

    private void addClass() {
        String className = JOptionPane.showInputDialog(this, "Enter class name:");
        if (className != null && !className.trim().isEmpty()) {
            if (classDAO.addClass(className)) {
                JOptionPane.showMessageDialog(this, "Class added successfully!");
                loadClasses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add class.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editClass() {
        int row = classTable.getSelectedRow();
        if (row == -1) return;
        String id = (String) classTable.getValueAt(row, 0);
        String name = (String) classTable.getValueAt(row, 1);
        String newName = JOptionPane.showInputDialog(this, "Edit class name:", name);
        if (newName != null && !newName.trim().isEmpty()) {
            if (classDAO.updateClass(Integer.parseInt(id), newName)) {
                JOptionPane.showMessageDialog(this, "Class updated successfully!");
                loadClasses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update class.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteClass() {
        int row = classTable.getSelectedRow();
        if (row == -1) return;
        String id = (String) classTable.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete class: " + classTable.getValueAt(row,1) + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (classDAO.deleteClass(Integer.parseInt(id))) {
                JOptionPane.showMessageDialog(this, "Class deleted successfully!");
                loadClasses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete class.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void enrollStudents() {
        int row = classTable.getSelectedRow();
        if (row == -1) return;
        String classId = (String) classTable.getValueAt(row, 0);
        String className = (String) classTable.getValueAt(row, 1);
        List<String[]> students = userDAO.getAllStudents(); // username + full name
        String[] studentNames = new String[students.size()];
        for (int i = 0; i < students.size(); i++) {
            studentNames[i] = students.get(i)[1] + " (" + students.get(i)[2] + ")";
        }
        JList<String> list = new JList<>(studentNames);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        int option = JOptionPane.showConfirmDialog(this, scrollPane, "Enroll Students in " + className, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            StudentClassDAO scDAO = new StudentClassDAO();
            int[] selectedIndexes = list.getSelectedIndices();
            for (int index : selectedIndexes) {
                int studentId = Integer.parseInt(students.get(index)[0]);
                scDAO.enrollStudent(studentId, Integer.parseInt(classId));
            }
            JOptionPane.showMessageDialog(this, "Students enrolled successfully!");
        }
    }

    // ================= ANNOUNCEMENTS TAB =================
    private JPanel createAnnouncementsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        announcementModel = new DefaultTableModel(new String[]{"ID", "Target", "Message", "Created At"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        announcementTable = new JTable(announcementModel);
        panel.add(new JScrollPane(announcementTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnSend = new JButton("Send Announcement");
        JButton btnRefresh = new JButton("Refresh");
        btnPanel.add(btnSend);
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);

        btnSend.addActionListener(e -> sendAnnouncement());
        btnRefresh.addActionListener(e -> loadAnnouncements());

        return panel;
    }

    private void sendAnnouncement() {
        String[] options = {"STUDENTS", "LECTURERS", "ALL"};
        String target = (String) JOptionPane.showInputDialog(this, "Select target audience:", "Send Announcement",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (target == null) return;
        String message = JOptionPane.showInputDialog(this, "Enter your announcement message:");
        if (message == null || message.trim().isEmpty()) return;
        int adminId = userDAO.getLecturerIdByUsername(username); // or getUserId
        if (userDAO.sendAnnouncement("ADMIN", String.valueOf(adminId), target, message)) {
            JOptionPane.showMessageDialog(this, "Announcement sent successfully!");
            loadAnnouncements();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send announcement.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAnnouncements() {
        announcementModel.setRowCount(0);
        List<String[]> announcements = userDAO.getAllAnnouncements();
        for (String[] a : announcements) {
            announcementModel.addRow(a);
        }
    }

    // ================= BACKUP TAB =================
    private JPanel createBackupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top: manual backup buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        JButton btnBackupNow = new JButton("Backup Now");
        JButton btnZipAndUpload = new JButton("Zip & Upload Now");
        // Backup logs table
backupModel = new DefaultTableModel(
        new String[]{"ID", "User", "Type", "Files", "Status", "Timestamp"}, 0) {
    @Override
    public boolean isCellEditable(int row, int column) { return false; }
};

backupTable = new JTable(backupModel) {
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (column == 4) { // Status column
            String status = (String) getValueAt(row, column);
            if ("SUCCESS".equalsIgnoreCase(status)) c.setForeground(new Color(0, 128, 0)); // Green
            else if ("FAIL".equalsIgnoreCase(status)) c.setForeground(Color.RED);
            else c.setForeground(Color.BLACK);
        } else {
            c.setForeground(Color.BLACK);
        }
        return c;
    }
};
backupTable.setFillsViewportHeight(true);

JScrollPane logScroll = new JScrollPane(backupTable);
logScroll.setPreferredSize(new Dimension(1050, 200));
panel.add(logScroll, BorderLayout.SOUTH);

        top.add(btnBackupNow);
        top.add(btnZipAndUpload);
        panel.add(top, BorderLayout.NORTH);

        btnBackupNow.addActionListener(e -> startBackupTask(false));
        btnZipAndUpload.addActionListener(e -> startBackupTask(false)); // same action — performBackup handles steps

        // Bottom: scheduling controls
        JPanel schedulePanel = new JPanel();
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Schedule Backup (weekly)"));
        schedulePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        // Day selector
        gbc.gridx = 0; gbc.gridy = 0;
        schedulePanel.add(new JLabel("Day of week:"), gbc);
        dayCombo = new JComboBox<>(new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        gbc.gridx = 1;
        schedulePanel.add(dayCombo, gbc);

        // Time selector
        gbc.gridx = 0; gbc.gridy = 1;
        schedulePanel.add(new JLabel("Hour:"), gbc);
        hourSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 23, 1));
        gbc.gridx = 1;
        schedulePanel.add(hourSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        schedulePanel.add(new JLabel("Minute:"), gbc);
        minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        gbc.gridx = 1;
        schedulePanel.add(minuteSpinner, gbc);

        // Start / Stop buttons
        btnStartSchedule = new JButton("Start Schedule");
        btnStopSchedule = new JButton("Stop Schedule");
        btnStopSchedule.setEnabled(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        schedulePanel.add(btnStartSchedule, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        schedulePanel.add(btnStopSchedule, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        lblScheduleStatus = new JLabel("No schedule running");
        schedulePanel.add(lblScheduleStatus, gbc);

        panel.add(schedulePanel, BorderLayout.CENTER);

        // Schedule button actions
        btnStartSchedule.addActionListener(e -> onStartSchedule());
        btnStopSchedule.addActionListener(e -> onStopSchedule());
        loadBackupLogs();

        return panel;
    }

    private void onStartSchedule() {
        int dayIndex = dayCombo.getSelectedIndex(); // 0 = Sunday ... 6 = Saturday
        int hour = (Integer) hourSpinner.getValue();
        int minute = (Integer) minuteSpinner.getValue();

        scheduleWeeklyBackup(dayIndex, hour, minute);
        btnStartSchedule.setEnabled(false);
        btnStopSchedule.setEnabled(true);
        lblScheduleStatus.setText("Scheduled: " + dayCombo.getSelectedItem() + " at " + String.format("%02d:%02d", hour, minute));
    }

    private void onStopSchedule() {
        cancelScheduledBackup();
        btnStartSchedule.setEnabled(true);
        btnStopSchedule.setEnabled(false);
        lblScheduleStatus.setText("No schedule running");
    }

    private void scheduleWeeklyBackup(int dayIndexSunday0, int hour24, int minute) {
        // Convert dayIndex (0=Sunday) to Java DayOfWeek (MONDAY=1 ... SUNDAY=7)
        DayOfWeek targetDayOfWeek = DayOfWeek.of(((dayIndexSunday0 + 1) % 7) == 0 ? 7 : ((dayIndexSunday0 + 1) % 7));
        // compute initial delay
        long initialDelayMillis = computeDelayMillisToNext(targetDayOfWeek, hour24, minute);
        long periodMillis = Duration.ofDays(7).toMillis();

        // Cancel previous if exists
        cancelScheduledBackup();

        scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[Scheduler] Triggering scheduled backup at " + LocalDateTime.now());
            startBackupTask(true); // scheduled run
        }, initialDelayMillis, periodMillis, TimeUnit.MILLISECONDS);

        System.out.println("[Scheduler] Scheduled backup. First run in " + initialDelayMillis/1000 + " seconds.");
    }
    

    private long computeDelayMillisToNext(DayOfWeek targetDay, int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalTime targetTime = LocalTime.of(hour, minute);
        LocalDate nextDate = now.toLocalDate();
        int currentDowValue = now.getDayOfWeek().getValue(); // Monday=1..Sunday=7
        int targetDowValue = targetDay.getValue();
        int daysDiff = targetDowValue - currentDowValue;
        if (daysDiff < 0) daysDiff += 7;
        LocalDateTime candidate = LocalDateTime.of(nextDate.plusDays(daysDiff), targetTime);
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusDays(7);
        }
        return Duration.between(now, candidate).toMillis();
    }

    private void cancelScheduledBackup() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
            System.out.println("[Scheduler] Cancelled scheduled backup.");
        }
    }

    private void shutdownScheduler() {
        cancelScheduledBackup();
        scheduler.shutdownNow();
    }

    // Start a backup in background (manual or scheduled)
    private void startBackupTask(boolean isScheduled) {
        // disable buttons briefly
        setBackupControlsEnabled(false);
        String message = isScheduled ? "Scheduled backup started..." : "Backup started...";
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message));

        // run in background
        new Thread(() -> {
            String result = doBackupFlow();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, result);
                setBackupControlsEnabled(true);
            });
        }, "backup-runner").start();
    }

    private void setBackupControlsEnabled(boolean enabled) {
        // enable/disable schedule buttons and manual backup buttons
        btnStartSchedule.setEnabled(enabled && (scheduledFuture == null));
        btnStopSchedule.setEnabled(enabled && (scheduledFuture != null));
    }

    /**
     * The full backup flow: database backup, files backup, optional zip, upload to Google Drive,
     * insert log into DB. Returns a user-friendly result message.
     */
    private String doBackupFlow() {
        String backupDir = "C:\\ClassroomBackups";
        File backupFolder = new File(backupDir);
        if (!backupFolder.exists()) backupFolder.mkdirs();

        String dbBackup = null;
        String fileBackup = null;
        String zippedFiles = null;
        StringBuilder logBuilder = new StringBuilder();

        // 1) Database backup
        try {
            dbBackup = BackupUtility.backupDatabase("classroom_db", "root", "", backupDir);
            logBuilder.append("DB: ").append(dbBackup == null ? "FAILED" : dbBackup).append("\n");
        } catch (Exception e) {
            e.printStackTrace();
            logBuilder.append("DB: EXCEPTION\n");
        }

        // 2) Files backup
        try {
            fileBackup = BackupUtility.backupFiles("C:\\ClassroomFiles", backupDir);
            logBuilder.append("Files: ").append(fileBackup == null ? "FAILED" : fileBackup).append("\n");
        } catch (Exception e) {
            e.printStackTrace();
            logBuilder.append("Files: EXCEPTION\n");
        }

        // 3) Zip files folder (optional)
        if (fileBackup != null) {
            try {
                zippedFiles = fileBackup + ".zip";
                ZipUtility.zipFolder(fileBackup, zippedFiles);
                logBuilder.append("Zipped: ").append(zippedFiles).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
                zippedFiles = null;
                logBuilder.append("Zipping failed\n");
            }
        }

        // 4) Upload to Google Drive (inside folder "ClassroomBackups")
        String uploadedDbId = null;
        String uploadedZipId = null;
        try {
            var driveService = GoogleDriveBackup.getDriveService();
            String folderId = GoogleDriveBackup.getOrCreateFolder(driveService, "ClassroomBackups");

            if (dbBackup != null) {
                uploadedDbId = GoogleDriveBackup.uploadFileToFolder(dbBackup, new File(dbBackup).getName(), folderId);
                logBuilder.append("Uploaded DB -> ").append(uploadedDbId).append("\n");
            }
            if (zippedFiles != null) {
                uploadedZipId = GoogleDriveBackup.uploadFileToFolder(zippedFiles, new File(zippedFiles).getName(), folderId);
                logBuilder.append("Uploaded ZIP -> ").append(uploadedZipId).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logBuilder.append("Drive upload: EXCEPTION\n");
        }

        // 5) Log into DB (backup_logs)
        String combinedFiles = (dbBackup != null ? dbBackup : "") + ";" + (fileBackup != null ? fileBackup : "");
        String status = (dbBackup != null && fileBackup != null) ? "SUCCESS" : "FAIL";
        try {
            BackupLogDAO logDAO = new BackupLogDAO();
            int userId = userDAO.getUserIdByUsername(username);
            logDAO.insertLog(userId, "FULL", combinedFiles, status);
            logBuilder.append("Logged backup (status=" + status + ")\n");
        } catch (Exception e) {
            e.printStackTrace();
            logBuilder.append("Logging failed\n");
        }

        // Final message
        String result = "Backup " + status + "!\n\n" +
                "DB: " + (dbBackup != null ? dbBackup : "FAILED") + "\n" +
                "Files: " + (fileBackup != null ? fileBackup : "FAILED") + "\n" +
                (zippedFiles != null ? ("Zip: " + zippedFiles + "\n") : "") +
                (uploadedDbId != null ? ("Uploaded DB Id: " + uploadedDbId + "\n") : "") +
                (uploadedZipId != null ? ("Uploaded ZIP Id: " + uploadedZipId + "\n") : "") +
                "\nDetails:\n" + logBuilder.toString();

        System.out.println("[Backup] " + result);
        return result;
    }
    private void loadBackupLogs() {
    backupModel.setRowCount(0); // clear existing table rows

    BackupLogDAO logDAO = new BackupLogDAO();
    List<BackupLog> logs = logDAO.getAllLogs();

    for (BackupLog log : logs) {
        String username = userDAO.getUsernameById(log.getUserId()); // make sure this method exists
        backupModel.addRow(new Object[]{
            log.getId(),
            username != null ? username : log.getUserId(), // fallback if username missing
            log.getType(),
            log.getFiles() != null ? log.getFiles() : log.getLocation(),
            log.getStatus(),
            log.getBackupTime().toString()
        });
    }
}
private JPanel createUserLogsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(new EmptyBorder(12, 12, 12, 12));

    // Table model
    userLogsModel = new DefaultTableModel(
        new String[]{"Log ID", "User", "Action", "Time"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // Table
    userLogsTable = new JTable(userLogsModel);
    userLogsTable.setFillsViewportHeight(true);

    JScrollPane scroll = new JScrollPane(userLogsTable);
    panel.add(scroll, BorderLayout.CENTER);

    // Load logs initially
    loadUserLogs();

    // Optional: refresh button
    JButton btnRefresh = new JButton("Refresh Logs");
    btnRefresh.addActionListener(e -> loadUserLogs());
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanel.add(btnRefresh);
    panel.add(topPanel, BorderLayout.NORTH);

    return panel;
}

private void loadUserLogs() {
    userLogsModel.setRowCount(0);
    BackupLogDAO logDAO = new BackupLogDAO(); // Or create a separate UserLogDAO
    UserDAO userDAO = new UserDAO(); // For getting username

    List<UserLog> logs = new UserLogDAO().getAllLogs(); // You need UserLog model + DAO

    for (UserLog log : logs) {
        String username = userDAO.getUsernameById(log.getUserId());
        userLogsModel.addRow(new Object[]{
            log.getLogId(),
            username,
            log.getAction(),
            log.getActionTime()
        });
    }
}







    // ================= GETTERS =================
    public JButton getBtnLogout() { return btnLogout; }
    public JTabbedPane getTabs() { return tabs; }
    public JTable getUserTable() { return userTable; }
    public JTable getClassTable() { return classTable; }
    public JTable getAnnouncementTable() { return announcementTable; }
}
