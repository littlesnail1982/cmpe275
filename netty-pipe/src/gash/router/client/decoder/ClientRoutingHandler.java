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
package gash.router.client.decoder;

import gash.router.container.JsonBuilder;
import gash.router.container.MessageContainer;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientRoutingHandler extends SimpleChannelUpstreamHandler {
	private RouteListener listener;

	public RouteListener getListener() {
		return listener;
	}

	public void setListener(RouteListener listener) {
		this.listener = listener;
	}

	/**
	 * override this method to provide processing behavior
	 * 
	 * @param msg
	 */
	public void handleMessage(String msg) {
		System.out.println("---> ClientRoutingHandler: " + msg);
		System.out.flush();

		// normally we would place or processing code here. 
		MessageContainer resp = JsonBuilder.decode(msg, MessageContainer.class);
		if (listener != null)
			listener.process(resp);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		handleMessage((String) e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		System.out.println("ERROR: " + e.getCause());

		// TODO do we really want to do this?
		e.getChannel().close();
	}
}