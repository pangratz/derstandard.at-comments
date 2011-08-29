package com.pangratz.derstandardatcomments.model;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


public class ForumFetcher {

	private final Pattern articleIdPattern;
	private final Pattern intentPattern;
	private final DateFormat dateFormat;
	private final String PERMA_LINK_FORMAT;
	private final String BASE_LINK;

	public ForumFetcher() {
		super();

		BASE_LINK = "http://derstandard.at";
		PERMA_LINK_FORMAT = BASE_LINK + "/plink/%d/%d";

		articleIdPattern = Pattern.compile(BASE_LINK + "/(\\d+).*");
		intentPattern = Pattern.compile("padding-left: (\\d+)px");
		dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Vienna"));
	}

	public Forum getForum(String link) throws IOException {
		Document doc = Jsoup.connect(link).get();
		return parseForum(link, doc);
	}

	public Forum getForum(String link, String html) {
		Document doc = Jsoup.parse(html);
		return parseForum(link, doc);
	}

	private List<ForumEntry> extractForumEntries(long articleId, Document doc) {
		Element communityCanvasEl = doc.select(".communityCanvas").first();
		List<ForumEntry> entries = new LinkedList<ForumEntry>();

		Element ratingScriptEl = communityCanvasEl.select("script").first();
		Context ctx = Context.enter();
		ctx.setLanguageVersion(Context.VERSION_1_2);
		ScriptableObject scope = ctx.initStandardObjects();
		ctx.evaluateString(scope, ratingScriptEl.data().replaceFirst("var ", ""), "ratingData", 1, null);
		Scriptable obj = (Scriptable) scope.get("ratingData", scope);

		Elements postingElements = communityCanvasEl.select(".up");
		Iterator<Element> i = postingElements.iterator();
		while (i.hasNext()) {
			ForumEntry forumEntry = new ForumEntry();
			Element element = i.next();

			String upEl = element.select(".up").first().attr("style");
			Matcher matcher = intentPattern.matcher(upEl);
			if (matcher.matches()) {
				String paddingStr = matcher.group(1);
				int padding = Integer.parseInt(paddingStr);
				int level = (padding / 7) - 1;
				forumEntry.setLevel(level);
			}

			Element postingEl = element.select(".posting08").first();

			Element nickEl = postingEl.select(".uname").first();
			String nick = nickEl.text();
			forumEntry.setNick(nick);

			Element threadIdEl = postingEl.select(".thread").first();
			String idStr = threadIdEl.id();
			idStr = idStr.replaceFirst("t", "");
			int id = Integer.parseInt(idStr);
			forumEntry.setPostId(id);

			String permaLink = String.format(PERMA_LINK_FORMAT, articleId, id);
			forumEntry.setPermaLink(permaLink);

			if (obj.has(id, obj)) {
				Scriptable dataArr = (Scriptable) obj.get(id, obj);
				Number brilliant = (Number) dataArr.get(0, dataArr);
				Number interesting = (Number) dataArr.get(1, dataArr);
				Number unnecessary = (Number) dataArr.get(2, dataArr);

				Rating rating = new Rating(brilliant.intValue(), interesting.intValue(), unnecessary.intValue());
				forumEntry.setRating(rating);
			}

			Element txtEl = postingEl.select(".txt").first();
			Element headerEl = txtEl.select("strong").first();
			String header = null;
			if (headerEl != null) {
				header = txtEl.select("strong").first().html();
				forumEntry.setHeader(header);
			}
			String posting = txtEl.select("p").first().html();
			forumEntry.setPosting(posting);

			Element dateEl = postingEl.select(".row2 .l").first();
			String dateStr = dateEl.text();
			try {
				Date postingDate = dateFormat.parse(dateStr);
				forumEntry.setDate(postingDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			entries.add(forumEntry);
		}
		return entries;
	}

	private List<String> extractPaginationLinks(Document doc) {
		// paging
		List<String> paginationLinks = new LinkedList<String>();
		Elements paging = doc.select(".pagingDirect");
		if (!paging.isEmpty()) {
			Elements pagingLinkEls = paging.first().select("a");
			Iterator<Element> it = pagingLinkEls.iterator();
			while (it.hasNext()) {
				Element pagingEl = it.next();
				if (!pagingEl.hasAttr("class")) {
					String link = pagingEl.attr("href");
					paginationLinks.add(BASE_LINK + link);
				}
			}
		}
		return paginationLinks;
	}

	protected long parseArticleId(String link) {
		Matcher matcher = articleIdPattern.matcher(link);
		if (matcher.matches()) {
			String articleIdStr = matcher.group(1);
			return Long.parseLong(articleIdStr);
		} else {
			throw new IllegalStateException("invalid link");
		}
	}

	protected Forum parseForum(String link, Document doc) {
		long articleId = parseArticleId(link);

		Forum forum = new Forum();
		forum.setForumEntries(extractForumEntries(articleId, doc));
		forum.setPaginationLinks(extractPaginationLinks(doc));

		return forum;
	}

}
