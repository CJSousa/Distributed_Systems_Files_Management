package tp1.api.service.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import tp1.api.FileInfo;

@WebService(serviceName=SoapFiles.NAME, targetNamespace=SoapFiles.NAMESPACE, endpointInterface=SoapFiles.INTERFACE)
public interface SoapFiles {

	static final String NAME = "files";
	static final String NAMESPACE = "http://sd2122";
	static final String INTERFACE = "tp1.api.service.soap.SoapFiles";

	@WebMethod
	byte[] getFile(String fileId, String token) throws FilesException;

	@WebMethod
	void deleteFile(String fileId, String token) throws FilesException;
	
	@WebMethod
	void writeFile(String fileId, byte[] data, String token) throws FilesException;

	/*
	@WebMethod
	FileInfo findFile(String fileId, String token) throws FilesException;
	*/
}
