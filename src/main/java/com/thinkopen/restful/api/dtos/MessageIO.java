package com.thinkopen.restful.api.dtos;

public class MessageIO {
    private String msgId = null;
    private int userId = -1;
    private Boolean read = false, del = false;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean isDeleted() {
        return del;
    }

    public void setDeleted(Boolean del) {
        this.del = del;
    }
}
