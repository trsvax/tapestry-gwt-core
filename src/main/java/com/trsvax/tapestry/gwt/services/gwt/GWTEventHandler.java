package com.trsvax.tapestry.gwt.services.gwt;

import java.io.IOException;

import org.apache.tapestry5.services.Response;

import com.google.gwt.user.server.rpc.RPCRequest;

public interface GWTEventHandler {
	public boolean handleRequest(String componentID, Response response,
			RPCRequest rpcRequest) throws IOException;

}
