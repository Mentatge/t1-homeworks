package ru.t1.homeworks.broker.kafka.task;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.t1.homeworks.config.kafka.KafkaConfig;
import ru.t1.homeworks.notification.NotificationService;

@Service
@RequiredArgsConstructor
public class TaskStatusListener {

    private final NotificationService notificationService;

    @Value("${notification.recipient}")
    private String recipientEmail;

    @KafkaListener(
            topics = KafkaConfig.TOPIC,
            groupId = "task-notifier-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onStatusChange(@Header(KafkaHeaders.RECEIVED_KEY) Long taskId,
                               @Payload String newStatus) {
        notificationService.sendStatusChangeEmail(taskId, newStatus, recipientEmail);
    }
}
