package com.cvanalyzer.cv_analyzer.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Table(name = "analysis_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cv_id")
    private CV cv;

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @ElementCollection
    @CollectionTable(name = "section_scores", joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "section_name")
    @Column(name = "score")
    private Map<String, Double> sectionScores;
}