package userservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import userservice.client.NotificationClient;
import userservice.dto.NotificationRequest;
import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.entity.AppUser;
import userservice.exception.UserNotFoundException;
import userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserEventPublisher publisher;
    private final NotificationClient notificationClient; // Feign

    @CircuitBreaker(name = "notificationService", fallbackMethod = "notificationFallback")
    public void sendNotification(String email, String message) {
        NotificationRequest request = new NotificationRequest(email, message);
        notificationClient.sendNotification(request);
    }

    public void notificationFallback(String email, String message, Throwable t) {
        System.out.println("NotificationService недоступен. Фолбек сработал для email: " + email);
    }


    public UserResponseDto create(UserRequestDto dto) {
        AppUser user = new AppUser();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());

        AppUser saved = repository.save(user);

        sendNotification(saved.getEmail(), "Пользователь " + saved.getName() + " создан");

        publisher.sendUserCreated(saved.getEmail());

        return mapToDto(saved);
    }

    public UserResponseDto getById(Long id) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToDto(user);
    }

    public List<UserResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserResponseDto update(Long id, UserRequestDto dto) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());

        AppUser updated = repository.save(user);
        return mapToDto(updated);
    }

    public void delete(Long id) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        repository.deleteById(id);

        sendNotification(user.getEmail(), "Пользователь " + user.getName() + " удалён");

        publisher.sendUserDeleted(user.getEmail());
    }

    private UserResponseDto mapToDto(AppUser user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}