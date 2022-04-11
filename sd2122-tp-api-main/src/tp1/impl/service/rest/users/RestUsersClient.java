package tp1.impl.service.rest.users;

import java.net.URI;
import java.util.List;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import tp1.api.User;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.impl.service.rest.clients.RestClient;

public class RestUsersClient extends RestClient implements Users {
	
	final WebTarget target;

	public RestUsersClient(URI serverURI) {
		super( serverURI );
		target = client.target( serverURI ).path( RestUsers.PATH );
	}

	@Override
	public Result<String> createUser(User user) {
		return super.reTry( () -> {
			return clt_createUser(user);
		});
	}

	@Override
	public Result<User> getUser(String userId, String password) {
		return super.reTry( () -> {
			return clt_getUser(userId, password);
		});
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		return super.reTry( () -> {
			return clt_updateUser(userId, password, user);
		});
	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		return super.reTry( () -> {
			return clt_deleteUser(userId, password);
		});
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		return super.reTry( () -> {
			return clt_searchUsers(pattern);
		});
	}
	
	//
	
	
	/**
	 * 
	 * @param user
	 * @return
	 */
	private Result<String> clt_createUser(User user){
		Response r = target.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			System.out.println("Success, created user with id: " );
			return r.readEntity(new GenericType<Result<String>>() {});
		}
		else {
			System.out.println("Error, HTTP error status: " + r.getStatus() );
		}
		return null;	
	}
	
	/**
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	private Result<User> clt_getUser(String userId, String password) {
		
		Response r = target.path( userId )
				.queryParam(RestUsers.PASSWORD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) 
			return r.readEntity(new GenericType<Result<User>>() {});
		else
			System.out.println("Error, HTTP error status: " + r.getStatus() );
		return null;
		
	}
	

	/**
	 * 
	 * @param userId
	 * @param password
	 * @param user
	 * @return
	 */
	private Result<User> clt_updateUser(String userId, String password, User user) {
		
		Response r = target.path( userId )
				.queryParam(RestUsers.PASSWORD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(user, MediaType.APPLICATION_JSON));
		
		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			System.out.println("Successfuly updated the user:");
			return r.readEntity(new GenericType<Result<User>>() {});
		} else
			System.out.println("Error, HTTP error status: " + r.getStatus() );
		return null;
	}
	
	/**
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	private Result<User> clt_deleteUser(String userId, String password) {
		
		Response r = target.path( userId )
				.queryParam(RestUsers.PASSWORD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.delete();
		
		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			System.out.println("Successful removal of: ");
			return r.readEntity(new GenericType<Result<User>>() {});
		} else
			System.out.println("Error, HTTP error status: " + r.getStatus() );
		return null;

	}
	
	/**
	 * 
	 * @param pattern
	 * @return
	 */
	private Result<List<User>> clt_searchUsers(String pattern) {
		
		Response r = target
				.queryParam(RestUsers.QUERY, pattern)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			System.out.println("It worked");
			return r.readEntity(new GenericType<Result<List<User>>>() {});
		}
		else 
			System.out.println("Error, HTTP error status: " + r.getStatus() );
		
		return null;
	}
	

}
