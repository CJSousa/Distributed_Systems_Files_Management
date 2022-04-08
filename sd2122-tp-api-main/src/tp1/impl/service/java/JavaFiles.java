package tp1.impl.service.java;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tp1.api.service.util.Files;
import java.nio.file.Files;
import java.nio.file.Paths;
import tp1.api.service.util.Result;
import tp1.impl.service.rest.files.FilesResource;

public class JavaFiles implements Files {
	

	private final List<String> files = new ArrayList<String>();
	private static Logger Log = Logger.getLogger(FilesResource.class.getName());

	@Override
	public void writeFile(String fileId, byte[] data, String token) {
	
		Log.info("writeFile : fileId = " + fileId + "; token = " + token);
		 
		// Check if token is valid
		if( token == null ) {
			Log.info("Token invalid.");
			//return Result.error(Result.ErrorCode.FORBIDDEN);
		}
		
		// Check if fileId is valid
		if ( fileId == null ) {
			Log.info("fileId invalid.");
			//return Result.error(Result.ErrorCode.BAD_REQUEST);
		}
		
		files.add(fileId);
		Files.write(Paths.get(fileId), data);
		
	}

	@Override
	public void deleteFile(String fileId, String token) {
		
		Log.info("deleteFile : fileId = " + fileId + "; token = " + token);
		
		// Check if fileId exists
		if( !files.contains(fileId) ) {
			Log.info("fileId does not exist.");
			//return Result.error(Result.ErrorCode.NOT_FOUND);
		}
		
		// Check if token is valid
		if( token == null ) {
			Log.info("Token invalid.");
			//return Result.error(Result.ErrorCode.FORBIDDEN);
		}
		
		// Check if fileId is valid
		if ( fileId == null ) {
			Log.info("fileId invalid.");
			//return Result.error(Result.ErrorCode.BAD_REQUEST);
		}
		
		files.remove(fileId);
		
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		
		Log.info("getFile : fileId = " + fileId + "; token = " + token);
		
		byte[] data = null;
		
		try {
			data = Files.readAllBytes(Paths.get(fileId));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Check if fileId exists - but could data be null in write method?
		if( data == null ) {
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
