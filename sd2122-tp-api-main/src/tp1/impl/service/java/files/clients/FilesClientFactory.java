package tp1.impl.service.java.files.clients;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import tp1.api.service.util.Files;
import tp1.discovery.Discovery;
import tp1.impl.service.rest.files.clients.RestFilesClient;
import tp1.impl.service.soap.files.clients.SoapFilesClient;

public class FilesClientFactory {

	private static final String SERVICE_NAME = "files";
	private static ConcurrentMap<URI, Files> clientInstance = new ConcurrentHashMap<>();

	public static URI[] getAvailableServers() {
		URI[] availableServers;
		Discovery discovery = Discovery.getInstance();
		while ((availableServers = discovery.knownUrisOf(SERVICE_NAME)) == null) {
		}

		return availableServers;
	}

	public static Files getClient(URI uri) {
		if (uri.toString().contains("rest"))
			return new RestFilesClient(uri);
		else {
			var client = clientInstance.computeIfAbsent(uri, k -> new SoapFilesClient(uri));
			return client;
		}
	}

}