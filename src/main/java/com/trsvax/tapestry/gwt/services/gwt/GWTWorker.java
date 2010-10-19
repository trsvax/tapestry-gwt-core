package com.trsvax.tapestry.gwt.services.gwt;

import java.util.ArrayList;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.services.ClassTransformation;
import org.apache.tapestry5.services.ComponentClassTransformWorker;
import org.apache.tapestry5.services.ComponentMethodAdvice;
import org.apache.tapestry5.services.ComponentMethodInvocation;
import org.apache.tapestry5.services.TransformConstants;
import org.apache.tapestry5.services.TransformMethod;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;

import com.trsvax.tapestry.gwt.annotation.GWT;

public class GWTWorker implements ComponentClassTransformWorker {
	private final Logger logger;
	private final JavaScriptSupport javaScriptSupport;

	public GWTWorker(Logger logger, JavaScriptSupport javaScriptSupport) {
		this.logger = logger;
		this.javaScriptSupport = javaScriptSupport;
	}

	public void transform(ClassTransformation transformation,
			MutableComponentModel model) {
		processClassAnnotationAtSetupRenderPhase(transformation, model);

	}

	private void processClassAnnotationAtSetupRenderPhase(
			ClassTransformation transformation, MutableComponentModel model) {
		GWT annotation = transformation.getAnnotation(GWT.class);

		if (annotation == null)
			return;

		model.enableSupportsInformalParameters();

		TransformMethod beginRender = transformation
				.getOrCreateMethod(TransformConstants.BEGIN_RENDER_SIGNATURE);
		decorateBeginMethod(transformation, model, beginRender, annotation);
		model.addRenderPhase(BeginRender.class);

	}

	private void decorateBeginMethod(ClassTransformation transformation,
			MutableComponentModel model, TransformMethod method, GWT annotation) {

		method.addAdvice(new MyComponentMethodAdvice(transformation
				.getClassName()) {

		});
	}

	public void render(String type, MarkupWriter writer,
			ComponentResources componentResources) {
		writer.element("script", "language", "javascript", "type",
				"text/javascript", "src",
				"/gwt/gwtvisualization/gwtvisualization.nocache.js");
		writer.end();

		writer.element("script", "language", "javascript", "type",
				"text/javascript");

		writer.write("\nvar trsvax = {componentIDs: \"\"};\n");

		writer.write(String.format("var %s = {\n", componentResources.getId()));

		writer.write(String.format("completeID: '%s'",
				componentResources.getCompleteId()));
		writer.write(String.format(",\ntype: '%s'", type));

		for (String name : componentResources.getInformalParameterNames()) {
			String value = componentResources.getInformalParameter(name,
					String.class);
			writer.write(String.format(",\n%s: '%s'", name,
					value.replaceAll("'", "\'")));

		}
		writer.write("};");
		writer.end();

		ArrayList<Object> attributes = new ArrayList<Object>();

		Element e = writer.element("div", "id", componentResources.getId());
		if (componentResources.getInformalParameter("height", String.class) != null) {
			e.attribute("height", componentResources.getInformalParameter(
					"height", String.class));
		}

		writer.end();

		javaScriptSupport.addScript("trsvax.componentIDs += \"%s,\"",
				componentResources.getId());

	}

	class MyComponentMethodAdvice implements ComponentMethodAdvice {
		String type;

		public MyComponentMethodAdvice(String name) {
			this.type = name.substring(name.lastIndexOf(".") + 1);
		}

		public void advise(ComponentMethodInvocation invocation) {
			invocation.proceed();
			ComponentResources componentResources = invocation
					.getComponentResources();
			MarkupWriter writer = (MarkupWriter) invocation.getParameter(0);

			render(type, writer, componentResources);

		}

	}
}
