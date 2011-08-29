package com.pangratz.derstandardatcomments.model;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ForumFetcherTest extends TestCase {

	private ForumFetcher forumFetcher;

	public void DONTtestLocalGetForum() throws IOException {
		URL url = ForumFetcherTest.class.getResource("/test_page.html");
		String html = IOUtils.toString(url.openStream());
		String link = "http://derstandard.at/1313025313062";

		Forum forum = forumFetcher.getForum(link, html);
		assertNotNull(forum);

		List<ForumEntry> entries = forum.getForumEntries();
		assertNotNull(entries);
		assertEquals(25, entries.size());

		List<String> paginationLinks = forum.getPaginationLinks();
		assertNotNull(paginationLinks);
		assertEquals(11, paginationLinks.size());

		String secondPage = paginationLinks.get(0);
		assertNotNull(secondPage);
		assertEquals(
				"http://derstandard.at/1313025313062/Zertifikatskaeufe-Klimaschutz-wird-noch-teurer?seite=2#forumstart",
				secondPage);

		String lastPage = paginationLinks.get(10);
		assertNotNull(lastPage);
		assertEquals(
				"http://derstandard.at/1313025313062/Zertifikatskaeufe-Klimaschutz-wird-noch-teurer?seite=12#forumstart",
				lastPage);

		ForumEntry firstEntry = entries.get(0);
		assertNotNull(firstEntry);
		assertEquals("Cui bono?", firstEntry.getNick());
		assertEquals(22655395, firstEntry.getPostId());
		assertEquals(0, firstEntry.getLevel());
		assertEquals("http://derstandard.at/plink/1313025313062/22655395", firstEntry.getPermaLink());
		assertEquals("Der Klimawandel und die G&ouml;tter:", firstEntry.getHeader());
		assertEquals(
				"<a href=\"http://www.youtube.com/watch?v=6a67l3qq9MQ&amp;feature=channel_video_title\" target=\"_blank\" rel=\"nofollow\">http://www.youtube.com/watch?v=6... ideo_title</a>",
				firstEntry.getPosting());
		DateTime firstEntryDate = new DateTime(2011, 8, 28, 20, 39, 0, DateTimeZone.forID("Europe/Vienna"));
		assertEquals(firstEntryDate.toDate(), firstEntry.getDate());

		ForumEntry lastEntry = entries.get(24);
		assertNotNull(lastEntry);
		assertEquals("FriedaKistner", lastEntry.getNick());
		assertEquals(22651504, lastEntry.getPostId());
		assertEquals(1, lastEntry.getLevel());
		assertEquals("http://derstandard.at/plink/1313025313062/22651504", lastEntry.getPermaLink());
		assertEquals("Klimaschutz und Zertifikate", lastEntry.getHeader());
		System.out.println(lastEntry.getPosting());
		assertEquals(
				"haben miteinander nichts zu tun. <br /> <br />Zertifikate sind ein Gesch&auml;ft, ein Ablasshandel, eine verquere Ideologie, oder einfach nur ein schr&auml;ges Argumentarium, um Einfluss und Macht zu erlangen. <br /> <br />Dass man glaubt, mit Zertifikaten k&ouml;nnten unser Klima &quot;gesch&uuml;tzt&quot; werden, ist mehr als nur befremdlich. <br /> <br />Allein schon unser t&auml;gliches Konsumverhalten widerspricht dem &quot;Klimaschutz&quot;-Gedanken auf das Heftigste.",
				lastEntry.getPosting());
		Rating lastEntryRating = lastEntry.getRating();
		assertNotNull(lastEntryRating);
		assertEquals(1, lastEntryRating.getBrilliant());
		assertEquals(0, lastEntryRating.getInteresting());
		assertEquals(0, lastEntryRating.getUnnecessary());
		DateTime lastEntryDate = new DateTime(2011, 8, 28, 13, 30, 0, DateTimeZone.forID("Europe/Vienna"));
		assertEquals(lastEntryDate.toDate(), lastEntry.getDate());
	}

	public void testConvertToJson() throws IOException {
		URL url = ForumFetcherTest.class.getResource("/test_page.html");
		String html = IOUtils.toString(url.openStream());
		String link = "http://derstandard.at/1313025313062";
		Forum forum = forumFetcher.getForum(link, html);

		String json = forum.toJson();
		JsonObject forumJson = new JsonParser().parse(json).getAsJsonObject();

		assertTrue(forumJson.has("forumEntries"));
		JsonArray forumEntries = forumJson.get("forumEntries").getAsJsonArray();
		assertEquals(25, forumEntries.size());

		assertTrue(forumJson.has("paginationLinks"));
		JsonArray paginationLinks = forumJson.get("paginationLinks").getAsJsonArray();
		assertEquals(11, paginationLinks.size());
	}

	public void testGetAllForumEntries() throws IOException {
		long start = System.currentTimeMillis();

		String link = "http://derstandard.at/1313025357069/OeVP-bremst-bei-Telekom-U-Ausschuss";
		Forum forum = forumFetcher.getForum(link);
		List<String> paginationLinks = forum.getPaginationLinks();
		for (String paginationLink : paginationLinks) {
			forumFetcher.getForum(paginationLink);
		}

		long end = System.currentTimeMillis();
		Duration duration = new Duration(end - start);
		System.out.println(duration);
	}

	public void testRemoteGetForum() throws IOException {
		String link = "http://derstandard.at/1313025357069/OeVP-bremst-bei-Telekom-U-Ausschuss";
		Forum forum = forumFetcher.getForum(link);

		assertNotNull(forum);

		// get last link and fetch forum
		List<String> paginationLinks = forum.getPaginationLinks();
		assertNotNull(paginationLinks);
		assertTrue(paginationLinks.size() > 0);
		int size = paginationLinks.size();
		String lastPage = paginationLinks.get(size - 1);
		Forum lastPageForum = forumFetcher.getForum(lastPage);
		assertNotNull(lastPageForum);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.forumFetcher = new ForumFetcher();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.forumFetcher = null;
	}

}
