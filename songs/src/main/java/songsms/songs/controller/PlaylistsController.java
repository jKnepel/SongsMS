package songsms.songs.controller;

import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import songsms.songs.ForbiddenException;
import songsms.songs.NotAuthorizedException;
import songsms.songs.NotFoundException;
import songsms.songs.model.Playlist;
import songsms.songs.service.IPlaylistService;

/**
 * RESTController for the playlists table.
 * @author Julian Knepel
 */
@RestController
@EnableDiscoveryClient
@RequestMapping(value = "/songs/playlists")
public class PlaylistsController {
    
	@Autowired
	private IPlaylistService playlistService;
	
	@Autowired
	private EurekaClient discoveryClient;
    
    /**
     * GET GATEWAY/songs/playlists/{listId} <br/>
     * Returns the playlist with the corresponding id if client is owner or playlist is public.
     * @param listId
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @GetMapping(value = "/{listId}", produces = "application/json")
    public ResponseEntity<String> getList(@PathVariable(value = "listId") int listId, @RequestHeader("Authorization") String token) {
    	try {
    		String userId = validateToken(token);
    		
    		Playlist playlist = playlistService.getPlaylistById(userId, listId);
        	
			return new ResponseEntity<String>(playlist.toString(), HttpStatus.OK);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(NotFoundException e) {
    		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    	} catch(ForbiddenException e) {
    		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    

    /**
     * GET GATEWAY/songs/playlists?userId= <br/>
     * Returns all playlists by passed user that the client has access to.
     * @param ownerId in userId parameter
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getUsersLists(@RequestParam("userId") String ownerId, @RequestHeader("Authorization") String token) {
    	try {
    		String userId = validateToken(token);
    		
    		List<Playlist> playlists = playlistService.getPlaylistsByUser(userId, ownerId);
        	
        	return new ResponseEntity<String>(playlists.toString(), HttpStatus.OK);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * POST GATEWAY/songs/playlists <br/>
     * Creates a playlist from payload if all songs exist in database.
     * @param songListData containing name, isPrivate and song id's
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> createList(@RequestBody String songListData, @RequestHeader("Authorization") String token) {
    	try {
    		String userId = validateToken(token); 
    		
	    	int listId = playlistService.postPlaylist(userId, songListData);
			
	    	return new ResponseEntity<String>(""+listId, HttpStatus.CREATED);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(NotFoundException | JSONException | IllegalArgumentException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch(ForbiddenException e) {
    		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * PUT GATEWAY/songs/playlists/{listId} <br/>
     * Updates the playlist with the corresponding id.
     * @param listId
     * @param songListData containing id, name, isPrivate and song id's
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @PutMapping(value = "/{listId}", consumes = "application/json")
    public ResponseEntity<String> putList(@PathVariable(value="listId") int listId, @RequestBody String songListData, @RequestHeader("Authorization") String token) {
    	try {
    		String userId = validateToken(token); 
    		
    		playlistService.putPlaylist(userId, listId, songListData);
			
    		return new ResponseEntity<String>(""+listId, HttpStatus.NO_CONTENT);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(NotFoundException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch(JSONException | IllegalArgumentException e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} catch(ForbiddenException e) {
    		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * DELETE GATEWAY/songs/playlists/{listId} <br/>
     * Delete the playlist with the given id.
     * @param listId
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @DeleteMapping(value = "/{listId}")
    public ResponseEntity<String> deleteList(@PathVariable(value="listId") int listId, @RequestHeader("Authorization") String token) {
    	try {
    		String userId = validateToken(token);
    		
	    	playlistService.deletePlaylist(userId, listId);			
	    	return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(NotFoundException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} catch(ForbiddenException e) {
    		return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * Sends API call to auth-endpoint to validate token
     * @param token
     * @return userId
     * @throws NotAuthorizedException when token is not correct
     */
    private String validateToken(String token) throws NotAuthorizedException {
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", token);
    	
    	HttpEntity<String> request = new HttpEntity<String>(headers);
    	
    	try {
    		ResponseEntity<String> response = restTemplate.postForEntity(discoveryClient.getNextServerFromEureka("AUTH-SERVICE", false).getHomePageUrl() + "/auth", request, String.class);
    		
    		return response.getBody();
    	} catch(Exception e) {
    		throw new NotAuthorizedException();
    	}
    }
}

