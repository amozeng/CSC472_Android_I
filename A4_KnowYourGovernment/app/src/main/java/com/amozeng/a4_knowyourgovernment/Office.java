package com.amozeng.a4_knowyourgovernment;

import java.io.Serializable;

public class Office implements Serializable {
    private String office;
    private String name;
    private String party;
    private String address;
    private String email;
    private String phone;
    private String website;
    private String photoUrls;
    private String facebookID;
    private String twitterID;
    private String youtubeID;

    public Office(String _office, String _name){
        this.office = _office;
        this.name = _name;
    }

    public String getOffice() { return office; }
    public String getName() { return this.name; }
    public String getParty() { return this.party; }
    public String getAddress() {return this.address;}
    public String getEmail() {return this.email;}
    public String getPhone(){return this.phone;}
    public String getWebsite() {return this.website;}
    public String getPhotoUrls() {return this.photoUrls;}
    public String getFacebookID() {return this.facebookID;}
    public String getTwitterID() {return this.twitterID;}
    public String getYoutubeID() {return this.youtubeID;}

    public void setParty(String pa) {this.party = pa;}
    public void setAddress(String a) {this.address = a;}
    public void setEmail(String e) {this.email = e;}
    public void setPhone(String p) {this.phone = p;}
    public void setWebsite(String w) {this.website = w;}
    public void setPhotoUrls(String ph) {this.photoUrls = ph;}
    public void setFacebookID(String f) {this.facebookID = f;}
    public void setTwitterID(String t) {this.twitterID = t;}
    public void setYoutubeID(String y) {this.youtubeID = y;}
}
