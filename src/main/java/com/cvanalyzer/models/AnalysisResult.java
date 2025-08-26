package com.cvanalyzer.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Field(type = FieldType.Double, name = "match_score")
    private Double matchScore;

    @ElementCollection
    @CollectionTable(
            name = "analysis_result_skills",
            joinColumns = @JoinColumn(name = "analysis_result_id")
    )
    private List<Skill> extractedSkills;

    @ElementCollection
    @CollectionTable(name = "suggestions", joinColumns = @JoinColumn(name = "analysis_result_id"))
    @Field(type = FieldType.Nested, name = "suggestions")
    private List<Suggestion> suggestions;

    @Field(type = FieldType.Text, name = "summary")
    private String summary;

    @Field(type = FieldType.Object, name = "cv")
    @OneToOne
    @JoinColumn(name = "cv_id")
    private CV cv;

    // AJOUTS NÉCESSAIRES
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_description_id")
    private JobDescription jobDescription;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "experience_years")
    private Double experienceYears;

    @Column(name = "integrity_score")
    private Double integrityScore;

    @Column(name = "analysis_date")
    private LocalDate analysisDate;

    @Column(name = "compatibility_score")
    private Double compatibilityScore;

    @Column(name = "recommendations", length = 5000)
    private String recommendations;

    @ElementCollection
    @CollectionTable(
            name = "analysis_result_inconsistencies",
            joinColumns = @JoinColumn(name = "analysis_result_id")
    )
    @Column(name = "inconsistency", length = 255)
    private List<String> inconsistencies;


    // Méthodes utilitaires pour accéder aux compétences sous forme de String
    public List<String> getSkills() {
        if (extractedSkills == null) {
            return List.of();
        }
        return extractedSkills.stream()
                .map(Skill::getName) // Supposant que Skill a une méthode getName()
                .collect(Collectors.toList());
    }

    // Getter pour le score global (alias de matchScore)
    public Double getOverallScore() {
        return overallScore != null ? overallScore : matchScore;
    }
}