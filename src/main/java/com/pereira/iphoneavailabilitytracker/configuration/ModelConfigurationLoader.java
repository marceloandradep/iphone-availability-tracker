package com.pereira.iphoneavailabilitytracker.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.pereira.iphoneavailabilitytracker.domain.Model;
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
public class ModelConfigurationLoader {

    private final ObjectMapper objectMapper;

    @Value("classpath:configuration/models.json")
    private Resource configurationFile;

    @Getter
    private List<Model> models;

    @PostConstruct
    public void load() throws IOException {
        CollectionType javaType =
                objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, Model.class);

        this.models = objectMapper.readValue(configurationFile.getInputStream(), javaType);
    }

}
