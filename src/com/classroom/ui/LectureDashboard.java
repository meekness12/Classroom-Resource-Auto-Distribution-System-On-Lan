package com.classroom.ui;

import com.classroom.dao.ClassDAO;
import com.classroom.dao.ResourceDAO;
import com.classroom.dao.ResourceAssignmentDAO;
import com.classroom.network.LecturerClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class LectureDashboard extends JFrame {

    private final JFrame parent;
    private final int lecturerId;

    private JButton btnUpload, btnRemove, btnAssign, btnChooseFile;
    private JTable tblResources;
    private JComboBox<String> cmbClass, cmbResource;
    private JTabbedPane tabs;
    private File selectedFile;

    private final ClassDAO classDAO = new ClassDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();
    private final ResourceAssignmentDAO assignmentDAO = new ResourceAssignmentDAO();

    public LectureDashboard(JFrame parent, int lecturerId) {
        this.parent = parent;
        this.lecturerId = lecturerId;

        setTitle("Lecture Dashboard - ID: " + lecturerId);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        loadClasses();
        loadResources();
        initActions();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setPreferredSize(new Dimension(1000, 70));

        JLabel lblTitle = new JLabel("📘 Lecture Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(lblTitle, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("Welcome, Lecturer ID: " + lecturerId);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblWelcome, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(244, 67, 54));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.addActionListener(e -> {
            this.dispose();
            parent.setVisible(true);
        });
        header.add(btnLogout, BorderLayout.EAST);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabs.addTab("Upload Resource", createUploadPanel());
        tabs.addTab("My Resources", createMyResourcesPanel());
        tabs.addTab("Assign to Class", createAssignPanel());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createUploadPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblInfo = new JLabel("<html>Select a file (PDF, MP4, or URL) and upload it.<br>Files remain local until assigned.</html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        main.add(lblInfo, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnChooseFile = new JButton("Choose File");
        btnUpload = new JButton("Upload (Local)");
        btnUpload.setBackground(new Color(76, 175, 80));
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setFocusPainted(false);

        btnPanel.add(btnChooseFile);
        btnPanel.add(btnUpload);
        main.add(btnPanel, BorderLayout.SOUTH);

        return main;
    }

    private JPanel createMyResourcesPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = {"Resource ID", "Title", "Type", "File Path"};
        tblResources = new JTable(new DefaultTableModel(columns, 0));
        tblResources.setFillsViewportHeight(true);
        tblResources.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblResources.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tblResources);
        main.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnRemove = new JButton("Remove Resource");
        btnRemove.setBackground(new Color(244, 67, 54));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.setFocusPainted(false);
        btnPanel.add(btnRemove);
        main.add(btnPanel, BorderLayout.SOUTH);

        return main;
    }

    private JPanel createAssignPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.add(new JLabel("Select Class:"));
        cmbClass = new JComboBox<>();
        formPanel.add(cmbClass);

        formPanel.add(new JLabel("Select Resource:"));
        cmbResource = new JComboBox<>();
        formPanel.add(cmbResource);

        btnAssign = new JButton("Assign Resource");
        btnAssign.setBackground(new Color(76, 175, 80));
        btnAssign.setForeground(Color.WHITE);
        btnAssign.setFocusPainted(false);

        formPanel.add(new JLabel());
        formPanel.add(btnAssign);

        main.add(new JLabel("Assign uploaded resources to a class:", SwingConstants.CENTER), BorderLayout.NORTH);
        main.add(formPanel, BorderLayout.CENTER);

        return main;
    }

    private void initActions() {
        btnChooseFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                JOptionPane.showMessageDialog(this, "Selected: " + selectedFile.getName());
            }
        });

        btnUpload.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Please select a file first!");
                return;
            }

            try {
                String type = selectedFile.getName().endsWith(".pdf") ? "PDF" :
                        selectedFile.getName().endsWith(".mp4") ? "MP4" : "URL";

                boolean success = resourceDAO.addResource(
                        selectedFile.getName(),
                        type,
                        selectedFile.getAbsolutePath(),
                        lecturerId
                );

                if (success) {
                    JOptionPane.showMessageDialog(this, "Resource uploaded locally!");
                    loadResources();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to upload resource.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnAssign.addActionListener(e -> {
            String selectedResource = (String) cmbResource.getSelectedItem();
            String selectedClass = (String) cmbClass.getSelectedItem();

            if (selectedResource == null || selectedClass == null) {
                JOptionPane.showMessageDialog(this, "Select both class and resource!");
                return;
            }

            int resourceId = Integer.parseInt(selectedResource.split(" - ")[0]);

            int classId = classDAO.fetchAllClasses().stream()
                    .filter(c -> c[1].equals(selectedClass))
                    .findFirst()
                    .map(c -> Integer.parseInt(c[0]))
                    .orElse(-1);

            if (classId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid class selected.");
                return;
            }

            if (assignmentDAO.assignResourceToClass(resourceId, classId, lecturerId)) {
                JOptionPane.showMessageDialog(this, "Resource assigned successfully!");

                try {
                    File file = new File(resourceDAO.getFilePathById(resourceId));
                    LecturerClient.sendResourceToServer(file, String.valueOf(lecturerId), selectedClass);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to send resource to students: " + ex.getMessage());
                    ex.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign resource.");
            }
        });
    }

    private void loadClasses() {
        cmbClass.removeAllItems();
        classDAO.fetchAllClasses().forEach(c -> cmbClass.addItem(c[1]));
    }

    private void loadResources() {
        cmbResource.removeAllItems();
        DefaultTableModel model = new DefaultTableModel(new String[]{"Resource ID", "Title", "Type", "File Path"}, 0);
        tblResources.setModel(model);

        resourceDAO.getAllResourcesByLecturer(String.valueOf(lecturerId)).forEach(r -> {
            model.addRow(r);
            cmbResource.addItem(r[0] + " - " + r[1] + " (" + r[2] + ")");
        });
    }

    // ------------ Getters used by controller ------------
    public JButton getBtnUpload() { return btnUpload; }
    public JButton getBtnRemove() { return btnRemove; }
    public JButton getBtnAssign() { return btnAssign; }
    public JTable getResourcesTable() { return tblResources; }
    public JComboBox<String> getClassCombo() { return cmbClass; }
    public JComboBox<String> getResourceCombo() { return cmbResource; }
    public JTabbedPane getTabs() { return tabs; }
}
