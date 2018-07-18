package com.thinkopen.restful.api.dtos;

public class Comment {

	private int id;
	private int postId;
	private int userId;
	private String text;
	private long date;
	
	public Comment() {}
	
	@Override
	public String toString() {
		return "Comment [id=" + id + ", postId=" + postId + ", userId=" + userId + ", content=" + text + ", date=" + date
				+ "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getContent() {
		return text;
	}

	public void setContent(String text) {
		this.text = text;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	
}
