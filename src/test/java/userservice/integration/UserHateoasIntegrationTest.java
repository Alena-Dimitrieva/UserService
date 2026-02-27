package userservice.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import userservice.entity.AppUser;
import userservice.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserHateoasIntegrationTest {

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
    void testGetUserByIdHateoas() throws Exception {
        mockMvc.perform(get("/users/{id}", savedUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Алена"))
                // Проверяем ссылки HATEOAS
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.users.href").exists())
                .andExpect(jsonPath("_links.update.href").exists())
                .andExpect(jsonPath("_links.delete.href").exists());
    }

    @Test
    void testGetAllUsersHateoas() throws Exception {
        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.userResponseDtoList[0].id").value(savedUser.getId()))
                .andExpect(jsonPath("_embedded.userResponseDtoList[0].name").value("Алена"))
                .andExpect(jsonPath("_embedded.userResponseDtoList[0]._links.self.href").exists())
                .andExpect(jsonPath("_embedded.userResponseDtoList[0]._links.update.href").exists())
                .andExpect(jsonPath("_embedded.userResponseDtoList[0]._links.delete.href").exists());
    }
}