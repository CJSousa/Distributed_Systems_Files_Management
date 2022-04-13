package tp1.impl.service.java;

import java.net.URI;

import discovery.Discovery;
import tp1.api.service.util.Users;
import tp1.impl.service.rest.users.RestUsersClient;

public class UsersClientFactory {

	private static final int MIN_REPLIES = 1;
	private static final int FIRST_SERVER_AVAILABLE = 0;
	private static final String SERVICE_NAME = "users";

	/**
	 * 
	 * 
	 * @return
	 */
	public static Users getClient() {

		// With multicast, find available URI
		Discovery discovery = Discovery.getInstance();
		discovery.listener(MIN_REPLIES);

		URI[] availableServers;
		while ((availableServers = discovery.knownUrisOf(SERVICE_NAME)) == null) {}
		String serverURI = availableServers[FIRST_SERVER_AVAILABLE].toString();

		// 
		if( serverURI.endsWith("rest") )
			return new RestUsersClient(URI.create(serverURI)); 
		//else 
			//return new SoapUsersClient(serverURI ); 
	
		//Remove after
		return null;

	}
}
