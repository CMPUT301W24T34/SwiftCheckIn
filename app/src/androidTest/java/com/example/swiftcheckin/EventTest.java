package com.example.swiftcheckin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.example.swiftcheckin.organizer.Event;

public class EventTest {

    /**
     * Test for getting the device ID.
     */
    @Test
    public void testGetDeviceId() {
        Event event = new Event();
        event.setDeviceId("TestDeviceId");
        assertEquals("TestDeviceId", event.getDeviceId());
    }

    /**
     * Test for getting the maximum number of attendees.
     */
    @Test
    public void testGetMaxAttendees() {
        Event event = new Event();
        event.setMaxAttendees(50);
        assertEquals(50, event.getMaxAttendees());
    }

    /**
     * Test for getting the event title.
     */
    @Test
    public void testGetEventTitle() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-01", "10:00", "12:00");
        assertEquals("Test Event", event.getEventTitle());
    }

    /**
     * Test for getting the location of the event.
     */
    @Test
    public void testGetLocation() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-01", "10:00", "12:00");
        assertEquals("Location", event.getLocation());
    }

    /**
     * Test for getting the description of the event.
     */
    @Test
    public void testGetDescription() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-01", "10:00", "12:00");
        assertEquals("Description", event.getDescription());
    }

    /**
     * Test for getting the URL of the event image.
     */
    @Test
    public void testGetEventImageUrl() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-01", "10:00", "12:00");
        assertEquals("ImageUrl", event.getEventImageUrl());
    }

    /**
     * Test for getting the start date of the event.
     */
    @Test
    public void testGetStartDate() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-01", "10:00", "12:00");
        assertEquals("2024-03-01", event.getStartDate());
    }

    /**
     * Test for getting the end date of the event.
     */
    @Test
    public void testGetEndDate() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-02", "10:00", "12:00");
        assertEquals("2024-03-02", event.getEndDate());
    }

    /**
     * Test for getting the start time of the event.
     */
    @Test
    public void testGetStartTime() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-01", "10:00", "12:00");
        assertEquals("10:00", event.getStartTime());
    }

    /**
     * Test for getting the end time of the event.
     */
    @Test
    public void testGetEndTime() {
        Event event = new Event("Test Event", "Description", "Location", "DeviceId", "ImageUrl", "2024-03-01", "2024-03-01", "10:00", "12:00");
        assertEquals("12:00", event.getEndTime());
    }

}
