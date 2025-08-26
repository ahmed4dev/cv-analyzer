package com.cvanalyzer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChartDataDto {
    private List<String> labels;
    private List<DatasetDto> datasets;
    private Map<String, Object> options;

    public ChartDataDto() {
        // Constructeur par défaut
    }
}