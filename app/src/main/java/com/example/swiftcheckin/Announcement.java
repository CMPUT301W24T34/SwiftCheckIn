package com.example.swiftcheckin;

public class Announcement {
    private String title;
    private String details;

    public Announcement(String title, String details) {
        this.title = title;
        this.details = details;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }
}
