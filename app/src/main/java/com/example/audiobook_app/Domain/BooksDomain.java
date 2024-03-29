package com.example.audiobook_app.Domain;

/**
 * Carousel book data
 */
public class BooksDomain {
    private String title;
    private String author;
    private String picAddress;

    public BooksDomain(String title, String author, String picAddress) {
        this.title = title;
        this.author = author;
        this.picAddress = picAddress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }
}
