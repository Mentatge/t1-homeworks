package ru.t1.homeworks.broker.kafka.task;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.t1.homeworks.notification.NotificationService;

@Service
@RequiredArgsConstructor
public class TaskStatusListener {

    private final NotificationService notificationService;

    @Value("${notification.recipient}")
    private String recipientEmail;

    @KafkaListener(
            topics = "${kafka.topics.task-status}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onStatusChange(
            @Header(KafkaHeaders.RECEIVED_KEY) Long taskId,
            @Payload String newStatus,
            Acknowledgment ack ){
        notificationService.sendStatusChangeEmail(taskId, newStatus, recipientEmail);
        ack.acknowledge();
    }
}
