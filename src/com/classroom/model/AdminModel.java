package com.classroom.model;

import java.util.ArrayList;
import java.util.List;

public class AdminModel {

    // Inner classes to represent entities
    public static class User {
        private int id;
        private String username;
        private String role;

        public User(int id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }

        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }

    public static class ClassEntity {
        private int id;
        private String name;

        public ClassEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
    }

    public static class Resource {
        private int id;
        private String title;
        private String type;

        public Resource(int id, String title, String type) {
            this.id = id;
            this.title = title;
            this.type = type;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getType() { return type; }
    }

    // Data lists
    private List<User> users = new ArrayList<>();
    private List<ClassEntity> classes = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();

    public AdminModel() {
        // Sample data
        users.add(new User(1, "Student01", "Student"));
        users.add(new User(2, "Lecture01", "Lecture"));
        users.add(new User(3, "Admin01", "Admin"));

        classes.add(new ClassEntity(1, "Class 1"));
        classes.add(new ClassEntity(2, "Class 2"));

        resources.add(new Resource(1, "Math Notes", "PDF"));
        resources.add(new Resource(2, "Physics Video", "MP4"));
    }

    public List<User> getUsers() { return users; }
    public List<ClassEntity> getClasses() { return classes; }
    public List<Resource> getResources() { return resources; }
}
