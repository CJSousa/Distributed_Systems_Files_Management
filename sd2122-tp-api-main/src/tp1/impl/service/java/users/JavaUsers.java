package tp1.impl.service.java.users;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import jakarta.inject.Singleton;
import tp1.api.User;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.impl.service.java.directory.clients.DirectoryClientFactory;
import tp1.impl.service.rest.users.UsersResource;

@Singleton
public class JavaUsers implements Users {

	private final ConcurrentMap<String, User> users = new ConcurrentHashMap<String, User>();
	private static Logger Log = Logger.getLogger(UsersResource.class.getName());

	@Override
	public Result<String> createUser(User user) {
		Log.info("createUser : " + user);

		var userId = user.getUserId();

		// Check if user data is valid
		if (userId == null || user.getPassword() == null || user.getFullName() == null || user.getEmail() == null) {
			Log.info("User object invalid.");
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		// Check if userId already exists
		if (users.containsKey(userId)) {
			Log.info("User already exists.");
			return Result.error(Result.ErrorCode.CONFLICT);
		}

		// Add the user to the map of users
		users.put(userId, user);
		return Result.ok(user.getUserId());
	}

	@Override
	public Result<User> getUser(String userId, String password) {
		Log.info("getUser : user = " + userId + "; pwd = " + password);

		// Check if userId is valid
		if (userId == null) {
			Log.info("userId null.");
			return Result.error(Result.ErrorCode.BAD_REQUEST);
		}

		var user = users.get(userId);

		// Check if user exists
		if (user == null) {
			Log.info("User does not exist.");
			return Result.error(Result.ErrorCode.NOT_FOUND);
		}

		// Check if password is null
		if (password == null) {
			Log.info("password null.");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}

		// Check if the password is correct
		if (!user.getPassword().equals(password)) {
			Log.info("password is incorrect.");
			return Result.error(Result.ErrorCode.FORBIDDEN);
		}

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		Log.info("updateUser : user = " + userId + "; pwd = " + password + " ; user = " + user);

		var result = this.getUser(userId, password);

		if (!result.isOK())
			return Result.error(result.error());

		var newUser = result.value();
		var newFullName = user.getFullName();
		var newEmail = user.getEmail();
		var newPassword = user.getPassword();

		// Check full name
		if (newFullName != null)
			newUser.setFullName(newFullName);

		// Check email
		if (newEmail != null)
			newUser.setEmail(newEmail);

		// Check password
		if (newPassword != null)
			newUser.setPassword(newPassword);

		return Result.ok(newUser);
	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		Log.info("deleteUser : user = " + userId + "; pwd = " + password);

		var result = this.getUser(userId, password);

		if (!result.isOK())
			return Result.error(result.error());

		var userToRemove = result.value();

		// Check if user is valid
		if (userToRemove == null) {
			Log.info("User does not exist.");
			return Result.error(Result.ErrorCode.NOT_FOUND);
		}

		// Process delete
		var dirResult = DirectoryClientFactory.getClient().deleteFilesOfUser(userId, password);
		
		if (!dirResult.isOK())
			return Result.error(Result.ErrorCode.BAD_REQUEST);

		users.remove(userId);

		return Result.ok(userToRemove);
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);

		List<User> patternedUsers = new ArrayList<>();

		for (User u : users.values()) {
			if (u.getFullName().toLowerCase().contains(pattern.toLowerCase()))
				patternedUsers.add(new User(u.getUserId(), u.getFullName(), u.getEmail(), ""));
		}

		return Result.ok(patternedUsers);
	}

}
