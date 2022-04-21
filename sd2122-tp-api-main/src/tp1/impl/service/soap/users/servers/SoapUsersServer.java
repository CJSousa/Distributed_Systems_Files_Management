package tp1.impl.service.soap.users.servers;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.ws.Endpoint;
import tp1.discovery.Discovery;
import tp1.impl.service.soap.users.UsersWebService;

public class SoapUsersServer {

	public static final int PORT = 8080;
	public static final String SERVICE = "users";
	public static String SERVER_BASE_URI = "http://%s:%s/soap";

	private static Logger Log = Logger.getLogger(SoapUsersServer.class.getName());

	public static void main(String[] args) throws Exception {

		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

		Log.setLevel(Level.INFO);

		String ip = InetAddress.getLocalHost().getHostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		
		Discovery discovery = Discovery.getInstance();
		discovery.announce(SERVICE, serverURI);

		Endpoint.publish(serverURI.replace(ip, "0.0.0.0"), new UsersWebService());

		Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE, serverURI));

	}

}
