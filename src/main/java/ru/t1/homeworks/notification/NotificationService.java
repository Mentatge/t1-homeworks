package ru.t1.homeworks.notification;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendStatusChangeEmail(Long taskId, String newStatus, String toEmail) {
        log.info("Отправка письма: taskId={}, newStatus={}, to={}", taskId, newStatus, toEmail);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toEmail);
        msg.setSubject("Изменён статус задачи #" + taskId);
        msg.setText("Новый статус: " + newStatus);
        mailSender.send(msg);
    }
}
