package songsms.songs.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import songsms.songs.model.Playlist;

/**
 * PlaylistRepository extending CrudRepository and implementing findPlaylist methods.
 * @author Julian Knepel
 */
@Repository
public interface PlaylistRepository extends CrudRepository<Playlist, String> {

	public Playlist findById(int id);
	
	public List<Playlist> findByOwnerId(String ownerId);
	
}
