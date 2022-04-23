package tp1.impl.service.java.files.clients;

import java.net.URI;
import tp1.api.service.util.Files;
import tp1.discovery.Discovery;
import tp1.impl.service.rest.files.clients.RestFilesClient;
import tp1.impl.service.soap.files.clients.SoapFilesClient;

public class FilesClientFactory {

    private static final int FIRST_SERVER_AVAILABLE = 0;
    private static final String SERVICE_NAME = "files";
    private static URI serverURI;

    /*
    public static Files getClient() {

        // With multicast, find available URI
        Discovery discovery = Discovery.getInstance();

        URI[] availableServers;
        while ((availableServers = discovery.knownUrisOf(SERVICE_NAME)) == null) {
        }
        serverURI = availableServers[FIRST_SERVER_AVAILABLE];
        //Math.random entre 0 - 1

        if (serverURI.toString().endsWith("rest"))
            return new RestFilesClient(serverURI);
        else
            return new SoapFilesClient(serverURI);
    }
*/
    public static URI getAvailableURI() {
        return serverURI;
    }

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
        else
            return new SoapFilesClient(uri);
    }

}