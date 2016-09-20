package gash.router.container;

import gash.router.container.JsonBuilder;
import gash.router.container.MessageContainer;
import junit.framework.Assert;

import org.junit.Test;

public class MessageContainerTest {
	private static final String sJSON = "{\"path\":\"/message\",\"statusCode\":-1,\"statusMsg\":null,\"body\":\"{\\\"name\\\" : \\\"jane doe\\\"}\",\"msgid\":1}";

	@Test
	public void testEncoding() throws Exception {
		MessageContainer mc = new MessageContainer();
		mc.setMsgid(1);
		mc.setPath("/message");
		mc.setBody("{\"name\" : \"jane doe\"}");

		String json = JsonBuilder.encode(mc);
		System.out.println("JSON: " + json);
		Assert.assertEquals(sJSON, json);

		MessageContainer mc2 = JsonBuilder.decode(json, MessageContainer.class);
		Assert.assertNotNull(mc2);
	}
}
