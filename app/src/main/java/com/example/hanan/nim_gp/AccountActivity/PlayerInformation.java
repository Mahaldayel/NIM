package com.example.hanan.nim_gp.AccountActivity;

public class PlayerInformation {

    private  String username,email,birthDate,picURL,countyCode;
    private boolean online;
    private int score;

    public PlayerInformation(){
        this.username="";
        this.email="";
        this.birthDate="";
        this.picURL="";
        this.countyCode="";
        this.online=false;
        this.score=0;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public int getScore() {
        return score;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPicURL() {
        return picURL;
    }

    public String getUsername() {
        return username;
    }
}
