package tp1.impl.service.soap.files.clients;

import java.net.MalformedURLException;

import java.net.URI;
import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;
import tp1.impl.service.soap.clients.SoapClient;

public class SoapFilesClient extends SoapClient implements Files {
	
	private SoapFiles files;
	
	public SoapFilesClient(URI serverURI) {
		QName qname = new QName(SoapFiles.NAMESPACE, SoapFiles.NAME);
		Service service;
		try {
			service = Service.create(URI.create(serverURI + "/files?wsdl").toURL(), qname);
			SoapFiles soapFiles = service.getPort(tp1.api.service.soap.SoapFiles.class);
			this.files = soapFiles;
			SoapClient.setTimeouts(files);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
		return super.reTry(() -> clt_writeFile(fileId, data, token));
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		return super.reTry(() -> clt_deleteFile(fileId, token));
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		return super.reTry(() -> clt_getFile(fileId, token));
	}

	private Result<Void> clt_writeFile(String fileId, byte[] data, String token){
		try {
			files.writeFile(fileId, data, token);
            return Result.ok();
        } catch (FilesException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}
	
	private Result<Void> clt_deleteFile(String fileId, String token){
		try {
			files.deleteFile(fileId, token);
            return Result.ok();
        } catch (FilesException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}
	
	private Result<byte[]> clt_getFile(String fileId, String token){
		try {
            return Result.ok(files.getFile(fileId, token));
        } catch (FilesException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}

}
