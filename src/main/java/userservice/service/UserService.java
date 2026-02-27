package userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.entity.AppUser;
import userservice.exception.UserNotFoundException;
import userservice.mapper.UserMapper;
import userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserResponseDto create(UserRequestDto dto) {
        AppUser user = mapper.toEntity(dto);
        AppUser saved = repository.save(user);
        return mapper.toResponseDto(saved);
    }

    public UserResponseDto getById(Long id) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapper.toResponseDto(user);
    }

    public List<UserResponseDto> getAll() {
        return mapper.toResponseDtoList(repository.findAll());
    }

    public UserResponseDto update(Long id, UserRequestDto dto) {
        AppUser user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());

        AppUser updated = repository.save(user);
        return mapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
    }
}