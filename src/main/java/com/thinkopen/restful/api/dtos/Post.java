package com.thinkopen.restful.api.dtos;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
    private int id;
    private int userId;
    private String title, content;
    private long date;
    private boolean isClosed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public String toString() {
        SimpleDateFormat sdt = new SimpleDateFormat();
        return String.format("ID: %d, USER-ID: %d, TITOLO: %s, CONTENUTO: %s, DATA: %s, CHIUSO: %s",
                id, userId, title, content, sdt.format(new Date(date)), isClosed);
    }
}
