package songsms.albums.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import songsms.albums.NotFoundException;
import songsms.albums.model.Album;
import songsms.albums.repository.AlbumRepository;

/**
 * AlbumService class implementing IAlbumService interface and allowing accessing of database.
 * @author Julian Knepel
 */
@Service
public class AlbumService implements IAlbumService {
	
	@Autowired
	private AlbumRepository repository;
	
	@Override
	public Album getAlbumById(String albumId) throws NotFoundException {
		Optional<Album> album = repository.findById(albumId);
		
		if(album.isEmpty()) throw new NotFoundException();
		else return album.get();
	}
	
	@Override
	public List<Album> getAlbumsByName(String name) {
		return repository.findByName(name);
	}
	
	@Override
	public List<Album> getAll() {
		return (List<Album>) repository.findAll();
	}
	
	@Override
	public String postAlbum(EurekaClient discoveryClient, String token, String payload) throws JSONException, IllegalArgumentException {
		JSONObject albumData = new JSONObject(payload);
		
		// check that name is not empty and that cover and youtube link are in the correct form
		if(albumData.getString("name").length() == 0 || albumData.getString("artist").length() == 0) throw new IllegalArgumentException();
		if(!albumData.getString("cover").matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
			throw new IllegalArgumentException();
		if(!albumData.getString("youtube").matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+"))
			throw new IllegalArgumentException();
		
		List<Integer> songs = new ArrayList<Integer>();
		
		// make call to songs API and add song to list if song exists in songs database
		albumData.getJSONArray("songs").forEach(song -> {			
			try {
	    		HttpHeaders headers = new HttpHeaders();
		    	headers.set("Authorization", token);

		    	HttpEntity<String> request = new HttpEntity<String>(headers);
		    	
				String songAPI = discoveryClient.getNextServerFromEureka("SONGS-SERVICE", false).getHomePageUrl() + "/songs/" + song;
	    		new RestTemplate().exchange(songAPI, HttpMethod.GET, request, String.class);
	    		
	    		songs.add((Integer) song);
	    	} catch(Exception e) {
	    		throw new IllegalArgumentException();
	    	}
		});
		
		// create album
		Album album = new Album();
		
		album.setName(albumData.getString("name"));
		album.setArtist(albumData.getString("artist"));
		album.setCover(albumData.getString("cover"));
		album.setYoutube(albumData.getString("youtube"));
		album.setReleased(albumData.getInt("released"));
		album.setLabel(albumData.getString("label"));
		album.setGenre(albumData.getString("genre"));
		album.setSongs(songs);
		
		repository.save(album);
		
		return album.getId();
	}

	@Override
	public void putAlbum(EurekaClient discoveryClient, String token, String albumId, String payload) throws JSONException, IllegalArgumentException, NotFoundException {
		JSONObject albumData = new JSONObject(payload);
		
		// check that name is not empty and that cover and youtube link are in the correct form
		if(albumData.getString("name").length() == 0 || albumData.getString("artist").length() == 0) throw new IllegalArgumentException();
		if(!albumData.getString("cover").matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
			throw new IllegalArgumentException();
		if(!albumData.getString("youtube").matches("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+"))
			throw new IllegalArgumentException();
		
		// check if album exists
		Album album = getAlbumById(albumId);
		
		List<Integer> songs = new ArrayList<Integer>();
		
		// make call to songs API and add song to list if song exists in songs database
		albumData.getJSONArray("songs").forEach(song -> {			
	    	try {
	    		HttpHeaders headers = new HttpHeaders();
		    	headers.set("Authorization", token);

		    	HttpEntity<String> request = new HttpEntity<String>(headers);
		    	
				String songAPI = discoveryClient.getNextServerFromEureka("SONGS-SERVICE", false).getHomePageUrl() + "/songs/" + song;
	    		new RestTemplate().exchange(songAPI, HttpMethod.GET, request, String.class);
	    		
	    		songs.add((Integer) song);
	    	} catch(Exception e) {
	    		throw new IllegalArgumentException();
	    	}
		});
		
		// update album
		album.setName(albumData.getString("name"));
		album.setArtist(albumData.getString("artist"));
		album.setCover(albumData.getString("cover"));
		album.setYoutube(albumData.getString("youtube"));
		album.setReleased(albumData.getInt("released"));
		album.setLabel(albumData.getString("label"));
		album.setGenre(albumData.getString("genre"));
		album.setSongs(songs);
		
		repository.save(album);
	}

	@Override
	public void deleteAlbum(String albumId) throws NotFoundException {
		Album album = getAlbumById(albumId);
		repository.delete(album);
	}
}
