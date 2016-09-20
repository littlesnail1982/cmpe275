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
package gash.router.server.decoder;

import gash.router.container.JsonBuilder;
import gash.router.container.MessageContainer;
import gash.router.container.RoutingConf;
import gash.router.server.resources.RouteResource;

import java.beans.Beans;
import java.util.HashMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * The message handler processes json messages that are delimited by a 'newline'
 * 
 * TODO replace println with logging!
 * 
 * @author gash
 * 
 */
public class RoutingHandler extends SimpleChannelUpstreamHandler {
	private HashMap<String, String> routing;

	public RoutingHandler(RoutingConf conf) {
		if (conf != null)
			routing = conf.asHashMap();
	}

	/**
	 * override this method to provide processing behavior
	 * 
	 * @param msg
	 */
	public void handleMessage(String msg, Channel channel) {
		// System.out.println("---> " + msg);
		System.out.flush();

		MessageContainer req = JsonBuilder.decode(msg, MessageContainer.class);
		if (req == null) {
			// TODO add logging
			System.out.println("ERROR: Unexpected content - " + msg);
			return;
		}

		try {
			// System.out.println("---> path: " + req.getPath());
			String clazz = routing.get(req.getPath().toLowerCase());
			if (clazz != null) {
				RouteResource rsc = (RouteResource) Beans.instantiate(RouteResource.class.getClassLoader(), clazz);
				try {
					String reply = rsc.process(req.getBody());
					// System.out.println("---> reply: " + reply);
					if (reply != null) {
						MessageContainer resp = new MessageContainer();
						resp.setMsgid(req.getMsgid());
						resp.setPath(req.getPath());
						resp.setBody(reply);
						resp.setStatusCode(1);

						String rtn = JsonBuilder.encode(resp);
						// System.out.println("---> response: " + rtn);
						channel.write(rtn);
					}
				} catch (Exception e) {
					// TODO add logging
					MessageContainer resp = new MessageContainer();
					resp.setMsgid(req.getMsgid());
					resp.setPath(req.getPath());
					resp.setStatusCode(0);
					resp.setStatusMsg(e.getMessage());
					String rtn = JsonBuilder.encode(resp);
					channel.write(rtn);
				}
			} else {
				// TODO add logging
				System.out.println("ERROR: unknown path - " + req.getPath());
			}
		} catch (Exception ex) {
			// TODO add logging
			System.out.println("ERROR: processing request - " + ex.getMessage());
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		handleMessage((String) e.getMessage(), e.getChannel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		System.out.println("ERROR (RoutingHandler): " + e.getCause());
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}