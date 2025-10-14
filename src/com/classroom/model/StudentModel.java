package com.classroom.model;

import java.util.ArrayList;
import java.util.List;

public class StudentModel {

    // Resource class
    public static class Resource {
        private String id;
        private String title;
        private String type;
        private String assignedOn;

        public Resource(String id, String title, String type, String assignedOn) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.assignedOn = assignedOn;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getAssignedOn() { return assignedOn; }
    }

    private List<Resource> resources;
    private List<String> announcements;

    public StudentModel() {
        resources = new ArrayList<>();
        announcements = new ArrayList<>();

        // Sample data
        resources.add(new Resource("R001", "Math Notes", "PDF", "2025-10-01"));
        resources.add(new Resource("R002", "Physics Lecture", "MP4", "2025-10-03"));

        announcements.add("Exam on Monday");
        announcements.add("Submit assignments by Friday");
    }

    public List<Resource> getResources() { return resources; }
    public List<String> getAnnouncements() { return announcements; }

    public void addResource(Resource resource) { resources.add(resource); }
    public void removeResource(String id) { resources.removeIf(r -> r.getId().equals(id)); }
    public void addAnnouncement(String text) { announcements.add(text); }
}
