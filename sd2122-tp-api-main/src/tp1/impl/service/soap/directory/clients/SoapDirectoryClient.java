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
		return super.reTry(() -> clt_writeFile(filename, data, userId, password));
	}

	@Override
	public Result<Void> deleteFile(String filename, String userId, String password) {
		return super.reTry(() -> clt_deleteFile(filename, userId, password));
	}

	@Override
	public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
		return super.reTry(() -> clt_shareFile(filename, userId, userIdShare, password));
	}

	@Override
	public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
		return super.reTry(() -> clt_unshareFile(filename, userId, userIdShare, password));	
	}

	@Override
	public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
		return super.reTry(() -> clt_getFile(filename, userId, accUserId, password));
	}

	@Override
	public Result<List<FileInfo>> lsFile(String userId, String password) {
		return super.reTry(() -> clt_lsFile(userId, password));
	}

	@Override
	public Result<Void> deleteFilesOfUser(String userId, String password) {
		return super.reTry(() -> clt_deleteFilesOfUser(userId, password));
	}
	
	@Override
	public Result<FileInfo> findFile(String filename, String userId, String accUserId, String password) {
		return super.reTry(() -> clt_findFile(filename, userId, accUserId, password));
	}
	

	private Result<FileInfo> clt_writeFile(String filename, byte[] data, String userId, String password){
		try {
            return Result.ok(directory.writeFile(filename, data, userId, password));
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
    }
	
	private Result<Void> clt_deleteFile(String filename, String userId, String password){
		try {
			directory.deleteFile(filename, userId, password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}
	
	private Result<Void> clt_shareFile(String filename, String userId, String userIdShare, String password){
		try {
			directory.shareFile(filename, userId, userIdShare, password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}
	
	private Result<Void> clt_unshareFile(String filename, String userId, String userIdShare, String password){
		try {
			directory.unshareFile(filename, userId, userIdShare, password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}
	
	private Result<byte[]> clt_getFile(String filename, String userId, String accUserId, String password){
		try {
            return Result.ok(directory.getFile(filename, userId, accUserId, password));
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}
	
	private Result<List<FileInfo>> clt_lsFile(String userId, String password){
		try {
            return Result.ok(directory.lsFile(userId, password));
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}
	
	private Result<Void> clt_deleteFilesOfUser(String userId, String password){
		try {
			directory.deleteFilesOfUser(userId, password);;
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}

	private Result<FileInfo> clt_findFile(String filename, String userId, String accUserId, String password) {
		try {
            return Result.ok(directory.findFile(filename, userId, accUserId, password));
        } catch (DirectoryException e) {
            return Result.error(SoapClient.getExceptionErrorCode(e));
        }
	}

	
}
