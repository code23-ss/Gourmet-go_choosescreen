package com.example.mainscreen;

// Guide.java
public class Guide {

    private int image;
    private String title;
    private String content;

    public Guide(int image, String title, String content) {
        this.image = image;
        this.title = title;
        this.content = content;
    }


    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
