package com.pereira.iphoneavailabilitytracker.integration;

import com.pereira.iphoneavailabilitytracker.domain.Model;
import com.pereira.iphoneavailabilitytracker.domain.ModelAvailability;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AppleStoreClient {

    private static final String URL_TEMPLATE = "https://www.apple.com/shop/fulfillment-messages?pl=true&cppart={carrier}&parts.0={model}&location={location}";

    private final RestTemplate restTemplate;

    @Data
    static class AppleResponse {
        private Head head;
        private Body body;
    }

    @Data
    static class Head {
        private String status;
    }

    @Data
    static class Body {
        private Content content;
    }

    @Data
    static class Content {
        private PickupMessage pickupMessage;
    }

    @Data
    static class PickupMessage {
        private List<Store> stores;
    }

    @Data
    static class Store {
        private String storeName;
        private String city;
        private String state;
        private Map<String, PartAvailability> partsAvailability;
    }

    @Data
    static class PartAvailability {
        private static final String AVAILABLE = "available";

        private String pickupType;
        private String pickupDisplay;

        public boolean isAvailable() {
            return pickupDisplay.equals(AVAILABLE);
        }
    }

    public List<ModelAvailability> checkAvailability(Model model, String location) {
        Map<String, String> params = new HashMap<>();
        params.put("carrier", "UNLOCKED/US");
        params.put("model", model.getCode());
        params.put("location", location);

        AppleResponse response = restTemplate.getForObject(URL_TEMPLATE, AppleResponse.class, params);

        if (isResponseOk(response)) {
            List<Store> stores = response.getBody().getContent().getPickupMessage().getStores();

            return stores.stream()
                    .flatMap(store -> checkStoreAvailability(model, store).stream())
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private boolean isResponseOk(AppleResponse response) {
        return response != null && response.getHead().getStatus().equals("200");
    }

    private List<ModelAvailability> checkStoreAvailability(Model model, Store store) {
        String storeDescription = store.getStoreName() + "-" + store.getCity() + "-" + store.getState();

        Collection<PartAvailability> parts = store.getPartsAvailability().values();
        return parts.stream()
                .filter(PartAvailability::isAvailable)
                .map(partAvailability -> new ModelAvailability(model, storeDescription))
                .collect(Collectors.toList());
    }
}