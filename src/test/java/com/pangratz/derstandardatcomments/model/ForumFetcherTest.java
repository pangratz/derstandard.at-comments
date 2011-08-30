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

	public void testLocalFirstPage() throws IOException {
		URL url = ForumFetcherTest.class.getResource("/test_first_page.html");
		String html = IOUtils.toString(url.openStream());
		String link = "http://derstandard.at/1313025313062";

		Forum firstPage = forumFetcher.getForum(link, html);
		assertEquals(1, firstPage.getPage());
	}

	public void testLocalGetForum() throws IOException {
		URL url = ForumFetcherTest.class.getResource("/test_page.html");
		String html = IOUtils.toString(url.openStream());
		String link = "http://derstandard.at/1313025313062";

		Forum forum = forumFetcher.getForum(link, html);
		assertNotNull(forum);

		assertEquals(1, forum.getPage());

		List<ForumEntry> entries = forum.getForumEntries();
		assertNotNull(entries);
		assertEquals(25, entries.size());

		List<String> paginationLinks = forum.getPaginationLinks();
		assertNotNull(paginationLinks);
		assertEquals(11, paginationLinks.size());

		String secondPage = paginationLinks.get(0);
		assertNotNull(secondPage);
		assertEquals("http://derstandard.at/1313025313062/Zertifikatskaeufe-Klimaschutz-wird-noch-teurer?seite=2",
				secondPage);

		String lastPage = paginationLinks.get(10);
		assertNotNull(lastPage);
		assertEquals("http://derstandard.at/1313025313062/Zertifikatskaeufe-Klimaschutz-wird-noch-teurer?seite=12",
				lastPage);

		ForumEntry firstEntry = entries.get(0);
		assertNotNull(firstEntry);
		assertEquals("Cui bono?", firstEntry.getNick());
		assertEquals(22655395, firstEntry.getPostId());
		assertEquals(0, firstEntry.getLevel());
		assertEquals("http://derstandard.at/plink/1313025313062/22655395", firstEntry.getPermaLink());
		assertNotNull(firstEntry.getHeader());
		assertNotNull(firstEntry.getPosting());
		DateTime firstEntryDate = new DateTime(2011, 8, 28, 20, 39, 0, DateTimeZone.forID("Europe/Vienna"));
		assertEquals(firstEntryDate.toDate(), firstEntry.getDate());

		ForumEntry lastEntry = entries.get(24);
		assertNotNull(lastEntry);
		assertEquals("FriedaKistner", lastEntry.getNick());
		assertEquals(22651504, lastEntry.getPostId());
		assertEquals(1, lastEntry.getLevel());
		assertEquals("http://derstandard.at/plink/1313025313062/22651504", lastEntry.getPermaLink());
		assertNotNull(lastEntry.getHeader());
		assertNotNull(lastEntry.getPosting());
		Rating lastEntryRating = lastEntry.getRating();
		assertNotNull(lastEntryRating);
		assertEquals(1, lastEntryRating.getBrilliant());
		assertEquals(0, lastEntryRating.getInteresting());
		assertEquals(0, lastEntryRating.getUnnecessary());
		DateTime lastEntryDate = new DateTime(2011, 8, 28, 13, 30, 0, DateTimeZone.forID("Europe/Vienna"));
		assertEquals(lastEntryDate.toDate(), lastEntry.getDate());
	}

	public void testLocalLastPage() throws IOException {
		URL url = ForumFetcherTest.class.getResource("/test_last_page.html");
		String html = IOUtils.toString(url.openStream());
		String link = "http://derstandard.at/1313025313062?seite=17";

		Forum firstPage = forumFetcher.getForum(link, html);
		assertEquals(17, firstPage.getPage());
	}

	public void testNoComments() throws IOException {
		URL url = ForumFetcherTest.class.getResource("/no_comments.html");
		String html = IOUtils.toString(url.openStream());
		String link = "http://derstandard.at/1314652504760";
		Forum forum = forumFetcher.getForum(link, html);
		assertNotNull(forum);

		assertEquals("http://derstandard.at/1314652504760", forum.getLink());
		assertEquals(1, forum.getPage());

		List<ForumEntry> forumEntries = forum.getForumEntries();
		assertNotNull(forumEntries);
		assertEquals(0, forumEntries.size());

		List<String> paginationLinks = forum.getPaginationLinks();
		assertNotNull(paginationLinks);
		assertEquals(0, paginationLinks.size());
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
