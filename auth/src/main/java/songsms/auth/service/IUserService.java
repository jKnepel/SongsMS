package songsms.auth.service;

import java.util.List;

import org.json.JSONException;

import songsms.auth.NotAuthorizedException;
import songsms.auth.NotFoundException;
import songsms.auth.model.User;

public interface IUserService {

	/**
	 * Returns the user with the passed userId.
	 * @param userId
	 * @return user
	 * @throws NotFoundException when the user doesn't exist
	 */
	User getUserById(String userId) throws NotFoundException;

	/**
	 * Returns the user with the passed token.
	 * @param token
	 * @return user
	 * @throws NotFoundException when the user doesn't exist
	 */
	User getUserByToken(String token) throws NotFoundException;
	
	/**
	 * Returns a list with every user in the database.
	 * @return list containing every user
	 */
	List<User> getAll();

	/**
	 * Authenticates user using userId and password.
	 * @param payload containing id and password
	 * @return token string when successful
	 * @throws JSONException when payload is not in json format or a key is missing
	 * @throws IllegalArgumentException when userId or password is empty
	 * @throws NotAuthorizedException user doesn't exist or when password is not correct
	 * @throws NotFoundException when the user doesn't exist
	 */
	String authenticateUser(String payload) throws JSONException, IllegalArgumentException, NotAuthorizedException, NotFoundException;

}
