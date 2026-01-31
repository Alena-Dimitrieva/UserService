package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность пользователя.
 * Соответствует таблице app_user в базе данных.
 */
@Entity
@Table(name = "app_user") // вместо users
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private int age;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Обязательный конструктор для Hibernate
     */
    public AppUser() {
    }

    /**
     * Конструктор с параметрами для удобства создания
     */
    public AppUser(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = LocalDateTime.now(); // устанавливаем текущее время
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Форматированная строка для вывода объекта
     */
    @Override
    public String toString() {
        return String.format(
                "AppUser{id=%d, name='%s', email='%s', age=%d, createdAt=%s}",
                id, name, email, age, createdAt
        );
    }
}
