package com.example.swiftcheckin;

public class AnnouncementService {
    public static Announcement getLatestAnnouncement() {
        // This simulates fetching data, in a real scenario this could be from an API
        return new Announcement("Event Update", "New schedule for upcoming events.");
    }
}
