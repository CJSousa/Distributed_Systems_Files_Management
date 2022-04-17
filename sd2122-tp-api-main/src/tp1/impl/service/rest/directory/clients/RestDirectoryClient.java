package tp1.impl.service.rest.directory.clients;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.impl.service.rest.clients.RestClient;

public class RestDirectoryClient extends RestClient implements Directory {

	final WebTarget target;
	
	public RestDirectoryClient(URI serverURI) {
		super(serverURI);
		target = client.target( serverURI ).path( RestDirectory.PATH );
	}

	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
		return super.reTry(() -> clt_writeFile(filename, data, userId, password));
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Result<FileInfo> clt_writeFile(String filename, byte[] data, String userId, String password){
		
		Response r = target.path(userId).path(filename)
				.queryParam(RestUsers.PASSWORD, password)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
			//return r.readEntity(new GenericType<Result<FileInfo>>() {});
			return Result.ok(r.readEntity(FileInfo.class));
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
	}

}
