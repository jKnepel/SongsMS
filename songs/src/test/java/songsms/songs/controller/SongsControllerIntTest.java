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
 * Integration testclass for the SongsController.
 * @author Julian Knepel
 */
@SpringBootTest
@TestPropertySource(locations = "/test.properties")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableDiscoveryClient
@AutoConfigureMockMvc
public class SongsControllerIntTest {
	
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
	void shouldGetSongWithId() throws Exception {
		String jsonSong = "{\"id\":1,\"title\":\"O'Children\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}";
		
		mockMvc.perform(get("/songs/1")
				.header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().json(jsonSong));
	}
	
	@Order(2)
	@Test
	void failGetSongWithId() throws Exception {
		mockMvc.perform(get("/songs/4")
				.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
	@Order(3)
	@Test
	void shouldGetAllSongs() throws Exception {
		String jsonSongs = "[{\"id\":1,\"title\":\"O'Children\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}, {\"id\":2,\"title\":\"The Lyre of Orpheus\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}, {\"id\":3,\"title\":\"Easy Money\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}]";
		
		mockMvc.perform(get("/songs")
				.header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().json(jsonSongs));
	}
	
	@Order(4)
	@Test
	void failGetAllSongs() throws Exception {		
		mockMvc.perform(get("/songs")
				.header("Authorization", ""))
			.andExpect(status().isUnauthorized());
	}
	
	@Order(5)
	@Test
	void shouldPostSong() throws Exception {
		String jsonSong = "{\"title\":\"Abattoir Blues\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}";
		
		mockMvc.perform(post("/songs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonSong)
				.header("Authorization", token))
			.andExpect(status().isCreated())
			.andExpect(content().string("4"));
	}
	
	@Order(6)
	@Test
	void failPostSong() throws Exception {
		String jsonSong = "{}";
		
		mockMvc.perform(post("/songs")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonSong)
				.header("Authorization", token))
			.andExpect(status().isBadRequest());
	}
	
	@Order(7)
	@Test
	void shouldPutSong() throws Exception {
		String jsonSong = "{\"id\":4,\"title\":\"Spell\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}";
		
		mockMvc.perform(put("/songs/4")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonSong)
				.header("Authorization", token))
			.andExpect(status().isNoContent());
	}
	
	@Order(8)
	@Test
	void failPutSong() throws Exception {
		String jsonSong = "{\"id\":5,\"title\":\"Spell\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}";
		
		mockMvc.perform(put("/songs/5")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonSong)
				.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
	@Order(9)
	@Test
	void shouldDeleteSong() throws Exception {
		mockMvc.perform(delete("/songs/4")
				.header("Authorization", token))
			.andExpect(status().isNoContent());
	}
	
	@Order(10)
	@Test
	void failDeleteSong() throws Exception {
		mockMvc.perform(delete("/songs/5")
				.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
}