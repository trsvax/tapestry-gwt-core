package com.trsvax.tapestry.gwt.services.gwt.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.trsvax.tapestry.gwt.services.gwt.GWTEventHandler;

public class GWTEventHandlerImpl implements GWTEventHandler {

	private final ComponentSource componentSource;

	public GWTEventHandlerImpl(Logger logger, ComponentSource componentSource) {
		this.componentSource = componentSource;
	}

	public boolean handleRequest(String completeID, Response response,
			RPCRequest rpcRequest) throws IOException {
		assert (response != null);
		if (rpcRequest == null)
			return false;
		String payload = handleRequest(completeID, rpcRequest);
		return sendResponse(payload, response);
	}

	boolean sendResponse(String payload, Response response) throws IOException {
		if (payload == null)
			return false;
		PrintWriter pw = response
				.getPrintWriter("application/json; charset=utf-8");
		pw.print(payload);
		pw.flush();
		return true;
	}

	String handleRequest(String completeID, RPCRequest rpcRequest)
			throws IOException {
		Method interfaceMethod = rpcRequest.getMethod();
		Object[] parameters = rpcRequest.getParameters();

		while (completeID != null) {
			Object component = componentSource.getComponent(completeID);
			Method method = findHandlerMethod(component, rpcRequest);
			try {
				Object object = tryEvent(component, method, parameters);
				if (isHandled(object))
					return encodeRPCSuccess(interfaceMethod, object);
			} catch (Exception cause) {
				return encodeRPCFailure(interfaceMethod, cause);
			}
			completeID = nextComponent(completeID);
		}
		return null;
	}

	String nextComponent(String completeID) {
		int index = completeID.lastIndexOf(":");
		if (index < 0)
			return null;
		return completeID.substring(0, index);
	}

	Boolean isHandled(Object object) {
		if (object != null && Boolean.class.isAssignableFrom(object.getClass())) {
			return (Boolean) object;
		}
		if (object != null) {
			return true;
		}
		return false;
	}

	String encodeRPCSuccess(Method method, Object object) throws IOException {
		try {
			return RPC.encodeResponseForSuccess(method, object);
		} catch (SerializationException e) {
			throw new IOException(e.getMessage());
		}
	}

	String encodeRPCFailure(Method method, Exception cause) throws IOException {
		try {
			return RPC.encodeResponseForFailure(method, cause);
		} catch (SerializationException e) {
			throw new IOException(e.getMessage());
		}
	}

	Method findHandlerMethod(Object component, RPCRequest rpcRequest)
			throws IOException {
		try {
			Method method = rpcRequest.getMethod();
			Class<?>[] parameterTypes = method.getParameterTypes();

			return component.getClass().getMethod(method.getName(),
					parameterTypes);
		} catch (SecurityException e) {
			throw new IOException(e.getMessage());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	Object tryEvent(Object component, Method method, Object[] parameters)
			throws Exception {
		if (method == null)
			return null;
		if (component == null)
			return null;
		return method.invoke(component, parameters);
	}

}
