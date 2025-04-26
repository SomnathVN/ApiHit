package com.example.apihit;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "news_table")
public class NewsEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String source;
    private String author;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

    public NewsEntity(String title, String source, String author, String description, String url, String urlToImage, String publishedAt) {
        this.title = title;
        this.source = source;
        this.author = author;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getUrlToImage() { return urlToImage; }
    public String getPublishedAt() { return publishedAt; }
}
