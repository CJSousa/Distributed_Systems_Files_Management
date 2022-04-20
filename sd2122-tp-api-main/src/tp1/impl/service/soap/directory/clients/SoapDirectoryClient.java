package tp1.impl.service.soap.directory.clients;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;
import tp1.api.FileInfo;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.soap.SoapException;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.impl.service.soap.clients.SoapClient;

public class SoapDirectoryClient extends SoapClient implements Directory {
	
	private SoapDirectory directory;
	
	public SoapDirectoryClient(URI serverURI) {
		QName qname = new QName(SoapUsers.NAMESPACE, SoapUsers.NAME);
		Service service;
		try {
			service = Service.create(URI.create(serverURI + "wsdl").toURL(), qname);
			SoapDirectory soapDirectory = service.getPort(tp1.api.service.soap.SoapDirectory.class);
			this.directory = soapDirectory;
			SoapClient.setTimeouts(directory);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
		return super.reTry(() -> directory.writeFile(filename, data, userId, password));
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {
		try {
			this.directory.deleteFile(filename, userId, password);
			return Result.ok();
		} catch (SoapException sx) {
			return Result.error(sx.getErrorCode());
		} //catch (WebServiceException)
	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
		try {
			this.directory.shareFile(filename, userId, userIdShare, password);
			return Result.ok();
		} catch (SoapException sx) {
			return Result.error(sx.getErrorCode());
		} //catch (WebServiceException)
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
		try {
			this.directory.unshareFile(filename, userId, userIdShare, password);
			return Result.ok();
		} catch (SoapException sx) {
			return Result.error(sx.getErrorCode());
		} //catch (WebServiceException)
	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
		return super.reTry(() -> directory.getFile(filename, userId, accUserId, password));
	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {
		return super.reTry(() -> directory.lsFile(userId, password));
	}

	@Override
	public Result<Void> deleteFilesOfUser(String userId, String password) {
		try {
			this.directory.deleteFilesOfUser(userId, password);
			return Result.ok();
		} catch (SoapException sx) {
			return Result.error(sx.getErrorCode());
		} //catch (WebServiceException)
	}

}