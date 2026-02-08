package dao;

import entity.AppUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;

public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    // Конструктор для тестов (позволяет инъекцию SessionFactory)
    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(AppUser user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            LOGGER.info("Пользователь создан: {}", user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Ошибка при создании пользователя", e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public AppUser findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(AppUser.class, id);
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске пользователя по id={}", id, e);
            throw new RuntimeException("Failed to find user", e);
        }
    }

    @Override
    public List<AppUser> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from AppUser", AppUser.class).list();
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении всех пользователей", e);
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public void update(AppUser user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            LOGGER.info("Пользователь обновлен: {}", user);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Ошибка при обновлении пользователя", e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            AppUser user = session.get(AppUser.class, id);
            if (user != null) {
                session.remove(user);
                LOGGER.info("Пользователь удален с id={}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.error("Ошибка при удалении пользователя", e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}