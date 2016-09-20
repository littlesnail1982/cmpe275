/**
 * Copyright 2012 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gash.router.client;

import gash.router.client.RoutingFactory.ClientHandler;
import gash.router.container.MessageContainer;

import java.util.Properties;

/**
 * proxy to our service
 * 
 * @author gash
 * 
 */
public class RoutingClient {
	ClientHandler svr;

	// private RoutingFactory svr;
	private String connectionName = "demo";

	// track requests
	private long curID = 1;

	protected RoutingClient(ClientHandler svr) {
		this.svr = svr;
	}

	public void ping() {
		// construct the message to send
		MessageContainer mc = new MessageContainer();
		mc.setPath("/ping");
		mc.setMsgid(nextId());

		try {
			svr.send(mc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postMessage(String msg) {
		// construct the message to send
		MessageContainer mc = new MessageContainer();
		mc.setPath("/message");
		mc.setMsgid(nextId());
		mc.setBody(msg);

		try {
			svr.send(mc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void release() {
		if (svr != null)
			svr.release();

		svr = null;
	}

	/**
	 * Since the service/server is asychronous we need a unique ID to associate
	 * our requests with the server's reply
	 * 
	 * @return
	 */
	private synchronized long nextId() {
		return ++curID;
	}
}
