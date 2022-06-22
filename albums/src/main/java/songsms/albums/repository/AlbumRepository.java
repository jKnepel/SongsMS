package songsms.albums.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import songsms.albums.model.Album;

/**
 * AlbumRepository extending MongoRepository and implementing findAlbum methods.
 * @author Julian Knepel
 */
@Repository
public interface AlbumRepository extends MongoRepository<Album, String> {
	
	List<Album> findByName(String name);
	
}
