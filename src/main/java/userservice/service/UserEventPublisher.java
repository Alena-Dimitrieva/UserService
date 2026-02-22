package userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import userservice.event.OperationType;
import userservice.event.UserEvent;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    private static final String TOPIC = "user-events";

    public void sendUserCreated(String email) {
        kafkaTemplate.send(TOPIC, new UserEvent(email, OperationType.CREATED));
    }

    public void sendUserDeleted(String email) {
        kafkaTemplate.send(TOPIC, new UserEvent(email, OperationType.DELETED));
    }
}