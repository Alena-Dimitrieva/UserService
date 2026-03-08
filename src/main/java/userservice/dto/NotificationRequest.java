package userservice.dto;

public record NotificationRequest(
        String email,
        String message
) {}
