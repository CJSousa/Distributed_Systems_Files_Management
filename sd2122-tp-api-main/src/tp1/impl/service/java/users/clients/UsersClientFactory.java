package tp1.impl.service.java.users.clients;

import java.net.MalformedURLException;
import java.net.URI;
import tp1.api.service.util.Users;
import tp1.discovery.Discovery;
import tp1.impl.service.rest.users.clients.RestUsersClient;
import tp1.impl.service.soap.users.clients.SoapUsersClient;

public class UsersClientFactory {

	private static final int FIRST_SERVER_AVAILABLE = 0;
	private static final String SERVICE_NAME = "users";

	/**
	 * 
	 * 
	 * @return
	 * @throws MalformedURLException 
	 */
	public static Users getClient() {

		// With multicast, find available URI
		Discovery discovery = Discovery.getInstance();

		URI[] availableServers;
		while ((availableServers = discovery.knownUrisOf(SERVICE_NAME)) == null) {
		}
		URI serverURI = availableServers[FIRST_SERVER_AVAILABLE];
		
		if (serverURI.toString().endsWith("rest"))
			return new RestUsersClient(serverURI);
		else 
			return new SoapUsersClient(serverURI);
			
	}
}
