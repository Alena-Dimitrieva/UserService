package userservice.service;

import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.entity.AppUser;
import userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repository;
    private UserEventPublisher publisher;
    private UserService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        publisher = mock(UserEventPublisher.class);
        service = new UserService(repository, publisher);
    }

    @Test
    void shouldCreateUser() {
        UserRequestDto dto = new UserRequestDto("Alena", "alena@mail.com", 25);
        AppUser savedUser = new AppUser();
        savedUser.setId(1L);
        savedUser.setName(dto.name());
        savedUser.setEmail(dto.email());
        savedUser.setAge(dto.age());
        savedUser.setCreatedAt(LocalDateTime.now());

        when(repository.save(any(AppUser.class))).thenReturn(savedUser);

        UserResponseDto response = service.create(dto);

        assertNotNull(response);
        assertEquals("Alena", response.name());
        assertEquals("alena@mail.com", response.email());
        assertEquals(25, response.age());

        // проверка что метод сохранения вызван с правильными данными
        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(repository).save(captor.capture());
        assertEquals("Alena", captor.getValue().getName());

        //  проверка, что событие отправлено
        verify(publisher).sendUserCreated("alena@mail.com");
    }

    @Test
    void shouldGetUserById() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setName("Alena");
        user.setEmail("alena@mail.com");
        user.setAge(25);
        user.setCreatedAt(LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto response = service.getById(1L);

        assertEquals("Alena", response.name());
        assertEquals("alena@mail.com", response.email());
    }

    @Test
    void shouldGetAllUsers() {
        AppUser user1 = new AppUser();
        user1.setId(1L);
        user1.setName("Alena");
        user1.setEmail("alena@mail.com");
        user1.setAge(25);
        user1.setCreatedAt(LocalDateTime.now());

        AppUser user2 = new AppUser();
        user2.setId(2L);
        user2.setName("Ivan");
        user2.setEmail("ivan@mail.com");
        user2.setAge(30);
        user2.setCreatedAt(LocalDateTime.now());

        when(repository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserResponseDto> all = service.getAll();

        assertEquals(2, all.size());
        assertEquals("Alena", all.get(0).name());
        assertEquals("Ivan", all.get(1).name());
    }

    @Test
    void shouldUpdateUser() {
        AppUser existing = new AppUser();
        existing.setId(1L);
        existing.setName("Alena");
        existing.setEmail("alena@mail.com");
        existing.setAge(25);
        existing.setCreatedAt(LocalDateTime.now());

        UserRequestDto dto = new UserRequestDto("Alena Updated", "alena@new.com", 26);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto updated = service.update(1L, dto);

        assertEquals("Alena Updated", updated.name());
        assertEquals("alena@new.com", updated.email());
        assertEquals(26, updated.age());

        verify(repository).save(existing);
    }

    @Test
    void shouldDeleteUserIfExists() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail("alena@mail.com");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(repository).deleteById(1L);
        //  проверка, что событие удаления отправлено
        verify(publisher).sendUserDeleted("alena@mail.com");
    }
}
