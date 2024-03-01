package com.example.swiftcheckin;
// This represents a user's profile
/**
 * This represents a user's profile, includes their contact and personal information
 */
public class Profile {

    private String name;
    private String birthday;
    private String phoneNumber;
    private String email;
    private String website;
    private String address;
    private boolean cameraPermission;
    private boolean locationPermission;

    public Profile() {
    }

    public Profile(String name, String birthday, String phoneNumber, String email, String website, String address, boolean cameraPermission, boolean locationPermission) {
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.address = address;
        this.locationPermission = locationPermission;
        this.cameraPermission = cameraPermission;

    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public boolean isCameraPermission() {
        return cameraPermission;
    }

    public boolean isLocationPermission() {
        return locationPermission;
    }
    // Citation: OpenAI, 02-26-2024, ChatGPT, Converting String to Boolean
    // ChatGPT gave me this code boolean myBool = Boolean.parseBoolean(userInput);
    public void setCameraPermission(String cameraPermission) {
        this.cameraPermission = Boolean.parseBoolean(cameraPermission);
    }
    public void setLocationPermission(String locationPermission) {
        this.locationPermission = Boolean.parseBoolean(locationPermission);
    }
}
