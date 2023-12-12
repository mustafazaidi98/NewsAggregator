package com.allemustafa.newsaggergator;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NewsArticles implements Serializable {
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String urlToImage;
    private final String publishedAt;
    private final String channel;

    public NewsArticles(String author, String title, String description, String url, String urlToImage, String publishedAt, String channel) {
        String publishedAt1;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        outputFormat.setTimeZone(TimeZone.getDefault()); // Set the desired time zone here
        Date date = null;
        publishedAt1 = publishedAt;
        try {
            date = inputFormat.parse(publishedAt);
            publishedAt1 = outputFormat.format(date);
        } catch (ParseException e) {
        }

        this.publishedAt = publishedAt1;
        this.channel = channel;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getChannel() {
        return channel;
    }
}
