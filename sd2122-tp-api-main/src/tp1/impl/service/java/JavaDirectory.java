package tp1.impl.service.java;

import java.util.HashMap;
import java.util.Map;

import tp1.api.FileInfo;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;

public class JavaDirectory implements Directory {

	private final Map<String, FileInfo> userFiles = new HashMap<String, FileInfo>();
	

	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
		
		Result result = UsersClientFactory.getClient().getUser(userId, password);
		
		
		
		return null;
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

	

}
