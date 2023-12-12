package com.allemustafa.newsaggergator;

import android.graphics.Color;

import java.io.Serializable;

public class NewsSource implements Serializable {
    private final String title;
    private final String id;
    private final String description;
    private final String url;
    private final String category;
    private final String country;
    private final String language;
    private Color textColor;
    public NewsSource(String title, String id, String description, String url, String category, String country, String language) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.url = url;
        this.category = category;
        this.country = country;
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public int getTextColor() {

        switch (category){
            case "business": return Color.RED;
            case "entertainment": return Color.YELLOW;
            case "general": return Color.BLUE;
            case "health": return Color.GREEN;
            case "science": return Color.CYAN;
            case "sports": return Color.MAGENTA;
            case "technology": return Color.GRAY;
        }
        return Color.BLACK;
    }
}
