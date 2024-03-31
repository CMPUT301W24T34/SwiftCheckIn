package com.example.swiftcheckin.organizer;

import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.provider.Settings;

import java.util.Date;

/**
 * Represents an event that will be accessed and manipulated by other classes.
 */
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
    private String qrID;
    private String qrPromoId;
    private int maxAttendees;

    private int currentAttendees = 0;

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
        this.maxAttendees = -1;   // Meant to act as a representative for no limit.
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

        // we will not add qr id here in constructor but instead add it later using setter function
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
     * Sets the deviceId of the event
     * @param deviceId - deviceId to be set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
     * Sets the maximum number of attendees attribute.
     * @param maxAttendees - Represents the maximum number of attendees that can attend an event.
     */
    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
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
     * Sets the title of an event
     * @param eventTitle - Sets the event title.
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
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
     * Sets the location of the event
     * @param location - Represents the location of the event.
     */
    public void setLocation(String location) {
        this.location = location;
    }


    /**
     * Returns the event description
     * @return
     * Returns description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the event description
     * @param description - Represents the event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the image URL
     * @return - Returns the image URL (event poster)
     */
    public String getEventImageUrl() {
        return eventImageUrl;
    }

    /**
     * Sets the image URL
     * @param eventImageUrl - Represents the image URL (event poster)
     */
    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    /**
     * Returns the start date of the event
     * @return - Returns the start date of event.
     */
    public String getStartDate() {
        return this.startDate;
    }

    /**
     * Sets the Start Date of the event
     * @param startDate - Represents the starting date of the event.
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the End Date of the event
     * @return - Returns the End Date of the Event
     */
    public String getEndDate()
    {
        return this.endDate;
    }
    /**
     * Sets the Start Date of the event
     * @param endDate - Represents the ending date of the event.
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Returns the Starting Time of the event
     * @return - Returns the start time of event.
     */
    public String getStartTime() {
        return this.startTime;
    }

    /**
     * Sets the starting time of the event
     * @param startTime - Represents the starting time of the event.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the ending time of the event.
     * @return - Returns the end time of the event.
     */
    public String getEndTime()
    {
        return this.endTime;
    }

    /**
     * Sets the end time of the event
     * @param endTime - Represents the ending time of an event.
     */
    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    /**
     * Returns the current number of attendees
     * @return - Returns how many attendees there are in an event.
     */
    public int getCurrentAttendees() {
        return currentAttendees;
    }

    /**
     * Increments the number of current attendees when someone join (not fully implemented yet.
     */
    public void setCurrentAttendees(int currentAttendees) {
        this.currentAttendees = currentAttendees;
    }

    /**
     * Sets the associated QR id.
     * @param qrID - String representing the qr id.
     */
    public void setQrID(String qrID)
    {
        this.qrID = qrID;
    }

    /**
     * Returns the qr id of the associated qr code.
     * @return - Returns qr id(String).
     */
    public String getQrID()
    {
        return this.qrID;
    }

    /**
     * Sets the associated promotional QR id.
     * @param qrID - String representing the qr id.
     */
    public void setQrPromoID(String qrID)
    {
        this.qrPromoId = qrID;
    }

    /**
     * Returns the promotional qr id of the associated qr code.
     * @return - Returns qr id(String).
     */
    public String getQrPromoID()
    {
        return this.qrPromoId;
    }
}
