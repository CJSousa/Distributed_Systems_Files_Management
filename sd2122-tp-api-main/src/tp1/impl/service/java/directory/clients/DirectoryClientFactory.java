package tp1.impl.service.java.directory.clients;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import tp1.api.service.util.Directory;
import tp1.discovery.Discovery;
import tp1.impl.service.rest.directory.clients.RestDirectoryClient;
import tp1.impl.service.soap.directory.clients.SoapDirectoryClient;

public class DirectoryClientFactory {

	private static final int FIRST_SERVER_AVAILABLE = 0;
	private static final String SERVICE_NAME = "directory";
	private static ConcurrentMap<URI, Directory> clientInstance = new ConcurrentHashMap<>();

	
	public static Directory getClient() {

		// With multicast, find available URI
		Discovery discovery = Discovery.getInstance();
		URI[] availableServers;

		while ((availableServers = discovery.knownUrisOf(SERVICE_NAME)) == null) {
		}
		URI serverURI = availableServers[FIRST_SERVER_AVAILABLE];

		if (serverURI.toString().endsWith("rest"))
			return new RestDirectoryClient(serverURI);
		else {
			var client = clientInstance.computeIfAbsent(serverURI, k -> new SoapDirectoryClient(serverURI));
			return client;
		}

	}

}
