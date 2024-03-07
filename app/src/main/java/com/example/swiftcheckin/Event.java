package com.example.swiftcheckin;

import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.Settings;

import java.util.Date;

public class Event {
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String eventTitle;
    private String eventImageUrl;
    private String location;
    private String deviceId;
    private String description;
    private int maxAttendees;

    private int currentAttendees;

    public Event(){
    }

    /**
     * First constructor for event
     * @param eventTitle - Represents the title of the event.
     * @param description - Represents the description of the event.
     * @param location - Represents the location of the event.
     * @param deviceId - Represents the deviceId where this event was made.
     * @param eventImageUrl - Represents the url of the image that will act as the image poster.
     * @param startDate - Represents the starting date of the event.
     * @param endDate - Represents the ending date of the event.
     * @param startTime - Represents the time when the event starts.
     * @param endTime - Represents the time when the event ends.
     */
    public Event(String eventTitle, String description, String location, String deviceId, String eventImageUrl, String startDate, String endDate, String startTime, String endTime){

        this.eventTitle = eventTitle;
        this.location = location;
        this.description = description;
        this.eventImageUrl = eventImageUrl;
        this.deviceId = deviceId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxAttendees = -1;
    }

    /**
     * Second constructor for event
     * @param eventTitle - Represents the title of the event.
     * @param description - Represents the description of the event.
     * @param location - Represents the location of the event.
     * @param deviceId - Represents the deviceId where this event was made.
     * @param eventImageUrl - Represents the url of the image that will act as the image poster.
     * @param maxAttendees - Represents the maximum number of attendees.
     * @param startDate - Represents the starting date of the event.
     * @param endDate - Represents the ending date of the event.
     * @param startTime - Represents the time when the event starts.
     * @param endTime - Represents the time when the event ends.
     */
    public Event(String eventTitle, String description, String location, String deviceId, String eventImageUrl, String maxAttendees, String startDate, String endDate, String startTime, String endTime){

        this.eventTitle = eventTitle;
        this.location = location;
        this.description = description;
        this.eventImageUrl = eventImageUrl;
        this.deviceId = deviceId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxAttendees = Integer.parseInt(maxAttendees);
    }

    /**
     * Returns the deviceId of the event
     * @return - deviceId of the event
     */
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

    /**
     * Returns the title of the event
     * @return
     * Returns eventTitle
     */
    public String getEventTitle() {
        return eventTitle;
    }

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

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }
    // setters

    /**
     * Returns the date
     * @return
     * Returns date
     */
    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getEndTime()
    {
        return this.endTime;
    }

    /**
     * Returns the title of the event
     * @return
     * Returns eventTitle
     */

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns the event description
     * @return
     * Returns description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }


    public int getCurrentAttendees() {
        return currentAttendees;
    }

    public void incrementCurrentAttendees(){
        this.currentAttendees++;
    }
}
