package com.thinkopen.restful.api.dtos;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String id = null;
    private int senderId = -1;
    private String content = null;
    private long date = -1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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

    @Override
    public String toString() {
        final SimpleDateFormat sdf = new SimpleDateFormat();

        return String.format("{\"id\" : \"%s\", \"senderId\" : %d, \"content\" : \"%s\", \"date\" : \"%s\"}",
                id, senderId, content, sdf.format(new Date(date)));
    }
}
