package com.example.user.markingactivity;

public class Project {
    private String name;
    private String id;

    public Project(String id) {
        this.id = id;
        this.name = id;
    }

    public Project(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


}
