package tp1.impl.service.rest.files;

import java.net.URI;

import bruh.RestClient;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.service.rest.RestFiles;


public class RestFilesClient extends RestClient implements Files {

	final WebTarget target;

	RestFilesClient(URI serverURI) {
		super(serverURI);
		target = client.target(serverURI).path(RestFiles.PATH);
	}

	@Override
	public void writeFile(String fileId, byte[] data, String token) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteFile(String fileId, String token) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getFile(String fileId, String token) {
		// TODO Auto-generated method stub
		return null;
	}

}
