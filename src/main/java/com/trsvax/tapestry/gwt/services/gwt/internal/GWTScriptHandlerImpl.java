package com.trsvax.tapestry.gwt.services.gwt.internal;

import java.io.IOException;

import org.apache.tapestry5.internal.services.AssetResourceLocator;
import org.apache.tapestry5.internal.services.ResourceStreamer;
import org.apache.tapestry5.ioc.Resource;
import org.slf4j.Logger;

import com.trsvax.tapestry.gwt.services.gwt.GWTScriptHandler;

public class GWTScriptHandlerImpl implements GWTScriptHandler {

	private final ResourceStreamer streamer;
	private final AssetResourceLocator assetResourceLocator;
	private final String baseFolder = "";
	private final Logger logger;

	public GWTScriptHandlerImpl(ResourceStreamer streamer,
			AssetResourceLocator assetResourceLocator, Logger logger) {
		this.streamer = streamer;
		this.assetResourceLocator = assetResourceLocator;
		this.logger = logger;
	}

	public boolean handleRequest(String path) throws IOException {
		String assetPath = baseFolder + path;
		Resource resource = assetResourceLocator
				.findClasspathResourceForPath(assetPath);
		if (resource == null)
			return false;

		streamer.streamResource(resource);
		return true;
	}

}
