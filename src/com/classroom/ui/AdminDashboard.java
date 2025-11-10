package com.classroom.ui;

import com.classroom.dao.UserDAO;
import com.classroom.dao.ClassDAO;
import com.classroom.dao.StudentClassDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(lblTitle, BorderLayout.WEST);

        btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        // Tabs
        tabs = new JTabbedPane();
        tabs.addTab("Users", createUsersPanel());
        tabs.addTab("Classes", createClassesPanel());
        tabs.addTab("Resources", new JPanel());
        tabs.addTab("Backups", new JPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ================= USERS TAB =================
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        userModel = new DefaultTableModel(new String[]{"ID", "Username", "Full Name", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                String uname = (String) getValueAt(row, 1);
                if (DEFAULT_ADMIN_USERNAME.equals(uname)) {
                    c.setBackground(new Color(255, 230, 230));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };

        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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
        classModel = new DefaultTableModel(new String[]{"ID", "Class Name", "Created At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        classTable = new JTable(classModel);
        JScrollPane scrollPane = new JScrollPane(classTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a class to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
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
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a class to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
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

    // ================= ENROLL STUDENTS =================
    private void enrollStudents() {
        int row = classTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a class to enroll students.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

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

    // ================= GETTERS FOR CONTROLLER =================
    public JButton getBtnLogout() {
        return btnLogout;
    }

    public JTabbedPane getTabs() {
        return tabs;
    }

    public JTable getUserTable() {
        return userTable;
    }

    public JTable getClassTable() {
        return classTable;
    }
}
