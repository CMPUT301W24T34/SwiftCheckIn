package com.example.swiftcheckin.organizer;

import android.graphics.Bitmap;

public class Qr_Code {
    private Bitmap image;
    private String eventID;
    private String QrID;
    private Boolean isPromo;

    private String deviceID;

    /**
     * Constructor
     * @param QrId - String which contains the qr id
     * @param img - Image associated with this qr object
     */
    public Qr_Code(String QrId, Bitmap img)
    {
        this.image = img;
        this.QrID = QrId;
        this.isPromo = false;
    }

    /**
     * Returns the eventID associated with the qr object
     */
    public String getEventID()
    {
        return this.eventID;
    }

    /**
     * Sets the event id
     * @param event - String with the event id
     */
    public void setEventID(String event)
    {
        this.eventID = event;
    }

    /**
     * Returns the unique id of the object
     * @return String containing the qr id
     */
    public String getQrID()
    {
        return this.QrID;
    }

    /**
     * Sets the QR id
     * @param QrID- String with the qr id
     */
    public void setQrID(String QrID)
    {
        this.QrID = QrID;
    }

    /**
     * Returns the image associated
     * @return Bitmap image of the qr
     */
    public Bitmap getImage()
    {
        return this.image;
    }

    /**
     * Sets the Bitmap image to the qr object
     * @param img - Bitmap image
     */
    public void setImage(Bitmap img)
    {
        this.image = img;
    }

    /**
     * Sets if the qr code is a promotional qr code or not
     * @param flag - Boolean
     */
    public void setIsPromo(Boolean flag)
    {
        this.isPromo = flag;
    }

    /**
     * Returns if the qr is a promotional qr code
     * @return - Boolean flag
     */
    public Boolean getIsPromo()
    {
        return this.isPromo;
    }

    /**
     * Ends the link between the qr object with event object
     */
    public void setEventToNull()
    {
        this.eventID = "null";
    }

    /**
     * Sets the device Id of the generating device
     * @param deviceID -String containing the device ID
     */
    public void setDeviceID(String deviceID)
    {
        this.deviceID = deviceID;
    }

    /**
     * Returns the device ID of the generating device
     * @return String that represents the device id
     */
    public String getDeviceID()
    {
        return this.deviceID;
    }
}
