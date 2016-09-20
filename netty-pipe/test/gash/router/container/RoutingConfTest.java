package gash.router.container;

import junit.framework.Assert;
import gash.router.container.JsonBuilder;
import gash.router.container.RoutingConf;
import gash.router.container.RoutingConf.RoutingEntry;

import org.junit.Test;

public class RoutingConfTest {
	@Test
	public void testRoutingConfiguration() throws Exception {
		RoutingConf rc = new RoutingConf();

		rc.addEntry(new RoutingEntry("/ping",
				"gash.router.server.resources.PingResource"));
		rc.addEntry(new RoutingEntry("/message",
				"gash.router.server.resources.MessageResource"));

		String json = JsonBuilder.encode(rc);
		System.out.println("JSON: " + json);

		RoutingConf rc2 = JsonBuilder.decode(json, RoutingConf.class);
		Assert.assertEquals(rc.getPort(), rc2.getPort());
		Assert.assertEquals(rc.getRouting().size(), rc2.getRouting().size());
		for (int n = 0, N = rc.getRouting().size(); n < N; n++) {
			RoutingEntry e = rc.getRouting().get(n);
			RoutingEntry e2 = rc2.getRouting().get(n);

			Assert.assertEquals(e.getPath(), e2.getPath());
		}
	}
}
