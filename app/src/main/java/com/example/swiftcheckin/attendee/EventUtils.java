package com.example.swiftcheckin.attendee;


/**
 *  A utility class for formatting event names to use FCM

 */
public class EventUtils {
    public static String convertEventNameToEventId(String eventName) {
        return eventName.replaceAll("\\s+", "_");
    }

    public static String convertEventIdToEventName(String eventId) {
        return eventId.replaceAll("_", " ");
    }
}