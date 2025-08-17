package com.cvanalyzer.cv_analyzer.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Embeddable
public class Skill {

    @Field(type = FieldType.Keyword, name = "name")
    private String name;

    @Field(type = FieldType.Double, name = "relevance")
    private Double relevance;

    @Field(type = FieldType.Keyword, name = "category")
    private String category; // "TECHNIQUE", "LANGUE", etc.
}
