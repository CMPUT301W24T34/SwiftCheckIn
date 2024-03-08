package com.example.swiftcheckin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTest {

    @Test
    public void testGetDeviceId() {
        Event event = new Event();
        event.setDeviceId("TestDeviceId");
        assertEquals("TestDeviceId", event.getDeviceId());
    }

    @Test
    public void testGetMaxAttendees() {
        Event event = new Event();
        event.setMaxAttendees(50);
        assertEquals(50, event.getMaxAttendees());
    }

    // You can add more tests for other getters and setters in the Event class
}
