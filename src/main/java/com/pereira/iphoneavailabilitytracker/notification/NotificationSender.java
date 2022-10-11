package com.pereira.iphoneavailabilitytracker.notification;

import com.pereira.iphoneavailabilitytracker.domain.ModelAvailability;

import java.util.List;

public interface NotificationSender {

    void sendNotification(String destination, List<ModelAvailability> availability);

}
