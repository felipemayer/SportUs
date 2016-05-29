package com.sportus.sportus.data;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Event {

    public String eventId;
    public String title;
    public String type;
    public String address;
    public String date;
    public String time;
    public String cost;
    public boolean payMethod;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Event(String title, String type, String address, String date, String time, String cost, boolean payMethod) {
        this.title = title;
        this.type = type;
        this.address = address;
        this.date = date;
        this.time = time;
        this.cost = cost;
        this.payMethod = payMethod;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("type", type);
        result.put("address", address);
        result.put("date", date);
        result.put("time", time);
        result.put("cost", cost);
        result.put("payMethod", payMethod);

        return result;
    }

}