package com.sportus.sportus.data;


import android.os.Parcel;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Participants {

    public String userId;
    public String userName;
    public String userPhoto;

    public Participants() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Participants(String userId, String userName, String userPhoto) {
        this.userId = userId;
        this.userName = userName;
        this.userPhoto = userPhoto;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("userName", userName);
        result.put("userPhoto", userPhoto);

        return result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    protected Participants(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userPhoto = in.readString();
    }

/*    public static final Creator<Participants> CREATOR = new Creator<Participants>() {
        @Override
        public Participants createFromParcel(Parcel in) {
            return new Participants(in);
        }

        @Override
        public Participants[] newArray(int size) {
            return new Participants[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userPhoto);
    }*/
}
