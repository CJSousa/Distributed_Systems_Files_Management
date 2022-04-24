package tp1.impl.service.java.directory;

import java.net.URI;

public class FileData {
	
	private int size;
	private URI serverURI;
	
	public FileData(int size, URI serverURI) {
		this.size = size;
		this.serverURI = serverURI;
	}
	
	public void setData(int newSize) {
		size = newSize;
	}

	public URI getServerURI() {
		return serverURI;
	}

	public int getSize() {
		return size;
	}
	

}
