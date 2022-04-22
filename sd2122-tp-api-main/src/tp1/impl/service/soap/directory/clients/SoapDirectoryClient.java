package tp1.impl.service.soap.directory.clients;

import java.net.MalformedURLException;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import tp1.api.FileInfo;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.impl.service.soap.clients.SoapClient;

public class SoapDirectoryClient extends SoapClient implements Directory {
	
	private SoapDirectory directory;
	
	public SoapDirectoryClient(URI serverURI) {
		QName qname = new QName(SoapDirectory.NAMESPACE, SoapDirectory.NAME);
		Service service;
		try {
			service = Service.create(URI.create(serverURI + "?wsdl").toURL(), qname);
			SoapDirectory soapDirectory = service.getPort(tp1.api.service.soap.SoapDirectory.class);
			this.directory = soapDirectory;
			SoapClient.setTimeouts(directory);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
		return super.reTry(() -> directory.writeFile(filename, data, userId, password));
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {
		return super.reTry(() -> directory.deleteFile(filename, userId, password));
	
	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
		return super.reTry(() -> directory.shareFile(filename, userId, userIdShare, password));
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
		return super.reTry(() -> directory.unshareFile(filename, userId, userIdShare, password));	
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
		return super.reTry(() -> directory.deleteFilesOfUser(userId, password));
	}

}
