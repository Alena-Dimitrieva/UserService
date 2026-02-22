package userservice.service;

import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.entity.AppUser;
import userservice.exception.UserNotFoundException;
import userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserEventPublisher publisher;

    public UserResponseDto create(UserRequestDto dto) {
        AppUser user = new AppUser();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());

        AppUser saved = repository.save(user);

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