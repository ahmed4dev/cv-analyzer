package com.cvanalyzer.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "education")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Field(type = FieldType.Keyword, name = "name")
    private String name;

    @Field(type = FieldType.Double, name = "relevance")
    private Double relevance;

    @Field(type = FieldType.Keyword, name = "category")
    private String category; // "TECHNIQUE", "LANGUE", etc.

    private String level; // BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    private Integer yearsOfExperience;
}
