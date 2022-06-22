package songsms.songs.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * 
 * Playlist Entity Class which represents the playlists table.
 * @author Julian Knepel
 *
 */
@Entity
@Table(name = "playlists")
public class Playlist {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false) private String ownerId;
	@Column(nullable = false) private String name;
	@Column(nullable = false) private boolean isPrivate;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "playlist_song",
			joinColumns= {@JoinColumn( name = "playlist_id", referencedColumnName= "id")},
			inverseJoinColumns= {@JoinColumn( name = "song_id", referencedColumnName= "id")})
	private List<Song> songs = new ArrayList<Song>();
		
	public int getId() { return id; }
	public String getOwnerId() { return ownerId; }
	public String getName() { return name; }
	public boolean getIsPrivate() { return isPrivate; }
	public List<Song> getSongs() { return songs; }
	
	public void setId(int id) { this.id = id; }
	public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
	public void setName(String name) { this.name = name; }
	public void setIsPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
	public void setSongs(List<Song> songs) { this.songs = songs; }
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
	        sb.append("\"id\":").append(id).append(",");
	        sb.append("\"ownerId\":\"").append(ownerId).append("\",");
	        sb.append("\"name\":\"").append(name).append("\",");
	        sb.append("\"isPrivate\":").append(isPrivate).append(",");
	        sb.append("\"songs\":").append(songs.toString());
        sb.append("}");
        return sb.toString();
	}
	
}