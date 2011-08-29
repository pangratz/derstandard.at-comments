package com.pangratz.derstandardatcomments.rest;

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.pangratz.derstandardatcomments.model.Forum;
import com.pangratz.derstandardatcomments.model.ForumFetcher;

public class ForumResource extends ServerResource {

	private ForumFetcher mForumFetcher;

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();

		this.mForumFetcher = new ForumFetcher();

		getVariants(Method.GET).add(new Variant(MediaType.APPLICATION_JSON));
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		// get the forum url
		String url = getQuery().getFirstValue("url");

		try {
			Forum forum = mForumFetcher.getForum(url);
			return new JsonRepresentation(forum.toJson());
		} catch (IOException e) {
			return new StringRepresentation("error: " + e.getMessage());
		}
	}
}
