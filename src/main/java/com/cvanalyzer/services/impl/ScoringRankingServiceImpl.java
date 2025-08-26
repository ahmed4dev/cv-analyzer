package com.cvanalyzer.services.impl;

import com.cvanalyzer.models.*;
import com.cvanalyzer.models.CandidateScore;
import com.cvanalyzer.repositories.AnalysisResultRepository;
import com.cvanalyzer.services.ScoringRankingService;
import com.cvanalyzer.services.JobCompatibilityService;
import com.cvanalyzer.services.IntegrityCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoringRankingServiceImpl implements ScoringRankingService {

    private final JobCompatibilityService jobCompatibilityService;
    private final IntegrityCheckService integrityCheckService;
    private final AnalysisResultRepository analysisResultRepository;

    // Facteurs de pondération par défaut
    private static final Map<String, Double> DEFAULT_WEIGHT_FACTORS = Map.of(
            "compatibility", 0.7,
            "integrity", 0.2,
            "experience", 0.1
    );

    @Override
    @Transactional
    public CandidateScore calculateOverallScore(CV cv, JobDescription jobDescription) {
        log.info("Calculating overall score for CV: {} and Job: {}", cv.getId(), jobDescription.getId());

        try {
            // Calculer la compatibilité
            CompatibilityResult compatibility = jobCompatibilityService.analyzeCompatibility(cv.getId(), jobDescription);

            // Vérifier l'intégrité
            IntegrityCheckResult integrity = integrityCheckService.checkCVIntegrity(cv);

            // Calculer le score final avec pondération
            double overallScore = calculateWeightedScore(compatibility, integrity);

            // Appliquer les ajustements basés sur l'expérience
            double experienceModifier = calculateExperienceModifier(cv.getExperiences());
            overallScore *= experienceModifier;

            // S'assurer que le score est entre 0 et 1
            overallScore = Math.max(0, Math.min(1, overallScore));

            return new CandidateScore(
                    cv.getId(),
                    overallScore,
                    compatibility.getOverallScore(),
                    integrity.getIntegrityScore(),
                    compatibility.getStrengths(),
                    compatibility.getWeaknesses(),
                    integrity.getInconsistencies()
            );

        } catch (Exception e) {
            log.error("Error calculating score for CV: {}", cv.getId(), e);
            throw new RuntimeException("Failed to calculate candidate score", e);
        }
    }

    @Override
    @Transactional
    public List<CandidateRanking> rankCandidates(List<CV> cvs, JobDescription jobDescription) {
        return rankCandidatesWithOptions(cvs, jobDescription, DEFAULT_WEIGHT_FACTORS);
    }

    @Override
    @Transactional
    public List<CandidateRanking> rankCandidatesWithOptions(List<CV> cvs, JobDescription jobDescription,
                                                            Map<String, Double> weightFactors) {
        log.info("Ranking {} candidates for job: {}", cvs.size(), jobDescription.getId());

        if (cvs == null || cvs.isEmpty()) {
            return Collections.emptyList();
        }

        // Calculate scores for all candidates
        List<CandidateScore> scores = cvs.parallelStream()
                .map(cv -> calculateOverallScore(cv, jobDescription))
                .filter(Objects::nonNull) // Filter out null scores
                .collect(Collectors.toList());

        if (scores.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort by descending score
        List<CandidateScore> sortedScores = scores.stream()
                .sorted(Comparator.comparingDouble(CandidateScore::getOverallScore).reversed())
                .collect(Collectors.toList());

        // Calculate percentiles
        Map<Long, Double> percentiles = calculatePercentiles(sortedScores);

        // Create rankings
        List<CandidateRanking> rankings = new ArrayList<>();
        for (int i = 0; i < sortedScores.size(); i++) {
            CandidateScore score = sortedScores.get(i);
            // Find the corresponding CV
            CV cv = cvs.stream()
                    .filter(c -> c.getId().equals(score.getCvId()))
                    .findFirst()
                    .orElse(null);

            if (cv != null) {
                CandidateRanking ranking = new CandidateRanking(
                        cv,
                        jobDescription,
                        score,
                        i + 1,
                        percentiles.get(score.getCvId())
                );
                rankings.add(ranking);
            }
        }

        log.info("Completed ranking of {} candidates", rankings.size());
        return rankings;
    }


    // Add null safety to percentile calculation
    @Override
    public Map<Long, Double> calculatePercentiles(List<CandidateScore> scores) {
        int totalCandidates = scores.size();
        Map<Long, Double> percentiles = new HashMap<>();

        for (int i = 0; i < totalCandidates; i++) {
            CandidateScore score = scores.get(i);
            double percentile = (double) (totalCandidates - i) / totalCandidates * 100;
            percentiles.put(score.getCvId(), Math.round(percentile * 100.0) / 100.0);
        }

        return percentiles;
    }

    @Override
    public List<CandidateRanking> filterCandidatesByScore(List<CandidateRanking> rankings, Double minScore) {
        return rankings.stream()
                .filter(ranking -> ranking.getScore().getOverallScore() >= minScore)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> generateRankingReport(List<CandidateRanking> rankings, JobDescription jobDescription) {
        Map<String, Object> report = new LinkedHashMap<>();

        // Statistiques de base
        report.put("totalCandidates", rankings.size());
        report.put("jobTitle", jobDescription.getTitle());
        report.put("company", jobDescription.getCompanyName());

        // Statistiques de score
        Map<String, Object> stats = calculateScoreStatistics(rankings);
        report.put("statistics", stats);

        // Top 10 candidats
        List<Map<String, Object>> topCandidates = getTopCandidates(rankings, 10).stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
        report.put("topCandidates", topCandidates);

        // Distribution des scores
        report.put("scoreDistribution", calculateScoreDistribution(rankings));

        return report;
    }

    @Override
    public Map<String, Object> calculateScoreStatistics(List<CandidateRanking> rankings) {
        if (rankings.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Double> scores = rankings.stream()
                .map(ranking -> ranking.getScore().getOverallScore())
                .collect(Collectors.toList());

        double average = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double median = calculateMedian(scores);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("averageScore", Math.round(average * 100.0) / 100.0);
        stats.put("maxScore", Math.round(max * 100.0) / 100.0);
        stats.put("minScore", Math.round(min * 100.0) / 100.0);
        stats.put("medianScore", Math.round(median * 100.0) / 100.0);
        stats.put("totalCandidates", rankings.size());

        return stats;
    }

    @Override
    public List<CandidateRanking> getTopCandidates(List<CandidateRanking> rankings, int topN) {
        return rankings.stream()
                .limit(Math.min(topN, rankings.size()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateRanking> normalizeScores(List<CandidateRanking> rankings) {
        if (rankings.isEmpty()) {
            return Collections.emptyList();
        }

        // Trouver le score maximum pour la normalisation
        double maxScore = rankings.stream()
                .mapToDouble(ranking -> ranking.getScore().getOverallScore())
                .max()
                .orElse(1.0);

        // Normaliser les scores entre 0 et 100
        return rankings.stream()
                .map(ranking -> {
                    CandidateScore originalScore = ranking.getScore();
                    double normalizedScore = (originalScore.getOverallScore() / maxScore) * 100;

                    CandidateScore normalized = new CandidateScore(
                            originalScore.getCvId(),
                            normalizedScore,
                            originalScore.getCompatibilityScore(),
                            originalScore.getIntegrityScore(),
                            originalScore.getStrengths(),
                            originalScore.getWeaknesses(),
                            originalScore.getInconsistencies()
                    );

                    return new CandidateRanking(
                            normalized,
                            ranking.getRank(),
                            ranking.getPercentile()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public CandidateScore applyScoreAdjustments(CandidateScore originalScore, Map<String, Double> adjustmentFactors) {
        double adjustedScore = originalScore.getOverallScore();

        // Appliquer les ajustements
        for (Map.Entry<String, Double> adjustment : adjustmentFactors.entrySet()) {
            adjustedScore *= adjustment.getValue();
        }

        // S'assurer que le score reste entre 0 et 1
        adjustedScore = Math.max(0, Math.min(1, adjustedScore));

        return new CandidateScore(
                originalScore.getCvId(),
                adjustedScore,
                originalScore.getCompatibilityScore(),
                originalScore.getIntegrityScore(),
                originalScore.getStrengths(),
                originalScore.getWeaknesses(),
                originalScore.getInconsistencies()
        );
    }

    @Override
    public boolean meetsMinimumRequirements(CandidateScore score, JobDescription jobDescription) {
        // Vérifier le score minimum
        if (score.getOverallScore() < 0.4) {
            return false;
        }

        // Vérifier l'intégrité minimum
        if (score.getIntegrityScore() < 0.6) {
            return false;
        }

        // Vérifier l'expérience minimum
        // (Cette logique pourrait être plus complexe basée sur les exigences du poste)
        return true;
    }

    @Override
    public Map<String, Double> calculateDetailedCompatibility(CV cv, JobDescription jobDescription) {
        Map<String, Double> compatibilityDetails = new HashMap<>();

        // Calculer différents aspects de la compatibilité
        CompatibilityResult compatibility = jobCompatibilityService.analyzeCompatibility(cv.getId(), jobDescription);

        compatibilityDetails.put("overall", compatibility.getOverallScore());
        compatibilityDetails.put("skills", compatibility.getSkillMatchScore());
        compatibilityDetails.put("experience", compatibility.getExperienceScore());
        compatibilityDetails.put("qualifications", compatibility.getQualificationScore());

        return compatibilityDetails;
    }

    // Méthodes helper privées
    private double calculateWeightedScore(CompatibilityResult compatibility, IntegrityCheckResult integrity) {
        return (compatibility.getOverallScore() * DEFAULT_WEIGHT_FACTORS.get("compatibility")) +
                (integrity.getIntegrityScore() * DEFAULT_WEIGHT_FACTORS.get("integrity"));
    }

    private double calculateExperienceModifier(List<WorkExperience> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return 0.8; // Pénalité pour manque d'expérience
        }

        // Calculer l'expérience totale en années
        long totalMonths = experiences.stream()
                .mapToLong(exp -> java.time.temporal.ChronoUnit.MONTHS.between(
                        exp.getStartDate(),
                        exp.getEndDate() != null ? exp.getEndDate() : java.time.LocalDate.now()
                ))
                .sum();

        double totalYears = totalMonths / 12.0;

        // Modificateur basé sur l'expérience
        if (totalYears < 1) return 0.7;
        if (totalYears < 3) return 0.8;
        if (totalYears < 5) return 0.9;
        if (totalYears < 10) return 1.0;
        return 1.1; // Bonus pour expérience significative
    }

    private double calculateMedian(List<Double> scores) {
        List<Double> sortedScores = new ArrayList<>(scores);
        Collections.sort(sortedScores);

        int size = sortedScores.size();
        if (size % 2 == 0) {
            return (sortedScores.get(size/2 - 1) + sortedScores.get(size/2)) / 2.0;
        } else {
            return sortedScores.get(size/2);
        }
    }

    private CandidateRanking createCandidateRanking(CandidateScore score, int rank, Map<Long, Double> percentiles) {
        return new CandidateRanking(
                score,
                rank,
                percentiles.get(score.getCvId())
        );
    }

    private Map<String, Integer> calculateScoreDistribution(List<CandidateRanking> rankings) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("0-20%", 0);
        distribution.put("21-40%", 0);
        distribution.put("41-60%", 0);
        distribution.put("61-80%", 0);
        distribution.put("81-100%", 0);

        rankings.forEach(ranking -> {
            double score = ranking.getScore().getOverallScore();
            if (score >= 0.8) distribution.put("81-100%", distribution.get("81-100%") + 1);
            else if (score >= 0.6) distribution.put("61-80%", distribution.get("61-80%") + 1);
            else if (score >= 0.4) distribution.put("41-60%", distribution.get("41-60%") + 1);
            else if (score >= 0.2) distribution.put("21-40%", distribution.get("21-40%") + 1);
            else distribution.put("0-20%", distribution.get("0-20%") + 1);
        });

        return distribution;
    }

    private Map<String, Object> convertToMap(CandidateRanking ranking) {
        Map<String, Object> candidateMap = new LinkedHashMap<>();
        candidateMap.put("rank", ranking.getRank());
        candidateMap.put("score", Math.round(ranking.getScore().getOverallScore() * 100.0) / 100.0);
        candidateMap.put("percentile", ranking.getPercentile());
        candidateMap.put("cvId", ranking.getScore().getCvId());
        candidateMap.put("strengths", ranking.getScore().getStrengths());
        candidateMap.put("weaknesses", ranking.getScore().getWeaknesses());
        return candidateMap;
    }
}