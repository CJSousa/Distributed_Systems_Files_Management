package tp1.impl.service.rest.users;

import java.util.List;

import jakarta.ws.rs.WebApplicationException;
import tp1.api.User;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.impl.service.java.JavaUsers;

public class UsersResource implements RestUsers {

	final Users impl = new JavaUsers();

	@Override
	public String createUser(User user) {

		var result = impl.createUser(user);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(result.error().toString());
	}

	@Override
	public User getUser(String userId, String password) {
		var result = impl.getUser(userId, password);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(result.error().toString());
	}

	@Override
	public User updateUser(String userId, String password, User user) {
		var result = impl.updateUser(userId, password, user);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(result.error().toString());
	}

	@Override
	public User deleteUser(String userId, String password) {
		var result = impl.deleteUser(userId, password);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(result.error().toString());
	}

	@Override
	public List<User> searchUsers(String pattern) {
		var result = impl.searchUsers(pattern);

		if (result.isOK())
			return result.value();
		else
			throw new WebApplicationException(result.error().toString());
	}

	/*
	 * private Object getResult(Result result){ if( result.isOK() ) return
	 * result.value(); else throw new WebApplicationException
	 * (result.error().toString()); }
	 */

}
