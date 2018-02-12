package com.example.amal.linkbike.Objects;

import android.app.Application;

/**
 * Created by jaani on 11/20/2017.
 */

public class MyAppUser {

    String id="";
    String Name="";
    String Email="";
    Double Wallet=0.0;
    Boolean payment=false;
    String Profile;

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public Boolean getPayment() {
        return payment;
    }

    public void setPayment(Boolean payment) {
        this.payment = payment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
