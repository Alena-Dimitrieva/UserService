package dao;

import entity.AppUser;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("resource")
@Testcontainers
class UserDaoImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private static UserDaoImpl userDao;

    @BeforeAll
    static void setup() {
        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        props.setProperty("hibernate.connection.username", postgres.getUsername());
        props.setProperty("hibernate.connection.password", postgres.getPassword());
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        props.setProperty("hibernate.show_sql", "true");

        sessionFactory = new Configuration()
                .addProperties(props)
                .addAnnotatedClass(AppUser.class)
                .buildSessionFactory();

        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) sessionFactory.close();
    }

    @BeforeEach
    void cleanBefore() {
        userDao.findAll().forEach(u -> userDao.delete(u.getId()));
    }

    @Test
    void shouldCreateAndFindUser() {
        AppUser user = new AppUser("Alena", "alena@mail.com", 25);
        userDao.create(user);

        AppUser found = userDao.findById(user.getId());
        assertNotNull(found);
        assertEquals("Alena", found.getName());
    }

    @Test
    void shouldUpdateUser() {
        AppUser user = new AppUser("Alena", "alena@mail.com", 25);
        userDao.create(user);

        user.setName("Alena Updated");
        user.setAge(26);
        userDao.update(user);

        AppUser updated = userDao.findById(user.getId());
        assertNotNull(updated);
        assertEquals("Alena Updated", updated.getName());
        assertEquals(26, updated.getAge());
    }

    @Test
    void shouldDeleteUser() {
        AppUser user = new AppUser("Alena", "alena@mail.com", 25);
        userDao.create(user);

        userDao.delete(user.getId());
        AppUser deleted = userDao.findById(user.getId());
        assertNull(deleted, "Пользователь должен быть удален");
    }

    @Test
    void shouldFindAllUsers() {
        AppUser user1 = new AppUser("Alena", "alena@mail.com", 25);
        AppUser user2 = new AppUser("Ivan", "ivan@mail.com", 30);

        userDao.create(user1);
        userDao.create(user2);

        List<AppUser> allUsers = userDao.findAll();
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("Alena")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("Ivan")));
    }

    @Test
    void shouldHandleNonExistentUser() {
        AppUser user = userDao.findById(999L);
        assertNull(user, "Не должно быть пользователя с несуществующим id");
    }
}
