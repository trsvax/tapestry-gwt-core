package com.trsvax.tapestry.gwt.services.gwt;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RPCServletUtils;

public class GWTEventDispatcher implements Dispatcher {

	private final Logger logger;
	private final GWTEventHandler gwtEventHandler;
	private final RequestGlobals requestGlobals;

	public GWTEventDispatcher(Logger logger, RequestGlobals requestGlobals,
			GWTEventHandler gwtEventHandler) {
		this.logger = logger;
		this.gwtEventHandler = gwtEventHandler;
		this.requestGlobals = requestGlobals;
	}

	public boolean dispatch(Request request, Response response)
			throws IOException {
		String contentType = request.getHeader("Content-Type");
		if (contentType == null)
			return false;
		if (contentType.toLowerCase().indexOf("text/x-gwt-rpc") < 0) {
			return false;
		}

		return gwtEventHandler.handleRequest(componentID(request), response,
				decodeRPCRequest(request));
	}

	RPCRequest decodeRPCRequest(Request request) throws IOException {
		try {
			String payload = RPCServletUtils.readContentAsGwtRpc(requestGlobals
					.getHTTPServletRequest());
			return RPC.decodeRequest(payload);
		} catch (ServletException e) {
			throw new IOException(e.getMessage());
		}
	}

	String componentID(Request request) {
		String path = request.getPath();
		path = path.replaceFirst("/", "");
		String id = path.replace(".", ":");
		return id;
	}

}
