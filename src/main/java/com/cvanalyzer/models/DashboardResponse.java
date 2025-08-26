package com.cvanalyzer.models;

import com.cvanalyzer.models.dto.CandidateRankingDto;
import com.cvanalyzer.models.dto.ChartDataDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DashboardResponse {
    private Long jobId;
    private String jobTitle;
    private int totalCandidates;
    private double averageScore;
    private Map<String, Integer> scoreDistribution;
    private List<String> topMissingSkills;
    private List<CandidateRankingDto> topCandidates;
    private ChartDataDto chartData;

    public DashboardResponse() {
        // Constructeur par défaut
    }
}