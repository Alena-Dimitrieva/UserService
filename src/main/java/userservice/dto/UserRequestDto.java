package userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на создание или обновление пользователя")
public record UserRequestDto(
        @NotBlank(message = "name не должно быть пустым")
        @Schema(description = "Имя пользователя", example = "Alena")
        String name,

        @Email(message = "email должен быть корректным")
        @Schema(description = "Email пользователя", example = "alena@mail.com")
        String email,

        @Min(value = 1, message = "age должно быть не менее 1")
        @Max(value = 120, message = "age должно быть не более 120")
        @Schema(description = "Возраст пользователя", example = "25")
        int age
) {}