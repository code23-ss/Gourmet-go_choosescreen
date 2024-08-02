package com.example.mainscreen;

import java.util.List;

public class Restaurant {
    private final String name;
    private final List<Integer> imageResIds;

    public Restaurant(String name, List<Integer> imageResIds) {
        this.name = name;
        this.imageResIds = imageResIds;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getImageResIds() {
        return imageResIds;
    }
}