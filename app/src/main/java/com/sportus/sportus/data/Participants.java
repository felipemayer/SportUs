package com.sportus.sportus.data;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Participants {

    public String userId;

    public Participants() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public Participants(String userId) {
        this.userId = userId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);

        return result;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
