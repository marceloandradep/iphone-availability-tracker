package com.pereira.iphoneavailabilitytracker.service;

import com.pereira.iphoneavailabilitytracker.configuration.ModelConfigurationLoader;
import com.pereira.iphoneavailabilitytracker.domain.Model;
import com.pereira.iphoneavailabilitytracker.domain.ModelAvailability;
import com.pereira.iphoneavailabilitytracker.integration.AppleStoreClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityTracker {

    private final ModelConfigurationLoader modelConfigurationLoader;
    private final AppleStoreClient appleStoreClient;
    private final NotificationService notificationService;

    @Value("${app.location}")
    private String location;

    @Scheduled(fixedRateString = "${app.check-rate}")
    public void track() {
        log.info("Checking availability at {}...", location);

        List<Model> models = modelConfigurationLoader.getModels();

        List<ModelAvailability> availabilityList =
                models.parallelStream()
                        .flatMap(model -> appleStoreClient.checkAvailability(model, location).stream())
                        .toList();

        log.info("Available models: [{}]", availabilityList);

        notificationService.sendNotification(availabilityList);
    }

}
