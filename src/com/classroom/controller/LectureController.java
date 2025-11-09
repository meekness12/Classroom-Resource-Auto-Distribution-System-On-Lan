package com.classroom.controller;

import com.classroom.dao.ClassDAO;
import com.classroom.dao.ResourceDAO;
import com.classroom.dao.ResourceAssignmentDAO;
import com.classroom.model.LectureModel;
import com.classroom.network.LecturerClient;
import com.classroom.ui.LectureDashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;

public class LectureController {

    private final LectureModel model;
    private final LectureDashboard view;
    private final ClassDAO classDAO = new ClassDAO();
    private final ResourceDAO resourceDAO = new ResourceDAO();
    private final ResourceAssignmentDAO assignmentDAO = new ResourceAssignmentDAO();

    private final String lecturerId;

    public LectureController(LectureModel model, LectureDashboard view, String lecturerId) {
        this.model = model;
        this.view = view;
        this.lecturerId = lecturerId;

        initController();
        refreshResourcesTable();
    }

    private void initController() {
        view.getBtnUpload().addActionListener(e -> uploadResource());
        view.getBtnRemove().addActionListener(e -> removeSelectedResource());
        view.getBtnAssign().addActionListener(e -> assignResource());
    }

    private void uploadResource() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(view);

        if (result != JFileChooser.APPROVE_OPTION)
            return;

        File selectedFile = chooser.getSelectedFile();

        String type = selectedFile.getName().endsWith(".pdf") ? "PDF"
                : selectedFile.getName().endsWith(".mp4") ? "MP4" : "OTHER";

        try {
            boolean success = resourceDAO.addResource(
                    selectedFile.getName(),
                    type,
                    selectedFile.getAbsolutePath(),
                    Integer.parseInt(lecturerId)
            );

            if (success) {
                JOptionPane.showMessageDialog(view,
                        "✅ Resource saved locally on lecturer's system!");
                refreshResourcesTable();
            } else {
                JOptionPane.showMessageDialog(view,
                        "❌ Failed to save resource. Please try again.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void removeSelectedResource() {
        JTable table = view.getResourcesTable();
        int selected = table.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(view, "Select a resource to remove.");
            return;
        }

        int resourceId = Integer.parseInt(table.getValueAt(selected, 0).toString());
        try {
            if (resourceDAO.deleteResource(resourceId)) {
                JOptionPane.showMessageDialog(view, "🗑️ Resource removed successfully!");
                refreshResourcesTable();
            } else {
                JOptionPane.showMessageDialog(view, "❌ Failed to remove resource.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
        }
    }

    private void assignResource() {
        String selectedClass = (String) view.getClassCombo().getSelectedItem();
        String selectedResource = (String) view.getResourceCombo().getSelectedItem();

        if (selectedClass == null || selectedResource == null) {
            JOptionPane.showMessageDialog(view, "Select both class and resource!");
            return;
        }

        int resourceId = Integer.parseInt(selectedResource.split(" - ")[0]);
        int classId = classDAO.getClassIdByName(selectedClass);

        if (assignmentDAO.assignResourceToClass(resourceId, classId, Integer.parseInt(lecturerId)))
 {
            JOptionPane.showMessageDialog(view, "✅ Resource assigned successfully!");

            try {
                File file = new File(resourceDAO.getFilePathById(resourceId));
                LecturerClient.sendResourceToServer(file, lecturerId, selectedClass);
                JOptionPane.showMessageDialog(view,
                        "📡 Resource sent to all students in " + selectedClass);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view,
                        "⚠️ Resource assigned but failed to send to students: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(view, "❌ Failed to assign resource to class.");
        }
    }

    private void refreshResourcesTable() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getResourcesTable().getModel();
        tableModel.setRowCount(0);

        var resources = resourceDAO.getAllResourcesByLecturer(lecturerId);
        for (String[] r : resources) {
            tableModel.addRow(r);
        }
    }
}
