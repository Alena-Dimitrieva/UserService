package userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Ответ с информацией о пользователе")
public record UserResponseDto(
        @Schema(description = "ID пользователя", example = "1")
        Long id,

        @Schema(description = "Имя пользователя", example = "Alena")
        String name,

        @Schema(description = "Email пользователя", example = "alena@mail.com")
        String email,

        @Schema(description = "Возраст пользователя", example = "25")
        int age,

        @Schema(description = "Дата и время создания пользователя")
        LocalDateTime createdAt
) {}
