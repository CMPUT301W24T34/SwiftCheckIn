package com.example.swiftcheckin.attendee;

/**
 * Represents an announcement containing a title and details.
 */
public class Announcement {
    private String title;
    private String details;

    /**
     * Constructs an Announcement object with the specified title and details.
     *
     * @param title   the title of the announcement
     * @param details the details of the announcement
     */
    public Announcement(String title, String details) {
        this.title = title;
        this.details = details;
    }

    /**
     * Retrieves the title of the announcement.
     *
     * @return the title of the announcement
     */
    public String getTitle() {
        return title;
    }


    /**
     * Retrieves the details of the announcement.
     *
     * @return the details of the announcement
     */
    public String getDetails() {
        return details;
    }
}

