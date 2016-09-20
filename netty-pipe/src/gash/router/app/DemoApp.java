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
package gash.router.app;

import gash.router.client.RoutingClient;
import gash.router.client.RoutingFactory;

import java.util.Properties;

public class DemoApp {
	private static final String sName = "demo";

	public static Properties appConf() {
		// create a connection to a remote service/server. We name the service
		// as we can create/manage multiple connections
		Properties conf = new Properties();
		conf.setProperty(RoutingFactory.sServerName, sName);
		conf.setProperty(RoutingFactory.sServerHost, "localhost");
		conf.setProperty(RoutingFactory.sServerPort, "5570");
		return conf;
	}

	/**
	 * sample application (client) use of our messaging service
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RoutingFactory factory = RoutingFactory.getInstance();
		factory.initConnection(DemoApp.appConf());

		try {
			RoutingClient rc = factory.lookupService(sName);

			// test round-trip overhead (note overhead for initial connection)
			final int maxN = 10;
			long[] dt = new long[maxN];
			long st = System.currentTimeMillis(), ft = 0;
			for (int n = 0; n < maxN; n++) {
				rc.ping();
				ft = System.currentTimeMillis();
				dt[n] = ft - st;
				st = ft;
			}

			System.out.println("Round-trip ping times (msec)");
			for (int n = 0; n < maxN; n++)
				System.out.print(dt[n] + " ");
			System.out.println("");

			// send a message
			st = System.currentTimeMillis();
			ft = 0;
			for (int n = 0; n < maxN; n++) {
				rc.postMessage("hello world " + n);
				ft = System.currentTimeMillis();
				dt[n] = ft - st;
				st = ft;
			}

			System.out.println("Round-trip post times (msec)");
			for (int n = 0; n < maxN; n++)
				System.out.print(dt[n] + " ");
			System.out.println("");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
