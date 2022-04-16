package tp1.impl.service.java.files.clients;

import java.net.URI;
import tp1.api.service.util.Files;
import tp1.discovery.Discovery;
import tp1.impl.service.rest.files.clients.RestFilesClient;

public class FilesClientFactory {

	private static final int MIN_REPLIES = 1;
	private static final int FIRST_SERVER_AVAILABLE = 0;
	private static final String SERVICE_NAME = "files";
	
	//
	private static String serverURI;

	public static Files getClient() {
		
		// With multicast, find available URI
		Discovery discovery = Discovery.getInstance();
		discovery.listener(MIN_REPLIES);

		URI[] availableServers;
		while ((availableServers = discovery.knownUrisOf(SERVICE_NAME)) == null) {}
		serverURI = availableServers[FIRST_SERVER_AVAILABLE].toString();

		//
		if (serverURI.endsWith("rest"))
			return new RestFilesClient(URI.create(serverURI));
			
		// else
		// return new SoapUsersClient(serverURI );

		// Remove after
		return null;

	}
	
	public static String getAvailableURI() { return serverURI; }

}
