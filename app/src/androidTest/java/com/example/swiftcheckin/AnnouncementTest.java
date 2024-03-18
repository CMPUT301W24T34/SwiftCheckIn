package com.example.swiftcheckin;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.example.swiftcheckin.attendee.Announcement;

/**
 * Tests for the Announcement class.
 */
public class AnnouncementTest {

    /**
     * Test to verify the getTitle() method of the Announcement class.
     */
    @Test
    public void testGetTitle() {
        Announcement announcement = new Announcement("Test Title", "Test Details");
        assertEquals("Test Title", announcement.getTitle());
    }

    /**
     * Test to verify the getDetails() method of the Announcement class.
     */
    @Test
    public void testGetDetails() {
        Announcement announcement = new Announcement("Test Title", "Test Details");
        assertEquals("Test Details", announcement.getDetails());
    }
}
