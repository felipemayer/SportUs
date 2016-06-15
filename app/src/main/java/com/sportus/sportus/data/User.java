package com.sportus.sportus.data;


import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String name;
    public String email;
    public String local;
    public String age;
    public Uri photo;
    public List<String> interests;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /*public User(String name, String email, Uri photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;
    }*/

    public User(String name, String email, Uri photo, String local, String age, List<String> interests) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.local = local;
        this.age = age;
        this.interests = interests;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("photo", photo);
        result.put("local", local);
        result.put("age", age);
        result.put("interests", interests);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}
