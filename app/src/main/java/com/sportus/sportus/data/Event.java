package com.sportus.sportus.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Event implements Parcelable {

    public String author;
    public String authorId;
    public String title;
    public String type;
    public String address;
    public String date;
    public String time;
    public String cost;
    public boolean payMethod;
    public String createdAt;
    public Double latitude;
    public Double longitude;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Event(String author, String authorId, String title, String type, String address, String date, String time, String cost,
                 boolean payMethod, String createdAt, Double latitude, Double longitude) {
        this.author = author;
        this.authorId = authorId;
        this.title = title;
        this.type = type;
        this.address = address;
        this.date = date;
        this.time = time;
        this.cost = cost;
        this.payMethod = payMethod;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Event(Parcel in) {
        title = in.readString();
        author = in.readString();
        authorId = in.readString();
        type = in.readString();
        address = in.readString();
        date = in.readString();
        time = in.readString();
        cost = in.readString();
        payMethod = in.readByte() != 0;
        createdAt = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("author", author);
        result.put("authorId", authorId);
        result.put("type", type);
        result.put("address", address);
        result.put("date", date);
        result.put("time", time);
        result.put("cost", cost);
        result.put("payMethod", payMethod);
        result.put("createdAt", createdAt);
        result.put("latitude", latitude);
        result.put("longitude", longitude);

        return result;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public boolean isPayMethod() {
        return payMethod;
    }

    public void setPayMethod(boolean payMethod) {
        this.payMethod = payMethod;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(authorId);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(address);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(cost);
        dest.writeByte((byte) (payMethod ? 1 : 0));
        dest.writeString(createdAt);
    }
}