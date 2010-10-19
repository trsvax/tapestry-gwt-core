package com.trsvax.tapestry.gwt.services.gwt;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.services.BindingFactory;

public class GWTBindingFactory implements BindingFactory {
	private final GWTModule gwtModule;

	public GWTBindingFactory(GWTModule gwtModule) {
		this.gwtModule = gwtModule;
	}

	public Binding newBinding(String description, ComponentResources container,
			ComponentResources component, String expression, Location location) {

		return new GWTBinding(location, description, expression);
	}

}
