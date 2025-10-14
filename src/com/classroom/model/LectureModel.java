package com.classroom.model;

import java.util.ArrayList;
import java.util.List;

public class LectureModel {

    public static class Resource {
        private String id;
        private String title;
        private String type;
        private String uploadedOn;

        public Resource(String id, String title, String type, String uploadedOn) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.uploadedOn = uploadedOn;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getUploadedOn() { return uploadedOn; }
    }

    private List<Resource> resources;
    private List<String> classes;

    public LectureModel() {
        resources = new ArrayList<>();
        classes = new ArrayList<>();

        // Sample data
        resources.add(new Resource("R001", "Math Notes", "PDF", "2025-10-01"));
        resources.add(new Resource("R002", "Physics Lecture", "MP4", "2025-10-03"));
        resources.add(new Resource("R003", "Chemistry Link", "URL", "2025-10-05"));

        classes.add("Class 1");
        classes.add("Class 2");
        classes.add("Class 3");
    }

    public List<Resource> getResources() { return resources; }
    public List<String> getClasses() { return classes; }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public void removeResource(String id) {
        resources.removeIf(r -> r.getId().equals(id));
    }
}
