package songsms.songs.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import songsms.songs.model.Song;

/**
 * SongRepository extending CrudRepository and implementing findSong methods.
 * @author Julian Knepel
 */
@Repository
public interface SongRepository extends CrudRepository<Song, String> {

	Song findById(int id);
	
}
