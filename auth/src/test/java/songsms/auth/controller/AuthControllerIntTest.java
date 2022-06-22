package songsms.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Integration testclass for the AuthController.
 * @author Julian Knepel
 */
@SpringBootTest
@TestPropertySource(locations = "/test.properties")
@AutoConfigureMockMvc
public class AuthControllerIntTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldGetAllUsers() throws Exception {
		mockMvc.perform(get("/auth"))
			.andExpect(status().isOk())
			.andExpect(content().json("[{\"userId\":\"eschuler\",\"password\":\"pass1234\",\"firstName\":\"Elena\",\"lastName\":\"Schuler\"}, {\"userId\":\"mmuser\",\"password\":\"pass1234\",\"firstName\":\"Maxime\",\"lastName\":\"Muster\"}]"));
	}
	
	@Test
	void shouldAuthenticateTestUser() throws Exception {
		String jsonUser = "{\"userId\":\"eschuler\",\"password\":\"pass1234\"}";
		
		mockMvc.perform(post("/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonUser))
			.andExpect(status().isOk());
	}
	
	@Test
	void failAuthenticateTestUser() throws Exception {
		mockMvc.perform(post("/auth")
			.contentType(MediaType.APPLICATION_JSON)
			.content(""))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldValidateUserToken() throws Exception {
		String jsonUser = "{\"userId\":\"eschuler\",\"password\":\"pass1234\"}";
		
		MvcResult result = mockMvc.perform(post("/auth")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonUser))
			.andExpect(status().isOk())
		.andReturn();
		
		mockMvc.perform(post("/auth")
				.header("Authorization", result.getResponse().getContentAsString()))
			.andExpect(status().isOk());
	}
	
	@Test
	void failValidateUserToken() throws Exception {
		mockMvc.perform(post("/auth")
				.header("Authorization", ""))
			.andExpect(status().isUnauthorized());
	}

}