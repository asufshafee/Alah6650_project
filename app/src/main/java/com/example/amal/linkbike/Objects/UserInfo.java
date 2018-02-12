package com.example.amal.linkbike.Objects;

import android.app.Application;

/**
 * Created by jaani on 11/23/2017.
 */

public class UserInfo extends Application {

    String id;
    String Name;
    String Email;
    Double Wallet=0.0;
    Boolean payment=false;
    Boolean RideStatus=false;
    Boolean ProfileLoadCheck=false;

    public Boolean getProfileLoadCheck() {
        return ProfileLoadCheck;
    }

    public void setProfileLoadCheck(Boolean profileLoadCheck) {
        ProfileLoadCheck = profileLoadCheck;
    }

    public String getProfileUrl() {
        return ProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        ProfileUrl = profileUrl;
    }

    String ProfileUrl;

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    String Profile;

    public Boolean getRideStatus() {
        return RideStatus;
    }

    public void setRideStatus(Boolean rideStatus) {
        RideStatus = rideStatus;
    }

    public Boolean getPayment() {
        return payment;
    }

    public void setPayment(Boolean payment) {
        this.payment = payment;
    }

    public Double getWallet() {
        return Wallet;
    }

    public void setWallet(Double wallet) {
        Wallet = wallet;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MyAppUser GetUser() {
        MyAppUser myAppUser=new MyAppUser();
        myAppUser.setWallet(this.Wallet);
        myAppUser.setId(this.id);
        myAppUser.setPayment(this.payment);
        myAppUser.setEmail(this.Email);
        myAppUser.setName(this.Name);
        myAppUser.setProfile(this.Profile);
        return myAppUser;

    }


}
