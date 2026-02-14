package userservice.apitest;

import com.fasterxml.jackson.databind.ObjectMapper;
import userservice.controller.UserController;
import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.exception.UserNotFoundException;
import userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponseDto sampleUser;

    @BeforeEach
    void setup() {
        sampleUser = new UserResponseDto(
                1L,
                "Алена",
                "alena@example.com",
                28,
                LocalDateTime.now()
        );
    }

    // Позитивные тесты
    @Test
    void testCreateUser() throws Exception {
        UserRequestDto request = new UserRequestDto("Алена", "alena@example.com", 28);
        given(userService.create(any(UserRequestDto.class))).willReturn(sampleUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // <- ожидание 201 вместо 200
                .andExpect(jsonPath("$.id").value(sampleUser.id()))
                .andExpect(jsonPath("$.name").value("Алена"))
                .andExpect(jsonPath("$.email").value("alena@example.com"))
                .andExpect(jsonPath("$.age").value(28));
    }

    @Test
    void testGetUserById() throws Exception {
        given(userService.getById(1L)).willReturn(sampleUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleUser.id()))
                .andExpect(jsonPath("$.name").value("Алена"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        given(userService.getAll()).willReturn(List.of(sampleUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleUser.id()))
                .andExpect(jsonPath("$[0].name").value("Алена"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserRequestDto updateRequest = new UserRequestDto("Алена Новая", "alena2@example.com", 30);
        UserResponseDto updatedUser = new UserResponseDto(1L, "Алена Новая", "alena2@example.com", 30, LocalDateTime.now());

        given(userService.update(eq(1L), any(UserRequestDto.class))).willReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Алена Новая"))
                .andExpect(jsonPath("$.email").value("alena2@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    // Негативные тесты
    @Test
    void testCreateUserWithEmptyName() throws Exception {
        UserRequestDto invalid = new UserRequestDto("", "valid@example.com", 25);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("name не должно быть пустым")));
    }

    @Test
    void testCreateUserWithInvalidEmail() throws Exception {
        UserRequestDto invalid = new UserRequestDto("Иван", "invalid-email", 25);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("email должен быть корректным")));
    }

    @Test
    void testCreateUserWithInvalidAge() throws Exception {
        UserRequestDto invalid = new UserRequestDto("Иван", "ivan@example.com", 150);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("age должно быть не более 120")));
    }

    @Test
    void testGetNonExistingUser() throws Exception {
        given(userService.getById(999L)).willThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Пользователь не найден")));
    }

    @Test
    void testUpdateNonExistingUser() throws Exception {
        UserRequestDto update = new UserRequestDto("Ghost", "ghost@example.com", 40);
        given(userService.update(eq(999L), any(UserRequestDto.class)))
                .willThrow(new UserNotFoundException(999L));

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Пользователь не найден")));
    }

    @Test
    void testDeleteNonExistingUser() throws Exception {
        // Для void методов используется doThrow
        doThrow(new UserNotFoundException(999L)).when(userService).delete(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Пользователь не найден")));
    }
}