package tp1.impl.service.rest.files;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;
import tp1.impl.service.java.files.JavaFiles;

@Singleton
public class FilesResource implements RestFiles {
	
	final Files impl = new JavaFiles();

	@Override
	public void writeFile(String fileId, byte[] data, String token) {
		
		var result = impl.writeFile(fileId, data, token);

		if (!result.isOK())
			throw new WebApplicationException(this.getError(result));
		
	}

	@Override
	public void deleteFile(String fileId, String token) {
		var result = impl.deleteFile(fileId, token);
		
		if (!result.isOK())
			throw new WebApplicationException(this.getError(result));
	}

	@Override
	public byte[] getFile(String fileId, String token) {
		var result = impl.getFile(fileId, token);

		if (result.isOK())
			return result.value();
		else 
			throw new WebApplicationException(this.getError(result));
	}
	
	/*
	@Override
	public FileInfo findFile(String fileId, String token) {
		var result = impl.findFile(fileId, token);

		if (result.isOK())
			return result.value();
		else 
			throw new WebApplicationException(this.getError(result));
	}
	*/
	
	/**
	 * Transforms an Error Code Result to a comprehensible Status Response
	 * 
	 * @param result - result of an operation
	 * @return Status response
	 */
	private Status getError(Result<?> result) {
		switch (result.error()) {
		case CONFLICT:
			return Status.CONFLICT;
		case NOT_FOUND:
			return Status.NOT_FOUND;
		case BAD_REQUEST:
			return Status.BAD_REQUEST;
		case FORBIDDEN:
			return Status.FORBIDDEN;
		case INTERNAL_ERROR:
			return Status.INTERNAL_SERVER_ERROR;
		default:
			return Status.NOT_IMPLEMENTED;
		}
	}

}
