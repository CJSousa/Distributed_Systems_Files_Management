package tp1.impl.service.rest.files;

import jakarta.ws.rs.WebApplicationException;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;
import tp1.impl.service.java.JavaFiles;

public class FilesResource implements RestFiles {
	
	final Files impl = new JavaFiles();

	@Override
	public void writeFile(String fileId, byte[] data, String token) {
		
		var result = impl.writeFile(fileId, data, token);

		if (!result.isOK())
			throw new WebApplicationException(result.error().toString());
		
	}

	@Override
	public void deleteFile(String fileId, String token) {
		var result = impl.deleteFile(fileId, token);
		
		if (!result.isOK())
			throw new WebApplicationException(result.error().toString());
	}

	@Override
	public byte[] getFile(String fileId, String token) {
		var result = impl.getFile(fileId, token);

		if (result.isOK())
			return result.value();
		else 
			throw new WebApplicationException(result.error().toString());
	}

}
