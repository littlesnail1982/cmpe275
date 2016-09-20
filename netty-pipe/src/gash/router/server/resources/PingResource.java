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
package gash.router.server.resources;

/**
 * responds to request for pinging the service
 * 
 * @author gash
 * 
 */
public class PingResource implements RouteResource {

	@Override
	public String getPath() {
		return "/ping";
	}

	@Override
	public String process(String body) {
		// TODO process message
		System.out.println("---> " + getPath());
		return "success";
	}

}
