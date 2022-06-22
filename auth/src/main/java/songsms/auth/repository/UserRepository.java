package songsms.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import songsms.auth.model.User;

/**
 * UserRepository extending CrudRepository and implementing findUser methods.
 * @author Julian Knepel
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
	
	User findByUserId(String userId);
	
	User findByToken(String token);
	
}
