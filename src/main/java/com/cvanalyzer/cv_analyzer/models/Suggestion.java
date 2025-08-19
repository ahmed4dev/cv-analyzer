package com.cvanalyzer.cv_analyzer.models;

import com.cvanalyzer.cv_analyzer.models.enums.PriorityLevel;
import com.cvanalyzer.cv_analyzer.models.enums.SuggestionType;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {
    @Field(type = FieldType.Keyword, name = "type")
    private SuggestionType type; // Enum: CONTENT, STRUCTURE, KEYWORD

    @Field(type = FieldType.Text, name = "message")
    private String message;

    @Field(type = FieldType.Keyword, name = "priority")
    private PriorityLevel priority; // Enum: HIGH, MEDIUM, LOW
    
}
