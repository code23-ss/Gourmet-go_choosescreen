package com.example.mainscreen;

import java.util.List;

public class RestaurantData {
    private String name;
    private String location;
    private String opening_hours_1;
    private String opening_hours_2;
    private String contact;
    private String cuisine;
    private List<String> images;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpeningHours1() {
        return opening_hours_1;
    }

    public void setOpeningHours1(String opening_hours_1) {
        this.opening_hours_1 = opening_hours_1;
    }

    public String getOpeningHours2() {
        return opening_hours_2;
    }

    public void setOpeningHours2(String opening_hours_2) {
        this.opening_hours_2 = opening_hours_2;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}