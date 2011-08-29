package com.pangratz.derstandardatcomments.model;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Forum {

	private List<ForumEntry> forumEntries;
	private List<String> paginationLinks;

	public List<ForumEntry> getForumEntries() {
		return forumEntries;
	}

	public List<String> getPaginationLinks() {
		return paginationLinks;
	}

	public void setForumEntries(List<ForumEntry> forumEntries) {
		this.forumEntries = forumEntries;
	}

	public void setPaginationLinks(List<String> paginationLinks) {
		this.paginationLinks = paginationLinks;
	}

	public String toJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return gson.toJson(this);
	}

}
