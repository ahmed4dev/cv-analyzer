/*

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
}*/
package com.cvanalyzer.cv_analyzer.models;

import com.cvanalyzer.cv_analyzer.models.Skill;
import com.cvanalyzer.cv_analyzer.models.Suggestion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
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

    //@Field(type = FieldType.Nested, name = "skills")
    @ElementCollection
    // -- relplace par   @ElementCollection @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "analysis_result_id") // Foreign key in Skill table
    private List<Skill> extractedSkills;

    @ElementCollection
    @CollectionTable(name = "suggestions", joinColumns = @JoinColumn(name = "analysis_result_id"))
    @Field(type = FieldType.Nested, name = "suggestions")
    private List<Suggestion> suggestions;

    @Field(type = FieldType.Text, name = "summary")
    private String summary;

    @Field(type = FieldType.Object, name = "cv")
    @OneToOne
    @JoinColumn(name = "cv_id") // nom de la colonne FK dans la table analysis_results
    private CV cv;
}
/*
import jakarta.annotation.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {
    private UUID id;
    private List<Skill> skills;
    private List<Suggestion> suggestions;
    private Double matchScore;

    @Data
    public static class Skill {
        private String name;
        private String category;
        private Double relevanceScore;
    }

    @Data
    public static class Suggestion {
        private String category; // "FORMATION", "EXPERIENCE", etc.
        private String message;
        private Priority priority; // HIGH, MEDIUM, LOW
    }
}*/
