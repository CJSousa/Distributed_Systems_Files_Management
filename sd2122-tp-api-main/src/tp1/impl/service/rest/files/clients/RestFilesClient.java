package tp1.impl.service.rest.files.clients;

import java.net.URI;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;
import tp1.impl.service.rest.clients.RestClient;

public class RestFilesClient extends RestClient implements Files {

	final WebTarget target;

	// PAY ATTENTION BECAUSE 200 and 204 should both present as 200s

	public RestFilesClient(URI serverURI) {
		super(serverURI);
		target = client.target(serverURI).path(RestFiles.PATH);
	}

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
		return super.reTry(() -> {
			return clt_writeFile(fileId, data, token);
		});
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		return super.reTry(() -> {
			return clt_deleteFile(fileId, token);
		});
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		return super.reTry(() -> {
			return clt_getFile(fileId, token);
		});
	}

	//

	/**
	 * Write a file. If the file exists, overwrites the contents. It consumes
	 * APPLICATION_OCTET_STREAM, which is used for entities whose sole intended
	 * purpose is to be saved to disk
	 * 
	 * @param fileId - unique id of the file.
	 * @param data   - contents of the file.
	 * @param token  - token for accessing the file server (in the first project
	 *               this will not be used).
	 * @return fileId
	 */
	private Result<Void> clt_writeFile(String fileId, byte[] data, String token) {

		Response r = target.path(fileId).queryParam(RestFiles.TOKEN, token).request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
			System.out.println("Success, wrote file with id: ");
			return r.readEntity(new GenericType<Result<Void>>() {
			});
		} else
			//Status.fromStatusCode(CONNECT_TIMEOUT)
			System.out.println("Error, HTTP error status: " + r.getStatus());

		return null;

	}

	/**
	 * 
	 * @param fileId
	 * @param token
	 * @return
	 */
	private Result<Void> clt_deleteFile(String fileId, String token) {

		Response r = target.path(fileId).queryParam(RestFiles.TOKEN, token).request().accept(MediaType.APPLICATION_JSON)
				.delete();

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
			System.out.println("Successful removal of: ");
			return r.readEntity(new GenericType<Result<Void>>() {
			});
		} else
			System.out.println("Error, HTTP error status: " + r.getStatus());
		return null;

	}

	/**
	 * 
	 * @param fileId
	 * @param token
	 * @return
	 */
	private Result<byte[]> clt_getFile(String fileId, String token) {

		Response r = target.path(fileId).queryParam(RestFiles.TOKEN, token).request().accept(MediaType.APPLICATION_JSON)
				.get();

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity())
			return r.readEntity(new GenericType<Result<byte[]>>() {
			});
		else
			System.out.println("Error, HTTP error status: " + r.getStatus());
		return null;

	}

}