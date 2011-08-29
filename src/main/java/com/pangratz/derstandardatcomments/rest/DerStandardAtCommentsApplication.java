package com.pangratz.derstandardatcomments.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class DerStandardAtCommentsApplication extends Application {

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());

		router.attach("/forum", ForumResource.class);

		return router;
	}

}
