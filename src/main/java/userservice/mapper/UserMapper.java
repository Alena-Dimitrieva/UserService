package userservice.mapper;

import org.springframework.stereotype.Component;
import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.entity.AppUser;

import java.util.List;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(AppUser user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }

    public AppUser toEntity(UserRequestDto dto) {
        AppUser user = new AppUser();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        return user;
    }

    public List<UserResponseDto> toResponseDtoList(List<AppUser> users) {
        return users.stream()
                .map(this::toResponseDto)
                .toList();
    }
}
