package tp1.impl.service.rest;

import java.net.URI;
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

import java.util.List;

import tp1.api.User;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;

public class RestUsersClient extends RestClient implements Users {
	
	final WebTarget target;

	RestUsersClient(URI serverURI) {
		super( serverURI );
		target = client.target( serverURI ).path( RestUsers.PATH );
	}

	@Override
	public Result<String> createUser(User user) {
		Response r = target.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			System.out.println("Success, created user with id: " );
			return r.readEntity(String.class);
		}
		else {
			System.out.println("Error, HTTP error status: " + r.getStatus() );
		}
		return null;	
		/*
		return super.reTry( () -> {
			return clt_createUser( user );
		});
		*/
	}

	@Override
	public Result<User> getUser(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<User> deleteUser(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

}
