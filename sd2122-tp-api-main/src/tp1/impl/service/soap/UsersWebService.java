package tp1.impl.service.soap;

import java.util.List;

import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;

public class UsersWebService implements SoapUsers {

	@Override
	public String createUser(User user) throws UsersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUser(String userId, String password) throws UsersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User updateUser(String userId, String password, User user) throws UsersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User deleteUser(String userId, String password) throws UsersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> searchUsers(String pattern) throws UsersException {
		// TODO Auto-generated method stub
		return null;
	}

}
