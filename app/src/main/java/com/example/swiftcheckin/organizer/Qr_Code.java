package com.example.swiftcheckin.organizer;

import android.graphics.Bitmap;

public class Qr_Code {
    private Bitmap image;
    private String eventID;
    private String QrID;
    private Boolean isPromo;

    public String getEventID()
    {
        return this.eventID;
    }

    public void setEventID(String event)
    {
        this.eventID = event;
    }

    public String getQrID()
    {
        return this.QrID;
    }

    public void setQrID(String QrID)
    {
        this.QrID = QrID;
    }

    public Bitmap getImage()
    {
        return this.image;
    }

    public void setImage(Bitmap img)
    {
        this.image = img;
    }
}
