package com.pereira.iphoneavailabilitytracker.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelAvailability {

    private Model model;
    private String store;

}
