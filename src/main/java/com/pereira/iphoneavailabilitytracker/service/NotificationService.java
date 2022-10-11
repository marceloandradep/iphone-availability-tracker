package com.pereira.iphoneavailabilitytracker.service;

import com.pereira.iphoneavailabilitytracker.configuration.NotificationConfigurationLoader;
import com.pereira.iphoneavailabilitytracker.domain.ModelAvailability;
import com.pereira.iphoneavailabilitytracker.notification.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationConfigurationLoader notificationConfigurationLoader;
    private final Map<String, NotificationSender> senders;

    private final Map<String, ModelAvailability> alreadySent;

    public void sendNotification(List<ModelAvailability> availability) {
        List<ModelAvailability> filteredAvailability =
                availability.stream()
                        .filter(modelAvailability -> !alreadySent.containsKey(modelAvailability.toString()))
                        .toList();

        log.info("Sending notification for: [{}]", filteredAvailability);

        notificationConfigurationLoader.getConfigurations()
                .forEach(notificationConfiguration -> {
                    NotificationSender sender = senders.get(notificationConfiguration.getType());
                    sender.sendNotification(notificationConfiguration.getDestination(), filteredAvailability);
                });

        filteredAvailability.forEach(modelAvailability -> alreadySent.put(modelAvailability.toString(), modelAvailability));
    }

}
