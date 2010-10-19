package com.trsvax.tapestry.gwt.pages;

import java.util.Date;

import org.apache.tapestry5.annotations.Property;

/**
 * Start page of application gwt.
 */
public class Index {

	@Property
	private String name;

	public Date getCurrentTime() {
		return new Date();
	}

	public String getGreeting() {
		return "hi there " + name + " " + getCurrentTime();
	}

	public String greetServer(String name) {

		this.name = name;
		return getGreeting();
	}

}
