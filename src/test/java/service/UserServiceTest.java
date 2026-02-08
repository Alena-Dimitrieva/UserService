package service;

import dao.UserDao;
import entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void shouldCreateUser() {
        AppUser createdUser = userService.createUser("Alena", "alena@mail.com", 25);

        assertNotNull(createdUser);
        assertEquals("Alena", createdUser.getName());
        assertEquals("alena@mail.com", createdUser.getEmail());
        assertEquals(25, createdUser.getAge());

        // Проверяем, что метод DAO был вызван с правильным объектом
        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userDao, times(1)).create(captor.capture());
        assertEquals("Alena", captor.getValue().getName());
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("", "alena@mail.com", 25));
        assertEquals("Name cannot be empty", exception.getMessage()); // Проверяем текст ошибки
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Alena", "invalid-email", 25));
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidAge() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Alena", "alena@mail.com", 0));
        assertEquals("Age must be between 1 and 120", exception.getMessage());
    }

    @Test
    void shouldGetUserById() {
        AppUser user = new AppUser("Alena", "alena@mail.com", 25);
        when(userDao.findById(1L)).thenReturn(user);

        AppUser found = userService.getUserById(1L);
        assertEquals("Alena", found.getName());
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        when(userDao.findById(999L)).thenReturn(null);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserById(999L));
        assertTrue(exception.getMessage().contains("User not found")); // Сообщение на русском можно в сервисе поменять, если нужно
    }

    @Test
    void shouldGetAllUsers() {
        AppUser user1 = new AppUser("Alena", "alena@mail.com", 25);
        AppUser user2 = new AppUser("Ivan", "ivan@mail.com", 30);
        when(userDao.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<AppUser> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertEquals("Alena", users.get(0).getName());
        assertEquals("Ivan", users.get(1).getName());
    }

    @Test
    void shouldUpdateUser() {
        AppUser existing = new AppUser("Alena", "alena@mail.com", 25);

        when(userDao.findById(1L)).thenReturn(existing);

        userService.updateUser(1L, "Alena Updated", "alena@new.com", 26);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userDao).update(captor.capture());
        AppUser updated = captor.getValue();
        assertEquals("Alena Updated", updated.getName());
        assertEquals("alena@new.com", updated.getEmail());
        assertEquals(26, updated.getAge());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentUser() {
        when(userDao.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(999L));
        assertTrue(exception.getMessage().contains("Cannot delete"));
    }

    @Test
    void shouldDeleteUser() {
        AppUser existing = new AppUser("Alena", "alena@mail.com", 25);
        when(userDao.findById(1L)).thenReturn(existing);

        userService.deleteUser(1L);

        verify(userDao, times(1)).delete(1L);
    }
}