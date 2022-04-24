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
		target = client.target(serverURI).path(RestDirectory.PATH);
	}

	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
		return super.reTry(() -> clt_writeFile(filename, data, userId, password));
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {
		return super.reTry(() -> clt_deleteFile(filename, userId, password));
	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
		return super.reTry(() -> clt_shareFile(filename, userId, userIdShare, password));
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
		return super.reTry(() -> clt_unshareFile(filename, userId, userIdShare, password));
	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
		return super.reTry(() -> clt_getFile(filename, userId, accUserId, password));
	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {
		return super.reTry(() -> clt_lsFile(userId, password));
	}
	
	//override?
	public Result<Void> deleteFilesOfUser(String userId, String password) {
		return super.reTry(() -> clt_deleteFilesOfUser(userId, password));
	}
	

	@Override
	public Result<FileInfo> findFile(String filename, String userId, String accUserId, String password) {
		return super.reTry(() -> clt_findFile(filename, userId, accUserId, password));
	}
	
	private Result<FileInfo> clt_writeFile(String filename, byte[] data, String userId, String password) {

		Response r = target.path(userId).path(filename).queryParam(RestUsers.PASSWORD, password).request()
				.accept(MediaType.APPLICATION_JSON).post(Entity.entity(data, MediaType.APPLICATION_OCTET_STREAM));

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
			// return r.readEntity(new GenericType<Result<FileInfo>>() {});
			return Result.ok(r.readEntity(FileInfo.class));
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
	}

	private Result<byte[]> clt_getFile(String filename, String userId, String accUserId, String password) {

		Response r = target.path(userId).path(filename).queryParam(RestUsers.USER_ID, accUserId)
				.queryParam(RestUsers.PASSWORD, password).request().accept(MediaType.APPLICATION_OCTET_STREAM).get();

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
			// return r.readEntity(new GenericType<Result<FileInfo>>() {});
			return Result.ok(r.readEntity(byte[].class));
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
	}

	private Result<Void> clt_deleteFile(String filename, String userId, String password) {

		Response r = target.path(userId).path(filename).queryParam(RestUsers.PASSWORD, password).request().delete();

		if (r.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			// return r.readEntity(new GenericType<Result<FileInfo>>() {});
			return Result.ok();
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
	}
	
	private Result<Void> clt_shareFile(String filename, String userId, String userIdShare, String password) {
		
		Response r = target.path(userId).path(filename).path("share").path(userIdShare).queryParam(RestUsers.PASSWORD, password).request().post(null);
		
		if (r.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			// return r.readEntity(new GenericType<Result<FileInfo>>() {});
			return Result.ok();
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
		
	}
	
	
	private Result<Void> clt_unshareFile(String filename, String userId, String userIdShare, String password) {
		
		Response r = target.path(userId).path(filename).path("share").path(userIdShare).queryParam(RestUsers.PASSWORD, password).request().delete();
		
		if (r.getStatus() == Status.NO_CONTENT.getStatusCode()) {
			// return r.readEntity(new GenericType<Result<FileInfo>>() {});
			return Result.ok();
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
	
	}
	
	private Result<List<FileInfo>> clt_lsFile(String userId, String password){
		
		Response r = target.path(userId).queryParam(RestUsers.PASSWORD, password).request().accept(MediaType.APPLICATION_JSON).get();
		
		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
			return r.readEntity(new GenericType<Result<List<FileInfo>>>() {});
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
		
	}
	
	private Result<Void> clt_deleteFilesOfUser(String userId, String password){
		
		Response r = target.path(userId).queryParam(RestUsers.PASSWORD, password).request().delete();

		if (r.getStatus() == Status.NO_CONTENT.getStatusCode()) 
			return Result.ok();
		 else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
	}

	private Result<FileInfo> clt_findFile(String filename, String userId, String accUserId, String password) {
		
		Response r = target.path(userId).path(filename).queryParam(RestUsers.USER_ID, accUserId)
				.queryParam(RestUsers.PASSWORD, password).request().accept(MediaType.APPLICATION_JSON).get();

		if (r.getStatus() == Status.OK.getStatusCode() && r.hasEntity()) {
			// return r.readEntity(new GenericType<Result<FileInfo>>() {});
			return Result.ok(r.readEntity(FileInfo.class));
		} else
			return Result.error(Result.getResponseErrorCode(Status.fromStatusCode(r.getStatus())));
	}


}
