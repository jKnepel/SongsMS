package songsms.auth.service;

import java.security.SecureRandom;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import songsms.auth.NotAuthorizedException;
import songsms.auth.NotFoundException;
import songsms.auth.model.User;
import songsms.auth.repository.UserRepository;

@Service
public class UserService implements IUserService {
	
	private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
	
	private static final SecureRandom secureRandom = new SecureRandom();
    private static final String base = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	@Override
	public User getUserById(String userId) throws NotFoundException {
		User user = repository.findByUserId(userId);
		
		if(user == null) throw new NotFoundException();
		else return user;
	}
	
	@Override
	public User getUserByToken(String token) {
		User user = repository.findByToken(token);
		
		if(user == null) throw new NotFoundException();
		else return user;
	}
	
	@Override
	public List<User> getAll() {
		return (List<User>) repository.findAll();
	}
	
	@Override
	public String authenticateUser(String payload) throws JSONException, IllegalArgumentException, NotFoundException, NotAuthorizedException {
		JSONObject userData = new JSONObject(payload);
		
		if(userData.getString("userId").length() == 0 || userData.getString("password").length() == 0)
			throw new IllegalArgumentException();
		
		User user = getUserById(userData.getString("userId"));
		
		if(user.getPassword().equals(userData.getString("password"))) {
			String token = createToken(15);
			
			user.setToken(token);
			
			repository.save(user);
			
			return token;
		} else {
			throw new NotAuthorizedException();
		}
	}
	
	/**
     * Creates a random token using the stringbuilder and returns it.
     * @param length
     * @return token string
     */
    private static String createToken(int length) {
    	StringBuilder stringBuilder = new StringBuilder(length);
    	   for(int i = 0; i < length; i++)
    		   stringBuilder.append(base.charAt(secureRandom.nextInt(base.length())));
    	   return stringBuilder.toString();
    }
}
