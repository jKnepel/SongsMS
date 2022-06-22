package songsms.songs.service;

import java.util.List;

import org.json.JSONException;

import songsms.songs.ForbiddenException;
import songsms.songs.NotFoundException;
import songsms.songs.model.Playlist;

/**
 * IPlaylistService interface preparing methods for interacting with the database.
 * @author Julian Knepel
 */
public interface IPlaylistService {

	/**
	 * Returns playlist with corresponding id if client is owner or playlist is public.
	 * @param userId
	 * @param playlistId
	 * @return playlist
	 * @throws NotFoundException when the playlist doesn't exist in the database
	 * @throws ForbiddenException when the client doesn't have the rights to access the playlist
	 */
	Playlist getPlaylistById(String userId, int playlistId) throws NotFoundException, ForbiddenException;

	/**
	 * Returns list of playlists by user if client is that user or the playlists are public.
	 * @param userId
	 * @param ownerId
	 * @return list of playlists
	 */
	List<Playlist> getPlaylistsByUser(String userId, String ownerId);

	/**
	 * Creates playlist in database.
	 * @param userId
	 * @param payload
	 * @return playlistId
	 * @throws JSONException when the payload is not in the correct format
	 * @throws IllegalArgumentException when the payload has empty or incorrectly formatted keys
	 */
	int postPlaylist(String userId, String payload) throws JSONException, IllegalArgumentException;

	/**
	 * Update playlist with the corresponding id in the database.
	 * @param userId
	 * @param playlistId
	 * @param payload
	 * @throws JSONException when the payload is not in the correct format
	 * @throws IllegalArgumentException when the payload has empty or incorrectly formatted keys
	 * @throws NotFoundException when the playlist doesn't exist in the database
	 * @throws ForbiddenException when the client doesn't have the rights to update the playlist
	 */
	void putPlaylist(String userId, int playlistId, String payload) throws JSONException, IllegalArgumentException, NotFoundException, ForbiddenException;

	/**
	 * Delete playlist in database
	 * @param userId
	 * @param playlistId
	 * @throws NotFoundException when the playlist doesn't exist in the database
	 * @throws ForbiddenException when the client deosnt't have the rights to delete the playlist
	 */
	void deletePlaylist(String userId, int playlistId) throws NotFoundException, ForbiddenException;

}
