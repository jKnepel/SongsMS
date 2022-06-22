package songsms.albums.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.netflix.discovery.EurekaClient;

import songsms.albums.model.Album;
import songsms.albums.repository.AlbumRepository;

/**
 * Integration testclass for the AlbumsController.
 * @author Julian Knepel
 */
@SpringBootTest
@Testcontainers
@TestPropertySource(locations = "/test.properties")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableDiscoveryClient
@AutoConfigureMockMvc
public class AlbumsControllerIntTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Container
    public static MongoDBContainer container = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
	
	@DynamicPropertySource
	public static void setProperties(DynamicPropertyRegistry registry) {
		container.start();
		registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
	}
	
	@Autowired
	private AlbumRepository repository;
	
	@Autowired
	private EurekaClient discoveryClient;
	
	private String token;

	@BeforeAll
	public void setUp() throws Exception {
		// get token from auth-service 
		String jsonUser = "{\"userId\":\"eschuler\",\"password\":\"pass1234\"}";
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> request = new HttpEntity<String>(jsonUser, headers);
	    	    
	    token = restTemplate.postForObject(discoveryClient.getNextServerFromEureka("AUTH-SERVICE", false).getHomePageUrl() + "/auth", request, String.class);
	    
	    // fill containertest database with an album
	    List<Integer> songs = new ArrayList<Integer>();
		songs.add(1);
		songs.add(2);
		
		Album album = new Album();
		album.setName("Abattoir Blues / The Lyre of Orpheus");
		album.setCover("https://upload.wikimedia.org/wikipedia/en/2/27/Abattoir_Blues%2BThe_Lyre_of_Orpheus.jpg");
		album.setYoutube("https://youtube.com/playlist?list=PLiN-7mukU_RFcY2qpW8jIssEp_7bUrxVM");
		album.setSongs(songs);
		
		repository.save(album);
	}

	@Order(1)
	@Test
	void shouldGetPlaylistWithId() throws Exception {
		String jsonAlbum = "{\"id\":1,\"name\":\"Abattoir Blues / The Lyre of Orpheus\",\"cover\":\"https://upload.wikimedia.org/wikipedia/en/2/27/Abattoir_Blues%2BThe_Lyre_of_Orpheus.jpg\",\"youtube\":\"https://youtube.com/playlist?list=PLiN-7mukU_RFcY2qpW8jIssEp_7bUrxVM\",\"songs\":[{\"id\":1,\"title\":\"O'Children\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}, {\"id\":2,\"title\":\"The Lyre of Orpheus\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}]}";
		
		mockMvc.perform(get("/albums/1")
				.header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().string(jsonAlbum));
	}
	
	@Order(2)
	@Test
	void failGetPlaylistWithId() throws Exception {
		mockMvc.perform(get("/albums/2")
				.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
	@Order(3)
	@Test
	void shouldGetAllPlaylists() throws Exception {
		String jsonAlbum = "[{\"id\":1,\"name\":\"Abattoir Blues / The Lyre of Orpheus\",\"cover\":\"https://upload.wikimedia.org/wikipedia/en/2/27/Abattoir_Blues%2BThe_Lyre_of_Orpheus.jpg\",\"youtube\\\":\"https://youtube.com/playlist?list=PLiN-7mukU_RFcY2qpW8jIssEp_7bUrxVM\",\"songs\":[{\"id\":1,\"title\":\"O'Children\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}, {\"id\":2,\"title\":\"The Lyre of Orpheus\",\"artist\":\"Nick Cave & The Bad Seeds\",\"label\":\"Mute\",\"released\":2004}]}]";
		
		mockMvc.perform(get("/albums")
				.header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(content().json(jsonAlbum));
	}
	
	@Order(4)
	@Test
	void failGetAllPlaylists() throws Exception {		
		mockMvc.perform(get("/albums")
				.header("Authorization", ""))
			.andExpect(status().isUnauthorized());
	}
	
	@Order(5)
	@Test
	void shouldPostPlaylist() throws Exception {
		String jsonAlbum = "{\"name\":\"Idiot Prayer\",\"cover\":\"https://media1.jpc.de/image/w600/front/0/5056167126249.jpg\",\"youtube\":\"https://youtube.com/playlist?list=PLOjIk8snVJPvlba_xy--4s9mqOI7Q8RGj\",\"songs\":[1,2,3]}";
		
		mockMvc.perform(post("/albums")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonAlbum)
				.header("Authorization", token))
			.andExpect(status().isCreated())
			.andExpect(content().string("2"));
	}
	
	@Order(6)
	@Test
	void failPostPlaylist() throws Exception {
		String jsonAlbum = "{}";
		
		mockMvc.perform(post("/albums")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonAlbum)
				.header("Authorization", token))
			.andExpect(status().isBadRequest());
	}
	
	@Order(7)
	@Test
	void shouldPutPlaylist() throws Exception {
		String jsonAlbum = "{\"name\":\"Idiot Prayer\",\"cover\":\"https://media1.jpc.de/image/w600/front/0/5056167126249.jpg\",\"youtube\":\"https://youtube.com/playlist?list=PLOjIk8snVJPvlba_xy--4s9mqOI7Q8RGj\",\"songs\":[1,2,3]}";
		
		mockMvc.perform(put("/albums/2")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonAlbum)
				.header("Authorization", token))
			.andExpect(status().isNoContent());
	}
	
	@Order(8)
	@Test
	void failPutPlaylist() throws Exception {
		String jsonAlbum = "{\"name\":\"Idiot Prayer\",\"cover\":\"https://media1.jpc.de/image/w600/front/0/5056167126249.jpg\",\"youtube\":\"https://youtube.com/playlist?list=PLOjIk8snVJPvlba_xy--4s9mqOI7Q8RGj\",\"songs\":[1,2,3]}";;
		
		mockMvc.perform(put("/albums/3")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonAlbum)
				.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
	@Order(9)
	@Test
	void shouldDeleteSong() throws Exception {
		mockMvc.perform(delete("/albums/1")
					.header("Authorization", token))
			.andExpect(status().isNoContent());
	}
	
	@Order(10)
	@Test
	void failDeleteSong() throws Exception {
		mockMvc.perform(delete("/albums/3")
					.header("Authorization", token))
			.andExpect(status().isNotFound());
	}
	
}