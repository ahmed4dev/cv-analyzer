package com.cvanalyzer.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "dashboard_data")
@Getter
@Setter
public class DashboardData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_description_id")
    private JobDescription jobDescription;

    @Column(name = "total_candidates")
    private Integer totalCandidates;

    @Column(name = "analyzed_candidates")
    private Integer analyzedCandidates;

    @Column(name = "pending_candidates")
    private Integer pendingCandidates;

    @Column(name = "average_score")
    private Double averageScore;

    @Column(name = "highest_score")
    private Double highestScore;

    @Column(name = "lowest_score")
    private Double lowestScore;

    @Column(name = "median_score")
    private Double medianScore;

    @ElementCollection
    @CollectionTable(name = "dashboard_score_distribution",
            joinColumns = @JoinColumn(name = "dashboard_id"))
    @MapKeyColumn(name = "score_range")
    @Column(name = "candidate_count")
    private Map<String, Integer> scoreDistribution;

    @ElementCollection
    @CollectionTable(name = "dashboard_skill_gaps",
            joinColumns = @JoinColumn(name = "dashboard_id"))
    @MapKeyColumn(name = "skill_name")
    @Column(name = "missing_count")
    private Map<String, Integer> missingSkills;

    @ElementCollection
    @CollectionTable(name = "dashboard_top_skills",
            joinColumns = @JoinColumn(name = "dashboard_id"))
    @MapKeyColumn(name = "skill_name")
    @Column(name = "frequency_count") // Changé de proficiency_level à frequency_count
    private Map<String, Integer> topSkills; // Changé de Double à Integer

    // AJOUTS NÉCESSAIRES
    @Column(name = "average_experience")
    private Double averageExperience;

    @Column(name = "max_experience")
    private Double maxExperience;

    @Column(name = "min_experience")
    private Double minExperience;

    @Column(name = "suspicious_candidates")
    private Integer suspiciousCandidates;

    @Column(name = "verified_candidates")
    private Integer verifiedCandidates;

    @Column(name = "integrity_rate")
    private Double integrityRate;

    @ElementCollection
    @CollectionTable(name = "dashboard_insights",
            joinColumns = @JoinColumn(name = "dashboard_id"))
    @Column(name = "insight_text")
    private List<String> insights;

    @ElementCollection
    @CollectionTable(name = "dashboard_recommendations",
            joinColumns = @JoinColumn(name = "dashboard_id"))
    @Column(name = "recommendation_text")
    private List<String> recommendations;

    @Column(name = "avg_experience_years")
    private Double averageExperienceYears;

    @Column(name = "avg_education_level")
    private String averageEducationLevel;

    @Column(name = "integrity_violations_count")
    private Integer integrityViolationsCount;

    @Column(name = "avg_integrity_score")
    private Double averageIntegrityScore;

    @Column(name = "completion_rate")
    private Double completionRate;

    @Column(name = "analysis_duration_avg")
    private Double analysisDurationAverage;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "time_period")
    private String timePeriod;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    @OneToMany(mappedBy = "dashboardData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChartData> charts;

    @ElementCollection
    @CollectionTable(name = "dashboard_metrics",
            joinColumns = @JoinColumn(name = "dashboard_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, String> additionalMetrics;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @Column(name = "created_by")
    private Long createdBy;

    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
        if (isCurrent == null) {
            isCurrent = true;
        }
        calculateDerivedMetrics();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateDerivedMetrics();
    }

    private void calculateDerivedMetrics() {
        // Calculer le taux de complétion
        if (totalCandidates != null && totalCandidates > 0 && analyzedCandidates != null) {
            this.completionRate = (double) analyzedCandidates / totalCandidates * 100;
        }

        // Calculer les candidats en attente
        if (totalCandidates != null && analyzedCandidates != null) {
            this.pendingCandidates = totalCandidates - analyzedCandidates;
        }
    }

    // Méthodes utilitaires
    public boolean isComplete() {
        return analyzedCandidates != null && totalCandidates != null
                && analyzedCandidates.equals(totalCandidates);
    }

    public double getSuccessRate() {
        if (totalCandidates == null || totalCandidates == 0) return 0.0;
        return (double) analyzedCandidates / totalCandidates * 100;
    }

    public String getPerformanceCategory() {
        if (averageScore == null) return "UNKNOWN";
        if (averageScore >= 0.8) return "EXCELLENT";
        if (averageScore >= 0.6) return "GOOD";
        if (averageScore >= 0.4) return "AVERAGE";
        return "POOR";
    }

    public boolean hasIntegrityIssues() {
        return integrityViolationsCount != null && integrityViolationsCount > 0;
    }

    public boolean isRecent() {
        return generatedAt != null && generatedAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    public DashboardData() {
        // Constructeur par défaut
    }

    public DashboardData(JobDescription jobDescription, Integer totalCandidates,
                         Integer analyzedCandidates, Double averageScore) {
        this.jobDescription = jobDescription;
        this.totalCandidates = totalCandidates;
        this.analyzedCandidates = analyzedCandidates;
        this.averageScore = averageScore;
        this.generatedAt = LocalDateTime.now();
        this.isCurrent = true;
        calculateDerivedMetrics();
    }
}