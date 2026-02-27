package userservice.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import userservice.entity.AppUser;
import userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserHateoasEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AppUser savedUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        AppUser user = new AppUser();
        user.setName("Алена");
        user.setEmail("alena@example.com");
        user.setAge(28);
        savedUser = userRepository.save(user);
    }

    @Test
    void testUserLinksEndToEnd() throws Exception {
        mockMvc.perform(get("/users/{id}", savedUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href").value(endsWith("/users/" + savedUser.getId())))
                .andExpect(jsonPath("_links.update.href").value(endsWith("/users/" + savedUser.getId())))
                .andExpect(jsonPath("_links.delete.href").value(endsWith("/users/" + savedUser.getId())))
                .andExpect(jsonPath("_links.users.href").value(endsWith("/users")));
    }

    @Test
    void testAllUsersLinksEndToEnd() throws Exception {
        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.userResponseDtoList[0]._links.self.href")
                        .value(endsWith("/users/" + savedUser.getId())))
                .andExpect(jsonPath("_embedded.userResponseDtoList[0]._links.update.href")
                        .value(endsWith("/users/" + savedUser.getId())))
                .andExpect(jsonPath("_embedded.userResponseDtoList[0]._links.delete.href")
                        .value(endsWith("/users/" + savedUser.getId())));
    }
}
