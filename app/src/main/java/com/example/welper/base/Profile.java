package com.example.welper.base;


public class Profile {
    private String userId;
    private String profileName;
    private String email;
    private String date;
    private String gender;
    private String country;
    private String fullName;
    private int level;

    public Profile() {
    }

    public Profile(String userId, String profileName, String email, String date,
                   String gender, String country, String fullName, int level) {
        this.userId = userId;
        this.profileName = profileName;
        this.email = email;
        this.date = date;
        this.gender = gender;
        this.country = country;
        this.fullName = fullName;
        this.level = level;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getEmail() {
        return email;
    }

    public int getLevel() {
        return level;
    }
}
