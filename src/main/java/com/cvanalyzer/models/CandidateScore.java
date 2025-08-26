package com.cvanalyzer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateScore {
    private Long cvId;
    private Double overallScore;
    private Double compatibilityScore;
    private Double integrityScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> inconsistencies;
}