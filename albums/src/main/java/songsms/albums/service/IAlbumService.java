package songsms.albums.service;

import java.util.List;

import org.json.JSONException;

import com.netflix.discovery.EurekaClient;

import songsms.albums.NotFoundException;
import songsms.albums.model.Album;

/**
 * IAlbumService interface preparing methods for interacting with the database.
 * @author Julian Knepel
 */
public interface IAlbumService {

	/**
	 * Returns album with corresponding albumId in the database.
	 * @param albumId
	 * @return album
	 * @throws NotFoundException when the album doesn't exist
	 */
	Album getAlbumById(String albumId) throws NotFoundException;

	/**
	 * Returns a list of albums with the corresponding name in the database.
	 * @param name
	 * @return list of albums
	 */
	List<Album> getAlbumsByName(String name);

	/**
	 * Returns a list of all albums in the database.
	 * @return list of albums
	 */
	List<Album> getAll();

	/**
	 * Creates album in database.
	 * @param discoveryClient used for discovering endpoint instances
	 * @param token
	 * @param payload
	 * @return albumId
	 * @throws JSONException when the payload is not in the correct format
	 * @throws IllegalArgumentException when the payload has empty or incorrectly formatted keys
	 */
	String postAlbum(EurekaClient discoveryClient, String token, String payload) throws JSONException, IllegalArgumentException;

	/**
	 * Updates album with the corresponding id in the database.
	 * @param discoveryClient used for discovering endpoint instances
	 * @param token
	 * @parm albumId
	 * @param payload
	 * @return albumId
	 * @throws JSONException when the payload is not in the correct format
	 * @throws IllegalArgumentException when the payload has empty or incorrectly formatted keys
	 * @throws NotFoundException when the album doesn't exist
	 */
	void putAlbum(EurekaClient discoveryClient, String token, String albumId, String payload) throws JSONException, IllegalArgumentException, NotFoundException;

	/**
	 * Deletes album with the corresponding id in the database.
	 * @param albumId
	 * @throws NotFoundException when the album doesn't exist
	 */
	void deleteAlbum(String albumId) throws NotFoundException;

}
