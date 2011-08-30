package com.pangratz.derstandardatcomments.model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Forum {

	private final String link;
	private int page;
	private List<ForumEntry> forumEntries;
	private List<String> paginationLinks;

	public Forum(String link) {
		super();
		this.link = link;
	}

	public List<ForumEntry> getForumEntries() {
		return forumEntries;
	}

	public String getLink() {
		return link;
	}

	public int getPage() {
		return page;
	}

	public List<String> getPaginationLinks() {
		return paginationLinks;
	}

	public void setForumEntries(List<ForumEntry> forumEntries) {
		this.forumEntries = forumEntries;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPaginationLinks(List<String> paginationLinks) {
		this.paginationLinks = paginationLinks;
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(this);
	}

}
