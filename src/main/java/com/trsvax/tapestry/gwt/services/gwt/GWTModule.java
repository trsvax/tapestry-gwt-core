package com.trsvax.tapestry.gwt.services.gwt;

import java.util.Map;

import org.slf4j.Logger;

public class GWTModule {
	private final Map<String, String> configuration;

	public GWTModule(Logger logger, Map<String, String> configuraton) {
		logger.info("config {}", configuraton);
		this.configuration = configuraton;
	}

	public Map<String, String> getModules() {
		return configuration;
	}

}
