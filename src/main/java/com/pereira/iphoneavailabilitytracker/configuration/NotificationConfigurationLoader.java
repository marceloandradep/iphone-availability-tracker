package com.pereira.iphoneavailabilitytracker.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.pereira.iphoneavailabilitytracker.domain.NotificationConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationConfigurationLoader {

    private final ObjectMapper objectMapper;

    @Value("classpath:configuration/notifications.json")
    private Resource configurationFile;

    @Getter
    private List<NotificationConfiguration> configurations;

    @PostConstruct
    public void load() throws IOException {
        CollectionType javaType =
                objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, NotificationConfiguration.class);

        this.configurations = objectMapper.readValue(configurationFile.getInputStream(), javaType);
    }

}
