package tp1.impl.service.java.files;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import tp1.api.service.util.Files;
import java.io.IOException;
import java.nio.file.Paths;
import tp1.api.service.util.Result;
import tp1.impl.service.rest.files.FilesResource;

public class JavaFiles implements Files {
	

	private final Set<String> files = new HashSet<String>();
	private static Logger Log = Logger.getLogger(FilesResource.class.getName());

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
	
		Log.info("writeFile : fileId = " + fileId + "; token = " + token);
		 
		// Check if token is valid
		if( token == null ) {
			Log.info("Token invalid.");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}
		
		// Check if fileId is valid
		if ( fileId == null ) {
			Log.info("fileId invalid.");
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}
		
		files.add(fileId);
		
		try {
			java.nio.file.Files.write(Paths.get(fileId), data);
		} catch (IOException e) {
			//ASK LATER
			e.printStackTrace();
		}
		
		return Result.ok();
		
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		
		Log.info("deleteFile : fileId = " + fileId + "; token = " + token);
		
		// Check if fileId exists
		if( !files.contains(fileId) ) {
			Log.info("fileId does not exist.");
			return Result.error(Result.ErrorCode.NOT_FOUND);
		}
		
		// Check if token is valid
		if( token == null ) {
			Log.info("Token invalid.");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}
		
		// Check if fileId is valid
		if ( fileId == null ) {
			Log.info("fileId invalid.");
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}
		
		files.remove(fileId);
		return Result.ok();
		
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		
		Log.info("getFile : fileId = " + fileId + "; token = " + token);
		
		byte[] data = null;
		
		try {
			data = java.nio.file.Files.readAllBytes(Paths.get(fileId));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Check if fileId exists - but could data be null in write method?
		if( data == null || !files.contains(fileId)) {
			Log.info("fileId does not exist.");
			return Result.error(Result.ErrorCode.NOT_FOUND);
		}
		
		// Check if token is valid
		if( token == null ) {
			Log.info("Token invalid.");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}
		
		// Check if fileId is valid
		if ( fileId == null ) {
			Log.info("fileId invalid.");
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}
		
		return Result.ok(data);
	}

}
