package app;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.AppUser;

import java.util.List;
import java.util.Scanner;

/**
 * Консольное приложение для работы с пользователями
 */
public class Main {

    private static final UserDao USER_DAO = new UserDaoImpl();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    1 — Создать пользователя
                    2 — Показать всех пользователей
                    3 — Удалить пользователя
                    0 — Выход
                    """);

            int choice = scanner.nextInt();
            scanner.nextLine(); // очищаем буфер

            switch (choice) {
                case 1 -> {
                    System.out.print("Имя: ");
                    String name = scanner.nextLine();

                    System.out.print("Email: ");
                    String email = scanner.nextLine();

                    System.out.print("Возраст: ");
                    int age = scanner.nextInt();
                    scanner.nextLine();

                    USER_DAO.create(new AppUser(name, email, age));
                }

                case 2 -> {
                    List<AppUser> users = USER_DAO.findAll();
                    users.forEach(System.out::println);
                }

                case 3 -> {
                    System.out.print("Введите id: ");
                    Long id = scanner.nextLong();
                    scanner.nextLine();
                    USER_DAO.delete(id);
                }

                case 0 -> {
                    System.out.println("Завершение работы");
                    return;
                }

                default -> System.out.println("Неизвестная команда");
            }
        }
    }
}
