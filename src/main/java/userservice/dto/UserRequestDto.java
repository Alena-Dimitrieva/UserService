package userservice.dto;

import jakarta.validation.constraints.*;

public record UserRequestDto(
        @NotBlank(message = "name не должно быть пустым") String name,
        @Email(message = "email должен быть корректным") String email,
        @Min(value = 1, message = "age должно быть не менее 1")
        @Max(value = 120, message = "age должно быть не более 120") int age
) {}