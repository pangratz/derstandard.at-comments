package com.pangratz.derstandardatcomments.model;

import java.util.Date;

public class ForumEntry {

	private Date date;
	private String header;
	private int level;
	private String nick;
	private String permaLink;
	private int postId;
	private String posting;
	private Rating rating;
	private int postingHash;

	public ForumEntry() {
		super();
		rating = new Rating();
	}

	public Date getDate() {
		return date;
	}

	public String getHeader() {
		return header;
	}

	public int getLevel() {
		return level;
	}

	public String getNick() {
		return nick;
	}

	public String getPermaLink() {
		return permaLink;
	}

	public int getPostId() {
		return postId;
	}

	public String getPosting() {
		return posting;
	}

	public int getPostingHash() {
		return postingHash;
	}

	public Rating getRating() {
		return rating;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setPermaLink(String permaLink) {
		this.permaLink = permaLink;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public void setPosting(String posting) {
		this.posting = posting;
		this.postingHash = this.posting.hashCode();
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

}
