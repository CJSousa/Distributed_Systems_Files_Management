package tp1.impl.service.soap.files.clients;

import java.net.MalformedURLException;
import java.net.URI;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;
import tp1.api.service.soap.SoapException;
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
			service = Service.create(URI.create(serverURI + "wsdl").toURL(), qname);
			SoapFiles soapFiles = service.getPort(tp1.api.service.soap.SoapFiles.class);
			this.files = soapFiles;
			SoapClient.setTimeouts(files);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Result<Void> writeFile(String fileId, byte[] data, String token) {
		return super.reTry(() -> files.writeFile(fileId, data, token));
		/*try {
			this.files.writeFile(fileId, data, token);
			return Result.ok();
		} catch (SoapException sx) {
			return Result.error(sx.getErrorCode());
		} //catch (WebServiceException)
		*/
	}

	@Override
	public Result<Void> deleteFile(String fileId, String token) {
		try {
			this.files.deleteFile(fileId, token);
			return Result.ok();
		} catch (SoapException sx) {
			return Result.error(sx.getErrorCode());
		} //catch (WebServiceException)
	}

	@Override
	public Result<byte[]> getFile(String fileId, String token) {
		return super.reTry(() -> files.getFile(fileId, token));
	}

}
