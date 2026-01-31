package dao;

import entity.AppUser;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

import java.util.List;

/**
 * Реализация DAO для AppUser
 */
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public void create(AppUser user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            LOGGER.info("Пользователь создан: {}", user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.error("Ошибка при создании пользователя", e);
        }
    }

    @Override
    public AppUser findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(AppUser.class, id);
        } catch (Exception e) {
            LOGGER.error("Ошибка при поиске пользователя по id={}", id, e);
            return null;
        }
    }

    @Override
    public List<AppUser> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from AppUser", AppUser.class).list();
        } catch (Exception e) {
            LOGGER.error("Ошибка при получении всех пользователей", e);
            return List.of();
        }
    }

    @Override
    public void update(AppUser user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
            LOGGER.info("Пользователь обновлен: {}", user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.error("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            AppUser user = session.get(AppUser.class, id);
            if (user != null) {
                session.delete(user);
                LOGGER.info("Пользователь удален с id={}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            LOGGER.error("Ошибка при удалении пользователя", e);
        }
    }
}