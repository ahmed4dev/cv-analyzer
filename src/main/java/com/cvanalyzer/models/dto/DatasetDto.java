package com.cvanalyzer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DatasetDto {
    private String label;
    private List<Double> data;
    private List<String> backgroundColor;
    private List<String> borderColor;
    private Integer borderWidth;

    public DatasetDto() {
        // Constructeur par défaut
    }
}