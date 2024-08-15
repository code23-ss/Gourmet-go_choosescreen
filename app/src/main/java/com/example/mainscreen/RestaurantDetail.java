package com.example.mainscreen;

import java.util.List;

public class RestaurantDetail {
    private String name;
    private String location;
    private String opening_hours_1;
    private String opening_hours_2;
    private String contact;
    private String cuisine;
    private String priceRange;
    private String cuisineButton;
    private String locationButton;
    private List<String> images;
    private List<String> menus;

    // Getters and setters for all fields including new ones

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

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getCuisineButton() {
        return cuisineButton;
    }

    public void setCuisineButton(String cuisineButton) {
        this.cuisineButton = cuisineButton;
    }

    public String getLocationButton() {
        return locationButton;
    }

    public void setLocationButton(String locationButton) {
        this.locationButton = locationButton;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getMenus() {
        return menus;
    }

    public void setMenus(List<String> menus) {
        this.menus = menus;
    }
}