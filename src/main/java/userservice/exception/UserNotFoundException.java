package userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Пользователь не найден"); // Сообщение теперь на русском
    }
}