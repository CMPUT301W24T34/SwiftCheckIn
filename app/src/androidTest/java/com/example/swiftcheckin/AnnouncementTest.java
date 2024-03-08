package com.example.swiftcheckin;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AnnouncementTest {

    @Test
    public void testGetTitle() {
        Announcement announcement = new Announcement("Test Title", "Test Details");
        assertEquals("Test Title", announcement.getTitle());
    }

    @Test
    public void testGetDetails() {
        Announcement announcement = new Announcement("Test Title", "Test Details");
        assertEquals("Test Details", announcement.getDetails());
    }
}
