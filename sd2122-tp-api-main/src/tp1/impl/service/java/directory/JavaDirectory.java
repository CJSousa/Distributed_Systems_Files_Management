package tp1.impl.service.java.directory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import tp1.api.FileInfo;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Result.ErrorCode;
import tp1.impl.service.java.files.clients.FilesClientFactory;
import tp1.impl.service.java.users.clients.UsersClientFactory;

public class JavaDirectory implements Directory {

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
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files == null)
			userFiles.put(userId, new HashMap<String, FileInfo>());

		// Check if file can be written

		String fileId = "/" + userId + filename;
		
		//userId/filename
		
		Result fileResult = FilesClientFactory.getClient().writeFile(fileId, data, password);

		if (!fileResult.isOK())
			return Result.error(userResult.error());

		// DUVIDA no URL
		FileInfo file = new FileInfo(userId, filename, fileId, new HashSet<String>());
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
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		// Check if file can be deleted

		String fileId = "/" + userId + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		if (!file.getOwner().equals(userId))
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		

		//Result fileResult = FilesClientFactory.getClient().deleteFile(fileId, Token.get());

		var fileResult = FilesClientFactory.getClient().deleteFile(fileId, "");

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

		// Check if userIdShare exists in the system
		ErrorCode userShareError = UsersClientFactory.getClient().getUser(userIdShare, password).error();
		if (userShareError != Result.ErrorCode.FORBIDDEN)
			return Result.error(userShareError);

		// Check if userId exists in directory
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		// Check if file can be deleted

		String fileId = userId + "!*!*!*!" + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		if (!file.getOwner().equals(userId))
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		// DUVIDA
		// file.setSharedWith(SO ADICIONAR)
		
		// Add file to userIdShare
		userFiles.get(userIdShare).put(fileId, file);
		
		return Result.ok();
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
		
		var userResult = UsersClientFactory.getClient().getUser(userId, password);

		// Check if userId exists in the system
		if (!userResult.isOK())
			return Result.error(userResult.error());

		// Verificacao de validade de userIdShare

		// Check if userId exists in directory
		Map<String, FileInfo> files = userFiles.get(userId);

		if (files == null)
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		// Check if file can be deleted

		String fileId = "/" + userId + filename;
		FileInfo file = files.get(fileId);

		if (file == null)
			return Result.error(Result.ErrorCode.NOT_FOUND);

		if (!file.getOwner().equals(userId))
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		// DUVIDA
		// file.setSharedWith(SO REMOVER)
		
		// Add file to userIdShare
		userFiles.get(userIdShare).remove(fileId);
		
		return Result.ok();
		
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

}
