package songsms.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User entity class which represents the users table.
 * @author Julian Knepel
 */
@Entity
@Table(name = "users")
public class User {
	
	@Id
	@Column(nullable = false)
	private String userId;
	
	@Column(nullable = false) private String password;
	@Column(nullable = false) private String firstName;
	@Column(nullable = false) private String lastName;
	@Column(nullable = true) private String token;
	
	public User() {}
	public User(String userId, String password, String firstName, String lastName) {
		this.userId = userId;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}
		
	public String getId() { return userId; }
	public String getPassword() { return password; }
	public String getFirstName() { return firstName; }
	public String getLastName() { return lastName; }
	public String getToken() { return token; }
	
	public void setId(String userId) { this.userId = userId; }
	public void setPassword(String password) { this.password = password; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public void setToken(String token) { this.token = token; }
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
	        sb.append("\"userId\":\"").append(userId).append("\",");
	        sb.append("\"password\":\"").append(password).append("\",");
	        sb.append("\"firstName\":\"").append(firstName).append("\",");
	        sb.append("\"lastName\":\"").append(lastName).append("\"");
        sb.append("}");
        return sb.toString();
	}
}
