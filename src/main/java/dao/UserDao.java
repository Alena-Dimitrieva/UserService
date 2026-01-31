package dao;

import entity.AppUser;
import java.util.List;

/**
 * DAO для работы с пользователями
 */
public interface UserDao {

    void create(AppUser user);       // Создать пользователя

    AppUser findById(Long id);       // Найти пользователя по id

    List<AppUser> findAll();         // Получить всех пользователей

    void update(AppUser user);       // Обновить пользователя

    void delete(Long id);            // Удалить пользователя по id
}
