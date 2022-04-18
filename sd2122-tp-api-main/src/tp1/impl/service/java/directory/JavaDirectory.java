package tp1.impl.service.java.directory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.impl.service.java.files.clients.FilesClientFactory;
import tp1.impl.service.java.users.clients.UsersClientFactory;

public class JavaDirectory implements Directory {

	private static final String DELIMITER = "!*!*!*!";

	private final Map<String, HashMap<String, FileInfo>> userFiles = new HashMap<String, HashMap<String, FileInfo>>();
	// <serverId, userId> como é que sabemos os servidores que responderem ao pedido
	// getClient
	// <serverId, fileID>

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {

		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Check if userId exists in directory
		HashMap<String, FileInfo> files = userFiles.get(userId);

		if (files == null) {
			files = new HashMap<String, FileInfo>();
			userFiles.put(userId, files);
		}

		// Check if file can be written

		String fileId = userId + DELIMITER + filename;

		Result fileResult = FilesClientFactory.getClient().writeFile(fileId, data, password);

		if (!fileResult.isOK())
			return Result.error(userResult.error());

		FileInfo file = new FileInfo(userId, filename, FilesClientFactory.getAvailableURI() + "/files/" + fileId,
				new HashSet<String>());
		files.put(fileId, file);

		return Result.ok(file);
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {

		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Check if userId exists in directory
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		// Check if file can be deleted

		String fileId = userId + DELIMITER + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		if (!file.getOwner().equals(userId))
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		// Result fileResult = FilesClientFactory.getClient().deleteFile(fileId,
		// Token.get());

		var fileResult = FilesClientFactory.getClient().deleteFile(fileId, "token");

		if (!fileResult.isOK())
			return Result.error(fileResult.error());

		files.remove(fileId);
		return Result.ok();
	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {

		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Check if userId exists in directory
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		// Check if userIdShare exists in the system
		ErrorCode userShareError = UsersClientFactory.getClient().getUser(userIdShare, password).error();
		if (userShareError != Result.ErrorCode.FORBIDDEN && !userId.equals(userIdShare))
			return Result.error(userShareError);

		// Check if file can be shared

		String fileId = userId + DELIMITER + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		if (!file.getOwner().equals(userId))
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		if (!userId.equals(userIdShare)) {

			// Add userIdShare to set of user IDs with whom the file has been shared
			Set<String> sharedWith = file.getSharedWith();
			sharedWith.add(userIdShare);
			file.setSharedWith(sharedWith);

			// Check if userIdShare exists in directory
			Map<String, FileInfo> userIdSharedFiles = userFiles.get(userIdShare);
			if (userIdSharedFiles == null)
				userFiles.put(userIdShare, new HashMap<String, FileInfo>());

			// Add file to userIdShare
			userIdSharedFiles.put(fileId, file);
		}

		return Result.ok();
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {

		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Check if userIdShare exists in the system
		ErrorCode userShareError = UsersClientFactory.getClient().getUser(userIdShare, password).error();
		if (userShareError != Result.ErrorCode.FORBIDDEN && !userId.equals(userIdShare))
			return Result.error(userShareError);

		// Check if userId exists in directory
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		// Check if file can be unshared

		String fileId = userId + DELIMITER + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		if (!file.getOwner().equals(userId))
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		Set<String> sharedWith = file.getSharedWith();

		// Check if userIdShare exists in directory
		Map<String, FileInfo> userIdSharedFiles = userFiles.get(userIdShare);

		// MAYBE WE DONT NEED SECOND CONDITION?
		if (sharedWith.contains(userIdShare) && userIdSharedFiles != null && userIdSharedFiles.containsKey(fileId)) {

			// Remove userIdShare from the set of user IDs with whom the file has been
			// shared
			sharedWith.remove(userIdShare);
			file.setSharedWith(sharedWith);

			// Remove file from userIdShare
			userIdSharedFiles.remove(fileId);
		}

		return Result.ok();
	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {

		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Check if accUserId exists in the system
		var accUserError = UsersClientFactory.getClient().getUser(accUserId, password).error();
		if (accUserError != Result.ErrorCode.FORBIDDEN && !userId.equals(accUserId))
			return Result.error(accUserError);

		// Check if userId and accUserId exist in directory
		Map<String, FileInfo> files = userFiles.get(userId);


		if (files == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		// Check if file exists
		String fileId = userId + DELIMITER + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		// Check if file can be read
		if (!this.canRead(userFiles.get(accUserId), fileId, file, accUserId)) {
			System.out.println(filename + " shared with: " + file.getSharedWith());
			System.out.println(accUserId + " has access to: " + userFiles.get(accUserId));
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}
			

		throw new WebApplicationException(Response.temporaryRedirect(URI.create(file.getFileURL())).build());

		// Check if read request can be made
		/*
		 * if (!fileResult.isOK()) return Result.error(fileResult.error());
		 * 
		 * return Result.ok(fileResult.value());
		 */
	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {
		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Check if userId exists in directory
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		List<FileInfo> filesList = new ArrayList<>();

		for (FileInfo f : files.values()) {
			filesList.add(f);
		}

		return Result.ok(filesList);

	}

	// Auxiliary Method

	/**
	 * Check if a user can read a file
	 * 
	 * @param readerFiles - set of files a user has access to
	 * @param fileId      - fileId of the file to be read
	 * @param file        - file to be read
	 * @param readerId    - user requesting to read file
	 * @return true if the reader can read the file
	 */
	private boolean canRead(Map<String, FileInfo> readerFiles, String fileId, FileInfo file, String readerId) {
		return readerFiles != null && readerFiles.containsKey(fileId)
				&& (file.getOwner().equals(readerId) || file.getSharedWith().contains(readerId));
	}

}
