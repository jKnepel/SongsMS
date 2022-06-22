package songsms.songs.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import songsms.songs.ForbiddenException;
import songsms.songs.NotFoundException;
import songsms.songs.model.Playlist;
import songsms.songs.model.Song;
import songsms.songs.repository.PlaylistRepository;
import songsms.songs.repository.SongRepository;

/**
 * PlaylistService class implementing IPlaylistService interface and allowing accessing of database.
 * @author Julian Knepel
 */
@Service
public class PlaylistService implements IPlaylistService {
	
	@Autowired
	private PlaylistRepository playlistRepository;
	
	@Autowired
	private SongRepository songRepository;
	
	@Override
	public Playlist getPlaylistById(String userId, int playlistId) throws NotFoundException, ForbiddenException {
		Playlist playlist = playlistRepository.findById(playlistId);
		
		if(playlist == null) throw new NotFoundException();
		if(!playlist.getOwnerId().equals(userId) && playlist.getIsPrivate()) throw new ForbiddenException();
		else  return playlist;
	}
	
	@Override
	public List<Playlist> getPlaylistsByUser(String userId, String ownerId) {
		List<Playlist> playlists = playlistRepository.findByOwnerId(ownerId);
		
		// remove playlists that are private if user is not owner
		if(!ownerId.equals(userId)) {
			for(int i = playlists.size() - 1; i >= 0; i--) {
				if(playlists.get(i).getIsPrivate()) playlists.remove(i); 
			}
		}
		
		return playlists;
	}
	
	@Override
	public int postPlaylist(String userId, String payload) throws JSONException, IllegalArgumentException {
		JSONObject playlistData = new JSONObject(payload);
		
		if(playlistData.getString("name").length() == 0) throw new IllegalArgumentException();
		
		List<Song> songs = new ArrayList<Song>();
		
		JSONArray jsonSongs = playlistData.getJSONArray("songs");
		for (int i = 0; i < jsonSongs.length(); i++) {
			Song queriedSong = songRepository.findById(jsonSongs.getInt(i));
			if(queriedSong == null) throw new IllegalArgumentException();
			
			songs.add(queriedSong);
		}
		
		Playlist playlist = new Playlist();
		
		playlist.setOwnerId(userId);
		playlist.setName(playlistData.getString("name"));
		playlist.setIsPrivate(playlistData.getBoolean("isPrivate"));
		playlist.setSongs(songs);
		
		playlistRepository.save(playlist);
		
		return playlist.getId();
	}
	
	@Override
	public void putPlaylist(String userId, int playlistId, String payload) throws JSONException, IllegalArgumentException, NotFoundException, ForbiddenException {
		JSONObject playlistData = new JSONObject(payload);
		
		if(playlistData.getString("name").length() == 0 || playlistData.getInt("id") != playlistId) throw new IllegalArgumentException();
		
		Playlist playlist = getPlaylistById(userId, playlistId);
		if(!playlist.getOwnerId().equals(userId)) throw new ForbiddenException();
		
		List<Song> songs = new ArrayList<Song>();
		
		JSONArray jsonSongs = playlistData.getJSONArray("songs");
		for (int i = 0; i < jsonSongs.length(); i++) {
			Song queriedSong = songRepository.findById(jsonSongs.getInt(i));
			if(queriedSong == null) throw new IllegalArgumentException();
			
			songs.add(queriedSong);
		}
		
		playlist.setName(playlistData.getString("name"));
		playlist.setIsPrivate(playlistData.getBoolean("isPrivate"));
		playlist.setSongs(songs);
		
		playlistRepository.save(playlist);
	}
	
	@Override
	public void deletePlaylist(String userId, int playlistId) throws NotFoundException, ForbiddenException {
		Playlist playlist = getPlaylistById(userId, playlistId);
		if(!playlist.getOwnerId().equals(userId)) throw new ForbiddenException();
		
		playlistRepository.delete(playlist);
	}
}
