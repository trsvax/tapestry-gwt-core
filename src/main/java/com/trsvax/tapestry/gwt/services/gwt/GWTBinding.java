package com.trsvax.tapestry.gwt.services.gwt;

import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.Location;

public class GWTBinding extends AbstractBinding {

	private final String description;

	private final Object value;

	public GWTBinding(Location location, String description, Object value) {
		super(location);
		this.description = description;
		this.value = value;
	}

	public Object get() {
		return "/gwt/" + value;
	}

}
