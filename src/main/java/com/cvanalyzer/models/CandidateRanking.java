package com.cvanalyzer.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "candidate_rankings")
@Getter
@Setter
public class CandidateRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id")
    private AnalysisResult analysisResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id")
    private CV cv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_description_id")
    private JobDescription jobDescription;

    private Integer rankPosition;
    private Double Rank;
    private Double overallScore;
    private Double compatibilityScore;
    private Double integrityScore;
    private Double percentile;

    @ElementCollection
    @CollectionTable(name = "candidate_strengths", joinColumns = @JoinColumn(name = "ranking_id"))
    @Column(name = "strength")
    private List<String> strengths;

    @ElementCollection
    @CollectionTable(name = "candidate_weaknesses", joinColumns = @JoinColumn(name = "ranking_id"))
    @Column(name = "weakness")
    private List<String> weaknesses;

    @ElementCollection
    @CollectionTable(name = "candidate_inconsistencies", joinColumns = @JoinColumn(name = "ranking_id"))
    @Column(name = "inconsistency")
    private List<String> inconsistencies;

    @ElementCollection
    @CollectionTable(name = "candidate_recommendations", joinColumns = @JoinColumn(name = "ranking_id"))
    @Column(name = "recommendation", length = 1000)
    private List<String> recommendations;

    private Boolean isTopCandidate;
    private Boolean meetsMinimumRequirements;

    @Transient
    private CandidateScore candidateScore;

    public CandidateRanking(CandidateScore score, int rank, Double aDouble) {
    }

    public CandidateRanking(CandidateScore normalized, Double rank, Double percentile) {
    }

    @PrePersist
    protected void onPrePersist() {
        if (isTopCandidate == null) {
            isTopCandidate = rankPosition != null && rankPosition <= 10;
        }
        if (meetsMinimumRequirements == null) {
            meetsMinimumRequirements = overallScore != null && overallScore >= 0.6;
        }
    }

    public CandidateRanking() {
        // Default constructor
    }

    // Improved constructor
    public CandidateRanking(CV cv, JobDescription jobDescription, CandidateScore score,
                            Integer rank, Double percentile) {
        this.cv = cv;
        this.jobDescription = jobDescription;
        this.rankPosition = rank;
        this.overallScore = score.getOverallScore();
        this.compatibilityScore = score.getCompatibilityScore();
        this.integrityScore = score.getIntegrityScore();
        this.percentile = percentile;
        this.strengths = score.getStrengths();
        this.weaknesses = score.getWeaknesses();
        this.inconsistencies = score.getInconsistencies();
        this.candidateScore = score;

        onPrePersist();
    }

    public CandidateRanking(CV cv, JobDescription jobDescription, Integer rankPosition,
                            Double overallScore, Double compatibilityScore, Double integrityScore) {
        this.cv = cv;
        this.jobDescription = jobDescription;
        this.rankPosition = rankPosition;
        this.overallScore = overallScore;
        this.compatibilityScore = compatibilityScore;
        this.integrityScore = integrityScore;
        onPrePersist();
    }

    public Long getCvId() {
        return cv != null ? cv.getId() : null;
    }

    public Long getJobDescriptionId() {
        return jobDescription != null ? jobDescription.getId() : null;
    }

    public CandidateScore getScore() {
        if (candidateScore == null) {
            candidateScore = new CandidateScore(
                    getCvId(),
                    overallScore,
                    compatibilityScore,
                    integrityScore,
                    strengths,
                    weaknesses,
                    inconsistencies
            );
        }
        return candidateScore;
    }

    public void setScore(CandidateScore score) {
        this.candidateScore = score;
        this.overallScore = score.getOverallScore();
        this.compatibilityScore = score.getCompatibilityScore();
        this.integrityScore = score.getIntegrityScore();
        this.strengths = score.getStrengths();
        this.weaknesses = score.getWeaknesses();
        this.inconsistencies = score.getInconsistencies();
    }
}