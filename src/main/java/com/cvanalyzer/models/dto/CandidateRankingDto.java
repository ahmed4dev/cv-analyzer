package com.cvanalyzer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CandidateRankingDto {
    private Long candidateId;
    private String candidateName;
    private Double score;
    private Integer rank;
    private Double percentile;
    private List<String> strengths;
    private List<String> weaknesses;

    public CandidateRankingDto() {
        // Constructeur par défaut
    }
}