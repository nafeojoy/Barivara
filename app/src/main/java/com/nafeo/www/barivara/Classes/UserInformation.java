package com.nafeo.www.barivara.Classes;

/**
 * Created by Friends on 5/30/2017.
 */

public class UserInformation {
    private String user_name;
    private String name;
    private String mobileno;
    private String facebookid;
    private int location_id;
    private String location;
    private String occupation;

    public UserInformation() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_no() {
        return mobileno;
    }

    public void setMobile_no(String mobile_no) {
        this.mobileno = mobileno;
    }

    public String getFacebook_id() {
        return facebookid;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebookid = facebookid;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
