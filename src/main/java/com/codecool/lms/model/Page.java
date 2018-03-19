package com.codecool.lms.model;

public abstract class Page {

    private String title;
    private String content;
    private boolean published;


    public Page(String title, String content) {
        this.title = title;
        this.content = content;
        this.published = false;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isPublished() {
        return published;
    }

    public void publish() {
        this.published = true;
    }

    public void depublish() {
        this.published = false;
    }
}