package tp1.impl.service.rest.directory;

import java.util.List;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.FileInfo;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.impl.service.java.directory.JavaDirectory;

@Singleton
public class DirectoryResource implements RestDirectory {

	final Directory impl = new JavaDirectory();

	@Override
	public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
		var result = impl.writeFile(filename, data, userId, password);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(this.getError(result));
	}

	@Override
	public void deleteFile(String filename, String userId, String password) {
		var result = impl.deleteFile(filename, userId, password);
		if (!result.isOK()) throw new WebApplicationException(this.getError(result));

	}

	@Override
	public void shareFile(String filename, String userId, String userIdShare, String password) {
		var result = impl.shareFile(filename, userId, userIdShare, password);
		if (!result.isOK()) throw new WebApplicationException(this.getError(result));
	}

	@Override
	public void unshareFile(String filename, String userId, String userIdShare, String password) {
		var result = impl.unshareFile(filename, userId, userIdShare, password);
		if (!result.isOK()) throw new WebApplicationException(this.getError(result));
	}

	@Override
	public byte[] getFile(String filename, String userId, String accUserId, String password) {
		var result = impl.getFile(filename, userId, accUserId, password);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(this.getError(result));
	}

	@Override
	public List<FileInfo> lsFile(String userId, String password) {
		var result = impl.lsFile(userId, password);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(this.getError(result));
	}

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
