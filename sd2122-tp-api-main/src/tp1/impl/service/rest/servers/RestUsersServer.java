package tp1.impl.service.rest.servers;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import discovery.Discovery;
import tp1.impl.service.rest.users.UsersResource;

public class RestUsersServer {
	
	private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static final int PORT = 8080;
	public static final String SERVICE = "UsersService";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";
	
	public static void main(String[] args) {
		try {
			
		//Debug.setLogLevel( Level.INFO, Debug.SD2122 );
			
		ResourceConfig config = new ResourceConfig();
		config.register(UsersResource.class);
		//config.register(CustomLoggingFilter.class);
		//For further knowledge on errors:
		config.register(GenericExceptionMapper.class);
		
		String ip = InetAddress.getLocalHost().getHostAddress();
		String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
		JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);
		
		Discovery discovery = Discovery.getInstance();
		discovery.announce(SERVICE, serverURI);
	
		Log.info(String.format("%s Server ready @ %s\n",  SERVICE, serverURI));
		
		//More code can be executed here...
		} catch( Exception e) {
			Log.severe(e.getMessage());
		}
	}	

}
