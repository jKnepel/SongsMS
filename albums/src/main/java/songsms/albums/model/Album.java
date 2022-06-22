package songsms.albums.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * Album Class which represents the albums collection.
 * @author Julian Knepel
 *
 */
@Document(collection = "albums")
public class Album {
	
	@Id private String id;
	private String name;
	private String artist;
	private String cover;
	private String youtube;
	private int released;
	private String label;
	private String genre;
	private List<Integer> songs;
	
	public String getId() { return id; }
	public String getName() { return name; }
	public String getArtist() { return artist; }
	public String getCover() { return cover; }
	public String getYoutube() { return youtube; }
	public int getReleased() { return released; }
	public String getLabel() { return label; }
	public String getGenre() { return genre; }
	public List<Integer> getSongs() { return songs; }
	
	public void setName(String name) { this.name = name; }
	public void setArtist(String artist) { this.artist = artist; }
	public void setCover(String cover) { this.cover = cover; }
	public void setYoutube(String youtube) { this.youtube = youtube; }
	public void setReleased(int released) { this.released = released; }
	public void setLabel(String label) { this.label = label; }
	public void setGenre(String genre) { this.genre = genre; }
	public void setSongs(List<Integer> songs) { this.songs = songs; }
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
	        sb.append("\"id\":\"").append(id).append("\",");
	        sb.append("\"name\":\"").append(name).append("\",");
	        sb.append("\"artist\":\"").append(artist).append("\",");
	        sb.append("\"cover\":\"").append(cover).append("\",");
	        sb.append("\"youtube\":\"").append(youtube).append("\",");
	        sb.append("\"released\":").append(released).append(",");
	        sb.append("\"label\":\"").append(label).append("\",");
	        sb.append("\"genre\":\"").append(genre).append("\",");
	        sb.append("\"songs\":").append(songs.toString());
        sb.append("}");
        return sb.toString();
	}
}