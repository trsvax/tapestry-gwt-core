package com.trsvax.tapestry.gwt.services.gwt;

import java.io.IOException;

import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

public class GWTScriptDispatcher implements Dispatcher {

	private final String pathPrefix = "/gwt";
	private final GWTScriptHandler gwtScriptHandler;
	private final Logger logger;

	public GWTScriptDispatcher(GWTScriptHandler gwtScriptHandler, Logger logger) {
		this.gwtScriptHandler = gwtScriptHandler;
		this.logger = logger;
	}

	public boolean dispatch(Request request, Response response)
			throws IOException {

		String path = request.getPath();

		if (!path.startsWith(pathPrefix))
			return false;

		logger.info("gwt path {}", path);

		String virtualPath = path.substring(pathPrefix.length());
		return gwtScriptHandler.handleRequest(virtualPath);
	}

}
