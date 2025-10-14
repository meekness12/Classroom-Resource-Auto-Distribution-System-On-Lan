package com.classroom.controller;

import com.classroom.model.AdminModel;
import com.classroom.ui.AdminDashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AdminController {

    private AdminModel model;
    private AdminDashboard view;

    public AdminController(AdminModel model, AdminDashboard view) {
        this.model = model;
        this.view = view;
        initController();
    }

    private void initController() {
        view.getBtnLogout().addActionListener(e -> logout());
        setupTabs();
    }

    private void setupTabs() {
        setupUsersTab();
        setupClassesTab();
        setupResourcesTab();
        setupBackupsTab();
    }

    private void setupUsersTab() {
        String[] columns = {"ID", "Username", "Role"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        for (AdminModel.User user : model.getUsers()) {
            tableModel.addRow(new Object[]{user.getId(), user.getUsername(), user.getRole()});
        }
        JTable table = new JTable(tableModel);
        view.getTabs().setComponentAt(0, new JScrollPane(table));
    }

    private void setupClassesTab() {
        String[] columns = {"Class ID", "Class Name"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        for (AdminModel.ClassEntity cls : model.getClasses()) {
            tableModel.addRow(new Object[]{cls.getId(), cls.getName()});
        }
        JTable table = new JTable(tableModel);
        view.getTabs().setComponentAt(1, new JScrollPane(table));
    }

    private void setupResourcesTab() {
        String[] columns = {"Resource ID", "Title", "Type"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        for (AdminModel.Resource res : model.getResources()) {
            tableModel.addRow(new Object[]{res.getId(), res.getTitle(), res.getType()});
        }
        JTable table = new JTable(tableModel);
        view.getTabs().setComponentAt(2, new JScrollPane(table));
    }

    private void setupBackupsTab() {
        JPanel backupPanel = new JPanel();
        backupPanel.add(new JLabel("Backup management goes here."));
        view.getTabs().setComponentAt(3, backupPanel);
    }

    private void logout() {
        view.dispose();
        JOptionPane.showMessageDialog(null, "Logged out successfully!");
        // TODO: redirect to MainDashboard
    }
}
