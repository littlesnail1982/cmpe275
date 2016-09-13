package gash.socket.common;

import gash.socket.common.MessageBuilder.MessageType;
import gash.socket.data.Message;

import java.text.DecimalFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class MessageBuilderTest {

	@Test
	public void testFormat() throws Exception {
		DecimalFormat fmt = new DecimalFormat("0000");
		System.out.println("s: " + fmt.format(10.0));
	}

	@Test
	public void testEncodingOne() throws Exception {
		MessageBuilder builder = new MessageBuilder();

		String s = builder.encode(MessageType.join, "testSrc", "hello world", null);
		System.out.println("msg: " + s);

		List<Message> list = builder.decode(s.getBytes());
		Assert.assertNotNull(list);

		for (Message msg : list) {
			System.out.println("msg type:" + msg.getType());
			System.out.println("source: " + msg.getSource());
			System.out.println("date: " + msg.getReceived());
			System.out.println("payload: " + msg.getPayload() + "\n");
		}
		System.out.println("\nis complete: " + builder.isComplete());
	}

	@Test
	public void testEncodingTwo() throws Exception {
		MessageBuilder builder = new MessageBuilder();

		String s = builder.encode(MessageType.join, "testSrc", null, null)
				+ builder.encode(MessageType.msg, "testSrc", "goodbye world", null);
		System.out.println("msg: " + s);

		List<Message> list = builder.decode(s.getBytes());
		Assert.assertNotNull(list);

		for (Message msg : list) {
			System.out.println("msg type:" + msg.getType());
			System.out.println("source: " + msg.getSource());
			System.out.println("date: " + msg.getReceived());
			System.out.println("payload: " + msg.getPayload() + "\n");
		}
		System.out.println("\nis complete: " + builder.isComplete());
	}

	@Test
	public void testEncodingPartial() throws Exception {
		MessageBuilder builder = new MessageBuilder();

		String s = builder.encode(MessageType.join, "testSrc", null, null)
				+ builder.encode(MessageType.msg, "testSrc", "goodbye world", null).substring(0, 20);
		System.out.println("msg: " + s);

		List<Message> list = builder.decode(s.getBytes());
		Assert.assertNotNull(list);

		for (Message msg : list) {
			System.out.println("msg type:" + msg.getType());
			System.out.println("source: " + msg.getSource());
			System.out.println("date: " + msg.getReceived());
			System.out.println("payload: " + msg.getPayload() + "\n");
		}
		System.out.println("\nis complete: " + builder.isComplete());
	}

	/**
	 * a partial message is received, ensure we buffer the remained for the next
	 * decoding
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEncodingPartialComplete() throws Exception {
		MessageBuilder builder = new MessageBuilder();

		String s2 = builder.encode(MessageType.msg, "testSrc", "goodbye world", null);

		String s = builder.encode(MessageType.join, "testSrc", null, null) + s2.substring(0, 20);
		System.out.println("raw: " + s);

		List<Message> list = builder.decode(s.getBytes());
		Assert.assertNotNull(list);

		for (Message msg : list) {
			System.out.println("msg type:" + msg.getType());
			System.out.println("source: " + msg.getSource());
			System.out.println("date: " + msg.getReceived());
			System.out.println("payload: " + msg.getPayload() + "\n");
		}
		System.out.println("\nis complete: " + builder.isComplete());

		// now process the rest of message 2
		builder.decode(s2.substring(20).getBytes());
		list = builder.decode(s2.getBytes());
		Assert.assertNotNull(list);

		for (Message msg : list) {
			System.out.println("msg type:" + msg.getType());
			System.out.println("source: " + msg.getSource());
			System.out.println("date: " + msg.getReceived());
			System.out.println("payload: " + msg.getPayload() + "\n");
		}
		System.out.println("\nis complete: " + builder.isComplete());

	}
}
