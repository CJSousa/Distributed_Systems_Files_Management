package tp1.impl.service.soap.users.clients;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;
import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.impl.service.soap.clients.SoapClient;

public class SoapUsersClient extends SoapClient implements Users {

	private SoapUsers users;

	public SoapUsersClient(URI serverURI) {
		QName qname = new QName(SoapUsers.NAMESPACE, SoapUsers.NAME);
		Service service;
		try {
			service = Service.create(URI.create(serverURI + "wsdl").toURL(), qname);
			SoapUsers soapUsers = service.getPort(tp1.api.service.soap.SoapUsers.class);
			this.users = soapUsers;
			SoapClient.setTimeouts(users);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Result<String> createUser(User user) {
		return super.reTry(() -> users.createUser(user));
	}

	@Override
	public Result<User> getUser(String userId, String password) {
		return super.reTry(() -> users.getUser(userId, password));
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		return super.reTry(() -> users.updateUser(userId, password, user));
	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		return super.reTry(() -> users.deleteUser(userId, password));
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		return super.reTry(() -> users.searchUsers(pattern));
	}

}
