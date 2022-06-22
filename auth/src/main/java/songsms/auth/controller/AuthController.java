package songsms.auth.controller;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import songsms.auth.NotAuthorizedException;
import songsms.auth.NotFoundException;
import songsms.auth.model.User;
import songsms.auth.service.IUserService;

/**
 * Controller for the users table and the authentication of users.
 * @author Julian Knepel
 */
@RestController
@EnableDiscoveryClient
@RequestMapping(value = "/auth")
public class AuthController {
	
	private final IUserService userService;

	@Autowired
	public AuthController(IUserService userService) {
		this.userService = userService;
	}
	
	/**
	 * GET GATEWAY/auth <br/>
	 * Returns all users in json format.
	 * @return ResponseEntity
	 */
    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getUsers() {
    	List<User> users = userService.getAll();
		
		return new ResponseEntity<String>(users.toString(), HttpStatus.OK);
    }
    
    /**
     * POST GATEWAY/auth <br/>
     * Authenticates user when id and password are correct.
     * @param loginData json-string containing userId and password
     * @return ResponseEntity
     */
    @PostMapping(consumes = "application/json", produces = "text/plain")
    public ResponseEntity<String> authenticate(@RequestBody String loginData) {
    	try {
    		String token = userService.authenticateUser(loginData);
    		return new ResponseEntity<String>(token, HttpStatus.OK);
    	} catch(JSONException | IllegalArgumentException e) {
    		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    	} catch(NotFoundException | NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * POST GATEWAY/auth <br/>
     * Checks if token is valid and returns corresponding userId.
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @PostMapping(produces = "text/plain")
    public ResponseEntity<String> validate(@RequestHeader("Authorization") String token) {
    	try {
    		User user = userService.getUserByToken(token);
    		return new ResponseEntity<String>(user.getId(), HttpStatus.OK);
    	} catch(NotFoundException e) {
    		return new ResponseEntity<String>(token, HttpStatus.UNAUTHORIZED);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}

