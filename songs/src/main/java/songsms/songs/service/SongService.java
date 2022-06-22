package songsms.songs.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import songsms.songs.NotFoundException;
import songsms.songs.model.Song;
import songsms.songs.repository.SongRepository;

/**
 * SongService class implementing ISongService interface and allowing accessing of database.
 * @author Julian Knepel
 */
@Service
public class SongService implements ISongService {
	
	@Autowired
	private SongRepository repository;
	
	@Override
	public Song getSongById(int songId) throws NotFoundException {
		Song song = repository.findById(songId);
		
		if(song == null) throw new NotFoundException();
		else return song;
	}
	
	@Override
	public List<Song> getAll() {
		return (List<Song>) repository.findAll();
	}
	
	@Override
	public int postSong(String payload) throws JSONException, IllegalArgumentException {
		JSONObject songData = new JSONObject(payload);
		
		Song song = new Song();
		
		if(songData.getString("title").length() == 0) throw new IllegalArgumentException();
		
		song.setTitle(songData.getString("title"));
		song.setArtist(songData.getString("artist"));
		song.setLabel(songData.getString("label"));
		song.setReleased(songData.getInt("released"));
		
		repository.save(song);
		
		return song.getId();
	}
	
	@Override
	public void putSong(int songId, String payload) throws JSONException, IllegalArgumentException, NotFoundException {
		JSONObject songData = new JSONObject(payload);
		
		if(songData.getString("title").length() == 0 || songData.getInt("id") != songId) throw new IllegalArgumentException();
		
		Song song = getSongById(songId);
		
		song.setTitle(songData.getString("title"));
		song.setArtist(songData.getString("artist"));
		song.setLabel(songData.getString("label"));
		song.setReleased(songData.getInt("released"));
		
		repository.save(song);
	}
	
	@Override
	public void deleteSong(int songId) throws NotFoundException {
		Song song = getSongById(songId);		
		repository.delete(song);
	}
}
