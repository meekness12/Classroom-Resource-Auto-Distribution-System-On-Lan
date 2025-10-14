package com.classroom.controller;

import com.classroom.model.LectureModel;
import com.classroom.ui.LectureDashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.UUID;

public class LectureController {

    private final LectureModel model;
    private final LectureDashboard view;

    public LectureController(LectureModel model, LectureDashboard view) {
        this.model = model;
        this.view = view;

        initController();
        refreshResourcesTable();
    }

    private void initController() {
        view.getBtnUpload().addActionListener(e -> uploadResource());
        view.getBtnRemove().addActionListener(e -> removeSelectedResource());
        view.getBtnAssign().addActionListener(e -> assignResource());
    }

    private void uploadResource() {
        String title = JOptionPane.showInputDialog(view, "Enter resource title:");
        if (title == null || title.isEmpty()) return;

        String[] types = {"PDF", "MP4", "URL"};
        String type = (String) JOptionPane.showInputDialog(view, "Select type:",
                "Resource Type", JOptionPane.PLAIN_MESSAGE, null, types, "PDF");
        if (type == null) return;

        LectureModel.Resource res = new LectureModel.Resource(
                UUID.randomUUID().toString().substring(0, 5),
                title,
                type,
                LocalDate.now().toString()
        );
        model.addResource(res);
        refreshResourcesTable();
        JOptionPane.showMessageDialog(view, "Resource uploaded successfully!");
    }

    private void removeSelectedResource() {
        JTable table = view.getResourcesTable();
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(view, "Select a resource to remove.");
            return;
        }

        String id = (String) table.getValueAt(selected, 0);
        model.removeResource(id);
        refreshResourcesTable();
    }

    private void assignResource() {
        String selectedClass = (String) view.getClassCombo().getSelectedItem();
        String selectedResource = (String) view.getResourceCombo().getSelectedItem();
        JOptionPane.showMessageDialog(view,
                "Assigned " + selectedResource + " to " + selectedClass + ".");
    }

    private void refreshResourcesTable() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getResourcesTable().getModel();
        tableModel.setRowCount(0);

        for (LectureModel.Resource res : model.getResources()) {
            tableModel.addRow(new Object[]{
                    res.getId(), res.getTitle(), res.getType(), res.getUploadedOn()
            });
        }
    }
}
