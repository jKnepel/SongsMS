package songsms.songs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * Song Entity Class which represents the songs table.
 * @author Julian Knepel
 *
 */
@Entity
@Table(name = "songs")
public class Song {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false) private String title;
	@Column private String artist;
	@Column private String label;
	@Column private int released;
		
	public int getId() { return id; }
	public String getTitle() { return title; }
	public String getArtist() { return artist; }
	public String getLabel() { return label; }
	public int getReleased() { return released; }
	
	public void setId(int id) { this.id = id; }
	public void setTitle(String title) { this.title = title; }
	public void setArtist(String artist) { this.artist = artist; }
	public void setLabel(String label) { this.label = label; }
	public void setReleased(int released) { this.released = released; }
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
	        sb.append("\"id\":").append(id).append(",");
	        sb.append("\"title\":\"").append(title).append("\",");
	        sb.append("\"artist\":\"").append(artist).append("\",");
	        sb.append("\"label\":\"").append(label).append("\",");
	        sb.append("\"released\":").append(released);
        sb.append("}");
        return sb.toString();
	}
}
