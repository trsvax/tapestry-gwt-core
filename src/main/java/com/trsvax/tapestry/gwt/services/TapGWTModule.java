package com.trsvax.tapestry.gwt.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.ComponentClassTransformWorker;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.urlrewriter.RewriteRuleApplicability;
import org.apache.tapestry5.urlrewriter.SimpleRequestWrapper;
import org.apache.tapestry5.urlrewriter.URLRewriteContext;
import org.apache.tapestry5.urlrewriter.URLRewriterRule;

import com.trsvax.tapestry.gwt.services.gwt.GWTBindingFactory;
import com.trsvax.tapestry.gwt.services.gwt.GWTEventDispatcher;
import com.trsvax.tapestry.gwt.services.gwt.GWTEventHandler;
import com.trsvax.tapestry.gwt.services.gwt.GWTModule;
import com.trsvax.tapestry.gwt.services.gwt.GWTScriptDispatcher;
import com.trsvax.tapestry.gwt.services.gwt.GWTScriptHandler;
import com.trsvax.tapestry.gwt.services.gwt.GWTWorker;
import com.trsvax.tapestry.gwt.services.gwt.internal.GWTEventHandlerImpl;
import com.trsvax.tapestry.gwt.services.gwt.internal.GWTScriptHandlerImpl;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
public class TapGWTModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(Dispatcher.class, GWTEventDispatcher.class)
				.withId("gwtrpc");
		binder.bind(GWTEventHandler.class, GWTEventHandlerImpl.class);

		binder.bind(Dispatcher.class, GWTScriptDispatcher.class).withId(
				"gwtScript");
		binder.bind(GWTScriptHandler.class, GWTScriptHandlerImpl.class);

		binder.bind(GWTModule.class).eagerLoad();
		binder.bind(GWTBindingFactory.class);
	}

	public void contributeGWTModule(
			MappedConfiguration<String, String> configuration) {
		configuration.add("HelloGWT", "com.trsvax.gwt.hello");
	}

	public void contributeMasterDispatcher(
			OrderedConfiguration<Dispatcher> configuration,
			@InjectService("gwtrpc") Dispatcher gwtrpc,
			@InjectService("gwtScript") Dispatcher gwtScript) {
		configuration.add("gwtrpc", gwtrpc, "before:ComponentEvent");
		configuration.add("gwtScript", gwtScript, "before:Asset");

	}

	public static void contributeBindingSource(
			MappedConfiguration<String, BindingFactory> configuration,
			@InjectService("GWTBindingFactory") GWTBindingFactory gwtBindingFactory) {

		configuration.add("gwt", gwtBindingFactory);
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration
				.add(SymbolConstants.APPLICATION_VERSION, "0.0.1-SNAPSHOT");
	}

	public static void contributeComponentClassTransformWorker(
			OrderedConfiguration<ComponentClassTransformWorker> configuration) {
		configuration.addInstance("GWT", GWTWorker.class, "after:SetupRender");
	}

	static final String codeServer = System.getProperty("codeServer");

	public static void contributeURLRewriter(OrderedConfiguration configuration) {

		if (codeServer != null) {
			URLRewriterRule rule = new URLRewriterRule() {
				public Request process(Request request,
						URLRewriteContext context) {
					// if this is a component event, getPageParameters() will
					// return null.
					if (context.getPageParameters() == null)
						return request;

					// not really right but good enough for dev
					String path = request.getPath() + "?gwt.codesvr="
							+ codeServer;
					return new SimpleRequestWrapper(request, path);
				}

				public RewriteRuleApplicability applicability() {
					return RewriteRuleApplicability.OUTBOUND;
				}

			};
			configuration.add("rule1", rule);
		}
	}
}
