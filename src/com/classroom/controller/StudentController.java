package com.classroom.controller;

import com.classroom.model.StudentModel;
import com.classroom.ui.StudentDashboard;

import javax.swing.table.DefaultTableModel;

public class StudentController {

    private final StudentModel model;
    private final StudentDashboard view;

    public StudentController(StudentModel model, StudentDashboard view) {
        this.model = model;
        this.view = view;

        initController();
    }

    private void initController() {
        loadResources();
        view.getBtnDownload().addActionListener(e -> downloadResource());
        view.getBtnCache().addActionListener(e -> cacheResource());
    }

    private void loadResources() {
        DefaultTableModel tableModel = (DefaultTableModel) view.getResourcesTable().getModel();
        tableModel.setRowCount(0); // Clear existing rows

        for (StudentModel.Resource res : model.getResources()) {
            Object[] row = {res.getId(), res.getTitle(), res.getType(), res.getAssignedOn()};
            tableModel.addRow(row);
        }
    }

    private void downloadResource() {
        int selectedRow = view.getResourcesTable().getSelectedRow();
        if (selectedRow == -1) return;

        String title = (String) view.getResourcesTable().getValueAt(selectedRow, 1);
        System.out.println("Downloading: " + title);
        // TODO: implement actual file download logic
    }

    private void cacheResource() {
        int selectedRow = view.getResourcesTable().getSelectedRow();
        if (selectedRow == -1) return;

        String title = (String) view.getResourcesTable().getValueAt(selectedRow, 1);
        System.out.println("Caching offline: " + title);
        // TODO: implement offline caching
    }
}
