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

import gash.router.container.JsonBuilder;
import gash.router.container.MessageContainer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class RoutingFactory {
	public static final String sServerName = "server.name";
	public static final String sServerHost = "server.host";
	public static final String sServerPort = "server.port";

	protected Properties conf;
	protected HashMap<String, RoutingClient> connection = new HashMap<String, RoutingClient>();
	protected ClientBootstrap bootstrap;

	protected static RoutingFactory instance;

	public static RoutingFactory getInstance() {
		if (instance == null)
			instance = new RoutingFactory();

		return instance;
	}

	protected RoutingFactory() {
	}

	public void finalize() {
		for (RoutingClient h : connection.values())
			h.release();

		bootstrap.releaseExternalResources();
	}

	public void initConnection(Properties conf) {
		if (conf == null || conf.size() < 3)
			throw new RuntimeException("Missing configuration and/or properties");

		String name = conf.getProperty(sServerName);
		if (connection.containsKey(name))
			throw new RuntimeException("Name already in use");

		// Configure the client.
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		int port = Integer.parseInt(conf.getProperty(sServerPort));
		final ClientHandler con = new ClientHandler(conf.getProperty(sServerHost), port);

		RoutingClient rc = new RoutingClient(con);
		connection.put(name, rc);

		// Set up the pipeline factory.
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(con);
			}
		});

		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);

		// bootstrap.setPipelineFactory(new ClientDecoderPipeline());

		// bootstrap.connect(new InetSocketAddress("localhost", port));
	}

	public RoutingClient lookupService(String name) throws Exception {
		RoutingClient con = connection.get(name);
		if (con != null)
			return con;

		return con;
	}

	/**
	 * The client handler provides a built in stream handler. This allows the
	 * processing and message retrieval to the within the same class. However,
	 * this does not allow us to take advantage of the pipeline features of
	 * Netty.
	 * 
	 * @author gash1
	 * 
	 */
	protected final class ClientHandler extends SimpleChannelUpstreamHandler {
		protected String host;
		protected int port;
		protected ChannelFuture channel;

		ClientHandler(String host, int port) {
			this.host = host;
			this.port = port;
		}

		public void release() {
			channel.awaitUninterruptibly();
			channel.getChannel().close();
		}

		private Channel connect() {
			// Start the connection attempt.
			if (channel == null) {
				// System.out.println("---> connecting");
				channel = RoutingFactory.this.bootstrap.connect(new InetSocketAddress(host, port));
			}

			// wait for the connection to establish
			channel.awaitUninterruptibly();

			return channel.getChannel();
		}

		public void send(MessageContainer mc) {
			// note the handler on the server is expecting 'newline' delimited
			// messages.
			String json = JsonBuilder.encode(mc) + "\n";
			send(json.getBytes());
		}

		public void send(byte[] data) {
			ChannelBuffer b = ChannelBuffers.buffer(data.length);
			b.writeBytes(data);
			connect().write(b);
		}

		/**
		 * on connection to the server
		 */
		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
			// System.out.println("---> client is connected");
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			// Send back the received message to the remote peer.

			ByteBuffer buffer = ByteBuffer.allocate(((ChannelBuffer) e.getMessage()).readableBytes());
			((ChannelBuffer) e.getMessage()).readBytes(buffer);

			buffer.rewind();
			String json = new String(buffer.array());
			MessageContainer mc = JsonBuilder.decode(json, MessageContainer.class);
			if (mc != null) {
				System.out.println("Results for " + mc.getPath() + " (" + mc.getMsgid() + "): " + mc.getBody());
			} else {
				// unexpected results
			}
		}

		/**
		 * Close the connection when an exception is raised.
		 */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			System.out.println("ERROR: " + e.getCause());
			// e.getChannel().close();
		}

	}
}
