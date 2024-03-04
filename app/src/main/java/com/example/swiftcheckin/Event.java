package com.example.swiftcheckin;

import android.location.Location;
import android.media.Image;
import android.provider.Settings;

import java.sql.Time;
import java.util.Date;

public class Event {
    /*
    * What are the properties of an event?
    * Needs a Date
    * Needs a Title
    * Needs an Image
    * Needs a Location
    * */
    private String date;
    private String eventTitle;
//    private Image poster;
    private String location;
    private String deviceId;
    private String description;
    private String startTime;
    private String endTime;
    private String amPM;
    private int maxAttendees;

    public Event(){
    }

    /**
     * This creates an event.

     * @param eventTitle - Represents the name of the event
     * @param location - Represents the event location.
     * @param description - Brief description of the event
     * @param deviceId - Unique identifier for the device.
     */
    public Event(String eventTitle, String description, String location, String deviceId,
                 String date, String startTime, String endTime,  String amPM){
        this.eventTitle = eventTitle;
        this.location = location;
        this.description = description;
        this.deviceId = deviceId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amPM = amPM;
    }

    public String getDeviceId() {
        return deviceId;
    }

    /**
     * This returns the maximum number of attendees.
     * @return
     * Returns maxAttendees
     */
    public int getMaxAttendees() {
        return maxAttendees;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getAmPM() {
        return amPM;
    }

    /**
     * Returns the date
     * @return
     * Returns date
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the title of the event
     * @return
     * Returns eventTitle
     */
    public String getEventTitle() {
        return eventTitle;
    }

//    public Image getPoster() {
//        return poster;
//    }
//
//    /**
//     * Sets event poster
//     * @param poster
//     * Represents a poster image.
//     */

//    public void setPoster(Image poster) {
//        this.poster = poster;
//    }

    /**
     * Returns location of the event
     * @return
     * Returns location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the event description
     * @return
     * Returns description.
     */
    public String getDescription() {
        return description;
    }
}
