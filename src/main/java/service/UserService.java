package service;

import dao.UserDao;
import entity.AppUser;

import java.util.List;

public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public AppUser createUser(String name, String email, int age) {
        validateUserInput(name, email, age);
        AppUser user = new AppUser(name, email, age);
        userDao.create(user);
        return user;
    }

    public AppUser getUserById(Long id) {
        AppUser user = userDao.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        return user;
    }

    public List<AppUser> getAllUsers() {
        return userDao.findAll();
    }

    public void updateUser(Long id, String name, String email, int age) {
        validateUserInput(name, email, age);
        AppUser existingUser = getUserById(id);
        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setAge(age);
        userDao.update(existingUser);
    }

    public void deleteUser(Long id) {
        AppUser user = userDao.findById(id);
        if (user == null) {
            throw new RuntimeException("Cannot delete: User not found with id: " + id);
        }
        userDao.delete(id);
    }

    private void validateUserInput(String name, String email, int age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120");
        }
    }
}
