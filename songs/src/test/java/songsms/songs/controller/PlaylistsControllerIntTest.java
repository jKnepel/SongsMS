package songsms.songs.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

/**
 * Integration testclass for the PlaylistsController.
 * @author Julian Knepel
 */
@SpringBootTest
@TestPropertySource(locations = "/test.properties")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableDiscoveryClient
@AutoConfigureMockMvc
public class PlaylistsControllerIntTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private EurekaClient discoveryClient;
	
	private String token;

	@BeforeAll
	public void setUp() throws Exception {
		String jsonUser = "{\"userId\":\"eschuler\",\"password\":\"pass1234\"}";
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> request = new HttpEntity<String>(jsonUser, headers);
	    	    
	    token = restTemplate.postForObject(discoveryClient.getNextServerFromEureka("AUTH-SERVICE", false).getHomePageUrl() + "/auth", request, String.class);
	}
	
	@Order(1)
	@Test
	void shouldGetPlaylistWithId() throws Exception {
		String jsonPlaylist = "{\"id\":1,\"ownerId\":\"eschuler\",\"name\":\"playlist 1\",\"isPrivate\":false,\"songs\":[{\"id\":1,\"title\":\"O'Children\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}, {\"id\":2,\"title\":\"The Lyre of Orpheus\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}]}";
		
		mockMvc.perform(get("/songs/playlists/1")
				.header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().string(jsonPlaylist));
	}
	
	@Order(2)
	@Test
	void failGetPlaylistWithId() throws Exception {
		mockMvc.perform(get("/songs/playlists/2")
				.header("Authorization", token))
			.andExpect(status().isForbidden());
	}
	
	@Order(3)
	@Test
	void shouldGetAllPlaylists() throws Exception {
		String jsonPlaylist = "[{\"id\":1,\"ownerId\":\"eschuler\",\"name\":\"playlist 1\",\"isPrivate\":false,\"songs\":[{\"id\":1,\"title\":\"O'Children\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}, {\"id\":2,\"title\":\"The Lyre of Orpheus\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}]}]";
		
		mockMvc.perform(get("/songs/playlists?userId=eschuler")
				.header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().json(jsonPlaylist));
	}
	
	@Order(4)
	@Test
	void failGetAllPlaylists() throws Exception {		
		mockMvc.perform(get("/songs/playlists?userId=eschuler")
				.header("Authorization", ""))
			.andExpect(status().isUnauthorized());
	}
	
	@Order(5)
	@Test
	void shouldPostPlaylist() throws Exception {
		String jsonPlaylist = "{\"name\":\"playlist 3\",\"isPrivate\":true,\"songs\":[1,2,3]}";
		
		mockMvc.perform(post("/songs/playlists")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonPlaylist)
				.header("Authorization", token))
			.andExpect(status().isCreated())
			.andExpect(content().string("3"));
	}
	
	@Order(6)
	@Test
	void failPostPlaylist() throws Exception {
		String jsonPlaylist = "{}";
		
		mockMvc.perform(post("/songs/playlists")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonPlaylist)
				.header("Authorization", token))
			.andExpect(status().isBadRequest());
	}
	
	@Order(7)
	@Test
	void shouldPutPlaylist() throws Exception {
		String jsonPlaylist = "{\"id\":3,\"name\":\"playlist 3\",\"isPrivate\":false,\"songs\":[1,2,3]}";
		
		mockMvc.perform(put("/songs/playlists/3")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonPlaylist)
				.header("Authorization", token))
			.andExpect(status().isNoContent());
	}
	
	@Order(8)
	@Test
	void failPutPlaylist() throws Exception {
		String jsonPlaylist = "{\"id\":4,\"name\":\"playlist 4\",\"isPrivate\":true,\"songs\":[1,2,3]}";
		
		mockMvc.perform(put("/songs/playlists/4")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonPlaylist)
				.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
	@Order(9)
	@Test
	void shouldDeleteSong() throws Exception {
		mockMvc.perform(delete("/songs/playlists/3")
					.header("Authorization", token))
			.andExpect(status().isNoContent());
	}
	
	@Order(10)
	@Test
	void failDeleteSong() throws Exception {
		mockMvc.perform(delete("/songs/playlists/4")
					.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
}