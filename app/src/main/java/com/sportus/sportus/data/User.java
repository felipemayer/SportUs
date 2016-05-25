package com.sportus.sportus.data;


public class User {
    public static String TOKEN = "br.com.sportus.sportus.domain.User.TOKEN";
    public static String ID = "br.com.sportus.sportus.domain.User.ID";


    public String mName;
    public String mEmail;
    private String mPassword;
    private String mNewPassword;

    public User(){}

    public User(String username, String email) {
        this.mName = username;
        this.mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getNewPassword() {
        return mNewPassword;
    }

    public void setNewPassword(String newPassword) {
        mNewPassword = newPassword;
    }
}
