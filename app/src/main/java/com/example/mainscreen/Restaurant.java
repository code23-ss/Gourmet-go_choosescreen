package com.example.mainscreen;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restaurant {
    @PropertyName("categories")
    private List<String> categories = new ArrayList<>();

    @PropertyName("category_ids")
    private List<DocumentReference> category_ids = new ArrayList<>();

    @PropertyName("contact_number")
    private String contact_number = "";

    @PropertyName("coordinates")
    private GeoPoint coordinates = new GeoPoint(0, 0);

    @PropertyName("imagePath")
    private List<String> imagePath = new ArrayList<>();

    @PropertyName("location")
    private String location = "";

    @PropertyName("menu")
    private Map<String, Map<String, String>> menu = new HashMap<>();

    @PropertyName("name")
    private String name = "";

    @PropertyName("opening_hours")
    private Map<String, Map<String, String>> opening_hours = new HashMap<>();

    @PropertyName("price_range")
    private String price_range = "";

    @PropertyName("rating")
    private double rating = 0.0;



    // 기본 생성자
    public Restaurant() {}

    // Getters and Setters

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<DocumentReference> getCategory_ids() {
        return category_ids;
    }

    public void setCategory_ids(List<DocumentReference> category_ids) {
        this.category_ids = category_ids;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public List<String> getImagePath() {
        return imagePath;
    }

    public void setImagePath(List<String> imagePath) {
        this.imagePath = imagePath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, Map<String, String>> getMenu() {
        return menu;
    }

    public void setMenu(Map<String, Map<String, String>> menu) {
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Map<String, String>> getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(Map<String, Map<String, String>> opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getPrice_range() {
        return price_range;
    }

    public void setPrice_range(String price_range) {
        this.price_range = price_range;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}

