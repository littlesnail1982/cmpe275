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

import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ByteHandler extends SimpleChannelUpstreamHandler {
	/**
	 * override this method to provide processing behavior
	 * 
	 * @param msg
	 */
	public void handleMessage(byte[] msg) {
		System.out.println("---> " + new String(msg));
		System.out.flush();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		ByteBuffer buffer = ByteBuffer
				.allocate(((ChannelBuffer) e.getMessage()).readableBytes());
		((ChannelBuffer) e.getMessage()).readBytes(buffer);

		buffer.rewind();
		handleMessage(buffer.array());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		// Close the connection when an exception is raised.
		System.out.println("ERROR: " + e.getCause());
		e.getChannel().close();
	}
}