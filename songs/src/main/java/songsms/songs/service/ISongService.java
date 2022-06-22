package songsms.songs.service;

import java.util.List;

import org.json.JSONException;

import songsms.songs.NotFoundException;
import songsms.songs.model.Song;

/**
 * ISongService interface preparing methods for interacting with the database.
 * @author Julian Knepel
 */
public interface ISongService {

	/**
	 * Returns song with corresponding songId in the database.
	 * @param songId
	 * @return song
	 * @throws NotFoundException when the song doesn't exist in the database
	 */
	Song getSongById(int songId) throws NotFoundException;
	
	/**
	 * Returns a list with all songs in the database.
	 * @return list of songs
	 */
	List<Song> getAll();

	/**
	 * Creates song in database.
	 * @param payload
	 * @return songId
	 * @throws JSONException when the payload is not in the correct format
	 * @throws IllegalArgumentException when the payload has empty or incorrectly formatted keys
	 */
	int postSong(String payload) throws JSONException, IllegalArgumentException;

	/**
	 * Updates the song with the corresponding id in the database.
	 * @param songId
	 * @param payload
	 * @throws JSONException when the payload is not in the correct format
	 * @throws IllegalArgumentException when the payload has empty or incorrectly formatted keys
	 */
	void putSong(int songId, String payload) throws JSONException, IllegalArgumentException, NotFoundException;

	/**
	 * Deletes the song with the corresponding id.
	 * @param songId
	 * @throws NotFoundException when the song doesn't exist in the database
	 */
	void deleteSong(int songId) throws NotFoundException;

}
