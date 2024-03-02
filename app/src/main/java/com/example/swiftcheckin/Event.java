package com.example.swiftcheckin;

import android.location.Location;
import android.media.Image;

import java.util.Date;

public class Event {
    /*
    * What are the properties of an event?
    * Needs a Date
    * Needs a Title
    * Needs an Image
    * Needs a Location
    * */
    private Date date;
    private String eventTitle;
//    private Image poster;

    private String location;
    private String description;
    private int maxAttendees;

    public Event(){
    }



    /**
     * This creates an event.

     * @param eventTitle - Represents the name of the event
     * @param location - Represents the event location.
     * @param description - Brief description of the event
     */
    public Event(String eventTitle, String description, String location){
        this.eventTitle = eventTitle;
        this.location = location;
        this.description = description;
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
     * Returns the date
     * @return
     * Returns date
     */
    public Date getDate() {
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

    /**
     * Returns poster of the event
     * @return
     * returns poster
     */
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
