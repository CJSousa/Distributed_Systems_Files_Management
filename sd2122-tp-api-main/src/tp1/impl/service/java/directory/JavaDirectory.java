package tp1.impl.service.java.directory;


import java.net.URI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.impl.service.java.files.clients.FilesClientFactory;
import tp1.impl.service.java.users.clients.UsersClientFactory;

@Singleton
public class JavaDirectory implements Directory {

	private static final String DELIMITER = "!*!*!*!";

	private final Map<String, ConcurrentHashMap<String, FileInfo>> userFiles = new ConcurrentHashMap<String, ConcurrentHashMap<String, FileInfo>>();
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
		ConcurrentHashMap<String, FileInfo> files = userFiles.get(userId);

		if (files == null) {
			files = new ConcurrentHashMap<String, FileInfo>();
			userFiles.put(userId, files);
		}

		// Check if file can be written

		String fileId = userId + DELIMITER + filename;

		Result fileResult = FilesClientFactory.getClient().writeFile(fileId, data, password);
		
		if (!fileResult.isOK())
			return Result.error(userResult.error());
		
		FileInfo file = new FileInfo(userId, filename, FilesClientFactory.getAvailableURI().toString() + "/files/" + fileId,
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

		// Process delete

		Set<String> usersSharedWith = files.get(fileId).getSharedWith();

		for (String s : usersSharedWith) {
			userFiles.get(s).remove(fileId);
		}

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

		// Process share

		// Add userIdShare to set of user IDs with whom the file has been shared
		Set<String> sharedWith = file.getSharedWith();
		sharedWith.add(userIdShare);
		file.setSharedWith(sharedWith);

		// Check if userIdShare exists in directory
		ConcurrentHashMap<String, FileInfo> userIdSharedFiles = userFiles.get(userIdShare);
		if (userIdSharedFiles == null)
			userFiles.put(userIdShare, new ConcurrentHashMap<String, FileInfo>());

		// Add file to userIdShare
		userIdSharedFiles.put(fileId, file);

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

		// Check if userIdShare exists in directory
		Map<String, FileInfo> userIdSharedFiles = userFiles.get(userIdShare);

		if (userIdSharedFiles == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		// Check if file can be unshared

		String fileId = userId + DELIMITER + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		if (!file.getOwner().equals(userId))
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		Set<String> sharedWith = file.getSharedWith();

		// Process unshare

		if (sharedWith.contains(userIdShare) && userIdSharedFiles.containsKey(fileId)) {

			// Remove userIdShare from the set of user IDs with whom the file has been
			// shared
			sharedWith.remove(userIdShare);
			file.setSharedWith(sharedWith);

			// Remove file from userIdShare
			if (!userId.equals(userIdShare))
				userIdSharedFiles.remove(fileId);
		}

		return Result.ok();
	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {

		var accUserResult = UsersClientFactory.getClient().getUser(accUserId, password);

		// Check if accUserId exists in the system
		if (!accUserResult.isOK())
			return Result.error(accUserResult.error());

		var userError = UsersClientFactory.getClient().getUser(userId, password).error();

		// Check if accUserId exists in the system
		if (userError != Result.ErrorCode.FORBIDDEN && !userId.equals(accUserId))
			return Result.error(userError);

		// Check if userId and accUserId exist in directory
		Map<String, FileInfo> userIdFiles = userFiles.get(userId);
		Map<String, FileInfo> accUserFiles = userFiles.get(accUserId);

		if (userIdFiles == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		if (accUserFiles == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		// Check if file exists
		String fileId = userId + DELIMITER + filename;
		FileInfo file = userIdFiles.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		// Check if file can be read
		if (!this.canRead(accUserFiles, fileId, file, accUserId))
			return Result.error(Result.ErrorCode.FORBIDDEN);
		else
			throw new WebApplicationException(Response.temporaryRedirect(URI.create(file.getFileURL())).build());
	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {
		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Check if userId has access to any files
		Map<String, FileInfo> files = userFiles.get(userId);

		List<FileInfo> filesList = new ArrayList<>();

		if (files != null) {

			for (FileInfo f : files.values()) {
				filesList.add(f);
			}
		}
		return Result.ok(filesList);

	}

	@Override
	public Result<Void> deleteFilesOfUser(String userId, String password) {
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files != null) {

			for (FileInfo f : new ArrayList<FileInfo>(files.values())) {

				String filename = f.getFilename();

				if (f.getOwner().equals(userId)) {
					var result = this.deleteFile(filename, userId, password);
					System.out.println("Delete request result: " + result);
				}
			}
		}

		return Result.ok();
	}

	// Auxiliary Methods

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
		return readerFiles.containsKey(fileId)
				&& (file.getOwner().equals(readerId) || file.getSharedWith().contains(readerId));
	}

}
