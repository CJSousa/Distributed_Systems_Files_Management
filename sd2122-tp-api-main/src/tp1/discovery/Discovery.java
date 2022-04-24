package tp1.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>A class to perform service discovery, based on periodic service contact endpoint 
 * announcements over multicast communication.</p>
 * 
 * <p>Servers announce their *name* and contact *uri* at regular intervals. The server actively
 * collects received announcements.</p>
 * 
 * <p>Service announcements have the following format:</p>
 * 
 * <p>&lt;service-name-string&gt;&lt;delimiter-char&gt;&lt;service-uri-string&gt;</p>
 */
public class Discovery {
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		// addresses some multicast issues on some TCP/IP stacks
		System.setProperty("java.net.preferIPv4Stack", "true");
		// summarizes the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}
	
	
	// The pre-aggreed multicast endpoint assigned to perform discovery. 
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 5000;

	// Used separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	private InetSocketAddress addr;
	private String serviceName;
	private String serviceURI;
	
	// Static variable reference of thisInstance of type Discovery
	private static Discovery thisInstance = null;
	
	// Added global variables
	public ConcurrentHashMap<String, ConcurrentHashMap<URI, Long>> servicesNet;
	long now;
	
	public Discovery (){
		// Ensures that the listener has had enough time to listen to announcements
		this.servicesNet = new ConcurrentHashMap<>();
		this.listener(0);
	}

	/**
	 * @param  serviceName the name of the service to announce
	 * @param  serviceURI an uri string - representing the contact endpoint of the service being announced
	 */
	Discovery( InetSocketAddress addr, String serviceName, String serviceURI) {
		this.addr = addr;
		this.serviceName = serviceName;
		this.serviceURI  = serviceURI;
		this.now = 0;
	}
	
	/**
	 * Static method to create instance of Discovery class
	 * Employs Singleton Pattern
	 * @return current instance of Discovery type
	 */
	public static Discovery getInstance() {
		if( thisInstance == null) thisInstance = new Discovery();
		return thisInstance;
	}

	/**
	 * Continuously announces a service given its name and uri
	 * 
	 * @param serviceName the composite service name: <domain:service>
	 * @param serviceURI - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName, serviceURI));

		var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		// start thread to send periodic announcements
		new Thread(() -> {
			try (var ds = new DatagramSocket()) {
				for (;;) {
					try {
						Thread.sleep(DISCOVERY_PERIOD);
						ds.send(pkt);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Listens for the given composite service name, blocks until a minimum number of replies is collected.
	 * @param serviceName - the composite name of the service
	 * @return the discovery results as an array
	 */
	public void listener(int minRepliesNeeded) {
		Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(), DISCOVERY_ADDR.getPort()));

		final int MAX_DATAGRAM_SIZE = 65536;
		var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);

		new Thread(() -> {
		    try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
			    joinGroupInAllInterfaces(ms);
			    
			    while( true ) {
					try {
						pkt.setLength(MAX_DATAGRAM_SIZE);
						ms.receive(pkt);
					
						var msg = new String(pkt.getData(), 0, pkt.getLength());
						System.out.printf( "FROM %s (%s) : %s\n", pkt.getAddress().getCanonicalHostName(), 
								pkt.getAddress().getHostAddress(), msg);
						var tokens = msg.split(DELIMITER);

						if (tokens.length == 2) {
							
							now = System.currentTimeMillis();
							URI newURI = URI.create(tokens[1]);
							ConcurrentHashMap<URI,Long> URITable = servicesNet.get(tokens[0]);
					
							if(URITable == null) {
								URITable = new ConcurrentHashMap<URI,Long> ();
								servicesNet.put(tokens[0], URITable);
							}
							URITable.put(newURI, now);

						}
							
					} catch (IOException e) {
						e.printStackTrace();
						try {
							this.cleanMap();
							Thread.sleep(DISCOVERY_PERIOD);
						} catch (InterruptedException e1) {
						}
						Log.finest("Still listening...");
					}
				}
  		    } catch (IOException e) {
			    e.printStackTrace();
		    }
		}).start();
	}

	
	public URI[] knownUrisOf(String serviceName) {
		var uris = servicesNet.get(serviceName);
		return uris == null ? null : uris.keySet().toArray(new URI[0]);
	}
	
	private void joinGroupInAllInterfaces(MulticastSocket ms) throws SocketException {
		Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
		while (ifs.hasMoreElements()) {
			NetworkInterface xface = ifs.nextElement();
			try {
				ms.joinGroup(DISCOVERY_ADDR, xface);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}
	
	private void cleanMap() {
		for (ConcurrentMap<URI, Long> URITable : servicesNet.values()) {
			URITable.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue() >= DISCOVERY_TIMEOUT);
        }
	}

	/**
	 * Starts sending service announcements at regular intervals... 
	 */
	public void start() {
		announce(serviceName, serviceURI);
	}

	// Main just for testing purposes
	public static void main( String[] args) throws Exception {
		Discovery discovery = new Discovery( DISCOVERY_ADDR, "test", "http://" + InetAddress.getLocalHost().getHostAddress());
		discovery.start();
	}

}
