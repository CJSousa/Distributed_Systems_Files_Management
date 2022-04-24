package tp1.impl.service.java.directory;

import java.net.URI;

import tp1.api.FileInfo;

public class FileData {
	
	private FileInfo file;
	private int size;
	private URI serverURI;
	
	public FileData(FileInfo file, int size, URI serverURI) {
		this.file = file;
		this.size = size;
		this.serverURI = serverURI;
	}

	/*
	public FileInfo getFileInfo() {
		return file;
	}
	*/

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
