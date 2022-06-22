package songsms.albums.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

import songsms.albums.NotAuthorizedException;
import songsms.albums.NotFoundException;
import songsms.albums.service.IAlbumService;

/**
 * RESTController for the albums endpoint.
 * @author Julian Knepel
 */
@RestController
@EnableDiscoveryClient
@RequestMapping(value = "/albums")
public class AlbumsController {
	
	@Autowired
	private IAlbumService albumService;
	
	@Autowired
	private EurekaClient discoveryClient;

	/**
	 * GET GATEWAY/albums/{albumId}
	 * Returns the album with the corresponding id.
	 * @param albumId
	 * @param token in Authorization header
	 * @return ResponseEntity
	 */
    @GetMapping(value = "/{albumId}", produces = "application/json")
    public ResponseEntity<String> getAlbumById(@PathVariable (value = "albumId") String albumId, @RequestHeader("Authorization") String token) {
    	try {
    		validateToken(token);
    		
    		String albumString = albumService.getAlbumById(albumId).toString();
    		
    		JSONObject album = addSongMetadataToAlbum(new JSONObject(albumString), token);
    		
    		return new ResponseEntity<String>(album.toString(), HttpStatus.OK);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(NotFoundException e) {
    		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * GET GATEWAY/albums
     * Returns all albums or albums with corresponding name if name parameter is given
     * @param albumName
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getAlbums(@RequestParam(name = "name", required = false) String albumName, @RequestHeader("Authorization") String token) {
    	try {
    		validateToken(token);
    		
    		// gets all albums or, if name parameter is defined, a list of albums with that name
    		String albumsString = ((albumName == null) ? albumService.getAll() : albumService.getAlbumsByName(albumName)).toString();
    		
    		JSONArray albums = addSongMetadataToAlbums(new JSONArray(albumsString), token);
    		
    		return new ResponseEntity<String>(albums.toString(), HttpStatus.OK);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

    /**
     * POST GATEWAY/albums
     * Creates album in database
     * @param albumData containing name, cover URL, youtube-link and list of song id's
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> postAlbum(@RequestBody String albumData, @RequestHeader("Authorization") String token) {
    	try {
    		validateToken(token); 
    		
	    	String albumId = albumService.postAlbum(discoveryClient, token, albumData);
			
			return new ResponseEntity<String>(albumId, HttpStatus.CREATED);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(JSONException | IllegalArgumentException e) {
    		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

    /**
     * PUT GATEWAY/albums
     * Updates album in database
     * @param albumId
     * @param albumData containing albumId, name, cover URL, youtube-link and list of song id's 
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @PutMapping(value = "/{albumId}", consumes = "application/json")
    public ResponseEntity<String> putAlbum(@PathVariable (value = "albumId") String albumId, @RequestBody String albumData, @RequestHeader("Authorization") String token) {
    	try {
    		validateToken(token); 
    		
    		albumService.putAlbum(discoveryClient, token, albumId, albumData);
			
			return new ResponseEntity<String>(albumId, HttpStatus.NO_CONTENT);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(JSONException | IllegalArgumentException e) {
    		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    	} catch(NotFoundException e) {
    		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    	} catch(Exception e) {
    		return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

    /**
     * DELETE GATEWAY/albums/{albumId}
     * @param albumId
     * @param token in Authorization header
     * @return ResponseEntity
     */
    @DeleteMapping(value = "/{albumId}")
    public ResponseEntity<String> deleteAlbum(@PathVariable (value = "albumId") String albumId, @RequestHeader("Authorization") String token) {
    	try {
    		validateToken(token);
    		
    		albumService.deleteAlbum(albumId);
    		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    	} catch(NotAuthorizedException e) {
    		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    	} catch(NotFoundException e) {
    		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
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
    
    /**
     * Iterates through the list of songs, gets their metadata from the songs-service and adds it to the json.
     * @param album
     * @param token
     * @return JSONObject album
     * @throws Exception
     */
    private JSONObject addSongMetadataToAlbum(JSONObject album, String token) throws Exception {
    	// prepare request
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", token);
    	HttpEntity<String> request = new HttpEntity<String>(headers);
    	
    	JSONArray newSongList = new JSONArray();
    	
    	// iterate through songlist and make api call for each
    	album.getJSONArray("songs").forEach(songId -> {
			String songUrl = discoveryClient.getNextServerFromEureka("SONGS-SERVICE", false).getHomePageUrl() + "/songs/" + (int) songId;
			JSONObject song = new JSONObject(restTemplate.exchange(songUrl, HttpMethod.GET, request, String.class).getBody());
			newSongList.put(song);
		});
    	
    	album.put("songs", newSongList);
		
		return album;
    }
    
    /**
     * Iterates through the albums and their list of songs, gets their metadata from the songs-service and adds it to the json.
     * @param albums
     * @param token
     * @return JSONArray albums
     * @throws Exception
     */
    private JSONArray addSongMetadataToAlbums(JSONArray albums, String token) throws Exception {
    	// prepare request
    	RestTemplate restTemplate = new RestTemplate();
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Authorization", token);
    	HttpEntity<String> request = new HttpEntity<String>(headers);
    	
    	// iterate through each album and add the new metadata before checking the next album
    	albums.forEach(album -> {
    		JSONArray newSongList = new JSONArray();
        	
    		// iterate through songlist and make api call for each
        	((JSONObject) album).getJSONArray("songs").forEach(songId -> {
    			String songUrl = discoveryClient.getNextServerFromEureka("SONGS-SERVICE", false).getHomePageUrl() + "/songs/" + (int) songId;
    			JSONObject song = new JSONObject(restTemplate.exchange(songUrl, HttpMethod.GET, request, String.class).getBody());
    			newSongList.put(song);
    		});
        	
        	((JSONObject) album).put("songs", newSongList);
    	});
		
		return albums;
    }

}
