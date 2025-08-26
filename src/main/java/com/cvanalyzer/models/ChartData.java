package com.cvanalyzer.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "chart_data")
@Getter
@Setter
public class ChartData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_data_id")
    private DashboardData dashboardData;

    private String chartType; // bar, pie, line, radar, etc.
    private String title;

    @ElementCollection
    @CollectionTable(name = "chart_labels", joinColumns = @JoinColumn(name = "chart_id"))
    @Column(name = "label")
    private java.util.List<String> labels;

    @ElementCollection
    @CollectionTable(name = "chart_datasets", joinColumns = @JoinColumn(name = "chart_id"))
    @MapKeyColumn(name = "dataset_name")
    @Column(name = "dataset_value")
    private Map<String, java.util.List<Double>> datasets;

    @ElementCollection
    @CollectionTable(name = "chart_colors", joinColumns = @JoinColumn(name = "chart_id"))
    @MapKeyColumn(name = "color_key")
    @Column(name = "color_value")
    private Map<String, String> colors;

    @Column(length = 2000)
    private String options; // JSON string for chart configuration

    private Integer width;
    private Integer height;

    public ChartData() {
        // Constructeur par défaut
    }

    public ChartData(String chartType, String title) {
        this.chartType = chartType;
        this.title = title;
    }
}