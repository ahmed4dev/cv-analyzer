package com.cvanalyzer.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Table(name = "comparison_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparisonResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private CV targetCV;

    @ManyToOne
    private CV referenceCV;

    private Double overallSimilarity;

    @ElementCollection
    @CollectionTable(name = "comparison_scores", joinColumns = @JoinColumn(name = "comparison_id"))
    @MapKeyColumn(name = "section_name")
    @Column(name = "similarity_score")
    private Map<String, Double> sectionSimilarities;

    @Column(columnDefinition = "TEXT")
    private String improvementSuggestions;
}
