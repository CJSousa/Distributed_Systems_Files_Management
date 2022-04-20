package tp1.impl.service.java.files.clients;

import java.net.URI;
import tp1.api.service.util.Files;
import tp1.discovery.Discovery;
import tp1.impl.service.rest.files.clients.RestFilesClient;
import tp1.impl.service.soap.files.clients.SoapFilesClient;

public class FilesClientFactory {

	private static final int FIRST_SERVER_AVAILABLE = 0;
	private static final String SERVICE_NAME = "files";
	private static String serverURI;

	public static Files getClient() {

		// With multicast, find available URI
		Discovery discovery = Discovery.getInstance();

		URI[] availableServers;
		while ((availableServers = discovery.knownUrisOf(SERVICE_NAME)) == null) {
		}
		serverURI = availableServers[FIRST_SERVER_AVAILABLE].toString();

		if (serverURI.endsWith("rest"))
			return new RestFilesClient(URI.create(serverURI));
		else
			return new SoapFilesClient(URI.create(serverURI));

	}

	public static String getAvailableURI() {
		return serverURI;
	}

}
