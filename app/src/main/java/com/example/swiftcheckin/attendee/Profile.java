package com.example.swiftcheckin.attendee;
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

    private String profileImageUrl;
    private boolean locationPermission;

    private int checkInCount = 0;
    /**
     * This creates a profile
     */
    public Profile() {
    }
    /**
     * This creates a profile
     * @param name The name of the user - String
     * @param birthday The birthday of the user - String
     * @param phoneNumber the phonenumber of the user - String
     * @param email the email of the user - String
     * @param website the website of the user - String
     * @param address the address of the user - String
     * @param profileImageUrl the url of the user's profile image - String
     * @param locationPermission if the user wants to allow location use - Boolean
     *
     */
    public Profile(String name, String birthday, String phoneNumber, String email, String website,String profileImageUrl, String address, boolean locationPermission) {
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.address = address;
        this.locationPermission = locationPermission;
        this.profileImageUrl = profileImageUrl;

    }
    /**
     * This returns the user's name
     * @return
     * returns the name
     */
    public String getName() {
        return name;
    }

    /**
     * This returns the user's profile image url
     * @return
     * returns profile image url
     */
    public String getProfileImageUrl() {

        return profileImageUrl;
    }

    /**
     * This sets the users profile image url
     * @param profileImageUrl - String of profile image url
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    /**
     * This returns the user's birthday
     * @return
     * returns the birthday
     */

    public String getBirthday() {
        return birthday;
    }
    /**
     * This returns the user's phone number
     * @return
     * returns the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * This returns the user's email
     * @return
     * returns the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * This returns the user's website
     * @return
     * returns the website
     */
    public String getWebsite() {
        return website;
    }
    /**
     * This returns the user's address
     * @return
     * returns the address
     */
    public String getAddress() {
        return address;
    }
    /**
     * This returns whether the user has enabled location permission
     * @return
     * returns the bool value permission
     */
    public boolean isLocationPermission() {
        return locationPermission;
    }
    // Citation: OpenAI, 02-26-2024, ChatGPT, Converting String to Boolean
    // ChatGPT gave me this code boolean myBool = Boolean.parseBoolean(userInput);
    /**
     * This turns the inputted string to the boolean value
     * @param locationPermission - True or False if the permission is enabled - String
     */
    public void setLocationPermission(String locationPermission) {
        this.locationPermission = Boolean.parseBoolean(locationPermission);
    }

    /**
     * gets the check in count
     * @return checkInCount
     */
    public int getCheckInCount() {
        return checkInCount;
    }

    /**
     * Sets the check in count to given parameter
     * @param checkInCount
     */
    public void setCheckInCount(int checkInCount) {
        this.checkInCount = checkInCount;
    }
}
