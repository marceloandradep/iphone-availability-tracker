package com.pereira.iphoneavailabilitytracker.notification;

import com.pereira.iphoneavailabilitytracker.domain.ModelAvailability;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("email")
@RequiredArgsConstructor
public class EmailNotification implements NotificationSender {

    private static final String DEFAULT_FROM = "noreply@pereira.com";
    private static final String DEFAULT_SUBJECT = "iPhone Availability";

    private final JavaMailSender mailSender;

    @Override
    public void sendNotification(String destination, List<ModelAvailability> availability) {
        if (availability.isEmpty()) {
            return;
        }

        StringBuilder messageBody = createMessageBody(availability);
        SimpleMailMessage message = createMailMessage(destination, messageBody);
        mailSender.send(message);
    }

    private SimpleMailMessage createMailMessage(String destination, StringBuilder messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(DEFAULT_FROM);
        message.setSubject(DEFAULT_SUBJECT);
        message.setTo(destination);
        message.setText(messageBody.toString());

        return message;
    }

    private StringBuilder createMessageBody(List<ModelAvailability> availability) {
        StringBuilder messageBody = new StringBuilder();

        availability.forEach(modelAvailability ->
                messageBody
                        .append(modelAvailability.getModel().getDescription())
                        .append(" at ")
                        .append(modelAvailability.getStore())
                        .append("\n")
        );

        return messageBody;
    }
}
