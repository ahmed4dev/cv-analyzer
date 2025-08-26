package com.cvanalyzer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilityResult {
    private Long cvId;
    private Long jobDescriptionId;
    private Double overallScore;
    private Double skillMatchScore;
    private Double experienceScore;
    private Double qualificationScore;
    private Double educationScore;
    private List<String> strengths;
    private List<String> weaknesses;
}
