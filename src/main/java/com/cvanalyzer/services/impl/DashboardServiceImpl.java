package com.cvanalyzer.services.impl;

import com.cvanalyzer.models.*;
import com.cvanalyzer.repositories.*;
import com.cvanalyzer.services.DashboardService;
import com.cvanalyzer.services.ScoringRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDataRepository dashboardDataRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final CVRepository cvRepository;
    private final ScoringRankingService scoringRankingService;

    @Override
    @Transactional
    public DashboardData generateDashboardData(Long jobDescriptionId) {
        JobDescription jobDescription = jobDescriptionRepository.findById(jobDescriptionId)
                .orElseThrow(() -> new RuntimeException("Job description not found"));

        return generateDashboardData(jobDescription);
    }

    @Override
    @Transactional
    public DashboardData generateDashboardData(JobDescription jobDescription) {
        // Désactiver le dashboard actuel s'il existe
        Optional<DashboardData> currentDashboard = dashboardDataRepository
                .findByJobDescriptionIdAndIsCurrent(jobDescription.getId(), true);
        currentDashboard.ifPresent(dashboard -> {
            dashboard.setIsCurrent(false);
            dashboardDataRepository.save(dashboard);
        });

        // Récupérer toutes les analyses pour ce job description
        List<AnalysisResult> analysisResults = analysisResultRepository
                .findByJobDescriptionId(jobDescription.getId());

        DashboardData dashboardData = new DashboardData();
        dashboardData.setJobDescription(jobDescription);
        dashboardData.setTotalCandidates(analysisResults.size());
        dashboardData.setAnalyzedCandidates(analysisResults.size()); // Tous analysés

        // Calculer les statistiques de score
        calculateScoreStatistics(dashboardData, analysisResults);

        // Calculer la distribution des scores
        calculateScoreDistribution(dashboardData, analysisResults);

        // Identifier les compétences manquantes
        identifyMissingSkills(dashboardData, analysisResults, jobDescription);

        // Identifier les meilleures compétences
        identifyTopSkills(dashboardData, analysisResults);

        // Calculer les statistiques d'expérience
        calculateExperienceStatistics(dashboardData, analysisResults);

        // Calculer les statistiques d'intégrité
        calculateIntegrityStatistics(dashboardData, analysisResults);

        // Générer des insights et recommandations
        generateInsightsAndRecommendations(dashboardData);

        dashboardData.setGeneratedAt(LocalDateTime.now());
        dashboardData.setIsCurrent(true);

        return dashboardDataRepository.save(dashboardData);
    }

    private void calculateScoreStatistics(DashboardData dashboard, List<AnalysisResult> results) {
        if (results.isEmpty()) return;

        List<Double> scores = results.stream()
                .map(AnalysisResult::getOverallScore)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!scores.isEmpty()) {
            dashboard.setAverageScore(scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            dashboard.setHighestScore(scores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
            dashboard.setLowestScore(scores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
            dashboard.setMedianScore(calculateMedian(scores));
        }
    }

    private void calculateScoreDistribution(DashboardData dashboard, List<AnalysisResult> results) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("0-20%", 0);
        distribution.put("21-40%", 0);
        distribution.put("41-60%", 0);
        distribution.put("61-80%", 0);
        distribution.put("81-100%", 0);

        results.stream()
                .map(AnalysisResult::getOverallScore)
                .filter(Objects::nonNull)
                .forEach(score -> {
                    if (score >= 0.8) distribution.put("81-100%", distribution.get("81-100%") + 1);
                    else if (score >= 0.6) distribution.put("61-80%", distribution.get("61-80%") + 1);
                    else if (score >= 0.4) distribution.put("41-60%", distribution.get("41-60%") + 1);
                    else if (score >= 0.2) distribution.put("21-40%", distribution.get("21-40%") + 1);
                    else distribution.put("0-20%", distribution.get("0-20%") + 1);
                });

        dashboard.setScoreDistribution(distribution);
    }

    private void identifyMissingSkills(DashboardData dashboard, List<AnalysisResult> results, JobDescription jobDescription) {
        // Supposons que JobDescription a une méthode getRequiredSkills()
        Set<String> requiredSkills = new HashSet<>();
        if (jobDescription.getRequiredSkills() != null) {
            requiredSkills = jobDescription.getRequiredSkills().keySet().stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }

        Map<String, Integer> missingSkillsCount = new HashMap<>();

        for (AnalysisResult result : results) {
            Set<String> candidateSkills = new HashSet<>();
            if (result.getSkills() != null) {
                candidateSkills = result.getSkills().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
            }

            for (String requiredSkill : requiredSkills) {
                if (!candidateSkills.contains(requiredSkill)) {
                    missingSkillsCount.put(requiredSkill, missingSkillsCount.getOrDefault(requiredSkill, 0) + 1);
                }
            }
        }

        // Trier par fréquence décroissante
        List<Map.Entry<String, Integer>> sortedMissingSkills = missingSkillsCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());

        Map<String, Integer> finalMissingSkills = sortedMissingSkills.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        dashboard.setMissingSkills(finalMissingSkills);
    }

    private void identifyTopSkills(DashboardData dashboard, List<AnalysisResult> results) {
        Map<String, Integer> skillFrequency = new HashMap<>();

        for (AnalysisResult result : results) {
            if (result.getSkills() != null) {
                for (String skill : result.getSkills()) {
                    skillFrequency.put(skill, skillFrequency.getOrDefault(skill, 0) + 1);
                }
            }
        }

        // Trier par fréquence décroissante et prendre les top 10
        List<Map.Entry<String, Integer>> topSkills = skillFrequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Integer> finalTopSkills = topSkills.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        dashboard.setTopSkills(finalTopSkills);
    }

    private void calculateExperienceStatistics(DashboardData dashboard, List<AnalysisResult> results) {
        if (results.isEmpty()) return;

        List<Double> experienceYears = results.stream()
                .map(AnalysisResult::getExperienceYears)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!experienceYears.isEmpty()) {
            dashboard.setAverageExperience(experienceYears.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            dashboard.setMaxExperience(experienceYears.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
            dashboard.setMinExperience(experienceYears.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
        }
    }

    private void calculateIntegrityStatistics(DashboardData dashboard, List<AnalysisResult> results) {
        if (results.isEmpty()) return;

        long totalCandidates = results.size();
        long suspiciousCandidates = results.stream()
                .filter(result -> result.getIntegrityScore() != null && result.getIntegrityScore() < 0.7)
                .count();

        long verifiedCandidates = results.stream()
                .filter(result -> result.getIntegrityScore() != null && result.getIntegrityScore() >= 0.9)
                .count();

        dashboard.setSuspiciousCandidates((int) suspiciousCandidates);
        dashboard.setVerifiedCandidates((int) verifiedCandidates);
        dashboard.setIntegrityRate(totalCandidates > 0 ? (double) verifiedCandidates / totalCandidates : 0.0);
    }

    private void generateInsightsAndRecommendations(DashboardData dashboard) {
        List<String> insights = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        // Insights basés sur les statistiques
        if (dashboard.getAverageScore() != null && dashboard.getAverageScore() < 0.5) {
            insights.add("Le score moyen des candidats est faible (" + String.format("%.2f", dashboard.getAverageScore() * 100) + "%)");
            recommendations.add("Envisagez d'élargir le pool de candidats ou de revoir les critères de sélection");
        }

        if (dashboard.getSuspiciousCandidates() != null && dashboard.getTotalCandidates() != null &&
                dashboard.getSuspiciousCandidates() > dashboard.getTotalCandidates() * 0.3) {
            insights.add("Taux élevé de CV suspects (" + dashboard.getSuspiciousCandidates() + " sur " + dashboard.getTotalCandidates() + ")");
            recommendations.add("Renforcez les vérifications d'intégrité des CV");
        }

        // Insights basés sur les compétences manquantes
        if (dashboard.getMissingSkills() != null && !dashboard.getMissingSkills().isEmpty()) {
            String topMissingSkill = dashboard.getMissingSkills().keySet().iterator().next();
            insights.add("Compétence fréquemment manquante: " + topMissingSkill);
            recommendations.add("Envisagez des formations ou élargissez les critères pour " + topMissingSkill);
        }

        dashboard.setInsights(insights);
        dashboard.setRecommendations(recommendations);
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

    @Override
    public Optional<DashboardData> getDashboardDataById(Long id) {
        return dashboardDataRepository.findById(id);
    }

    @Override
    public List<DashboardData> getDashboardDataByJobDescription(Long jobDescriptionId) {
        return dashboardDataRepository.findByJobDescriptionId(jobDescriptionId);
    }

    @Override
    public Optional<DashboardData> getCurrentDashboardData(Long jobDescriptionId) {
        return dashboardDataRepository.findByJobDescriptionIdAndIsCurrent(jobDescriptionId, true);
    }

    @Override
    public DashboardData updateDashboardData(Long id, DashboardData dashboardData) {
        DashboardData existing = dashboardDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dashboard data not found"));

        // Mettre à jour les champs modifiables
        if (dashboardData.getInsights() != null) {
            existing.setInsights(dashboardData.getInsights());
        }
        if (dashboardData.getRecommendations() != null) {
            existing.setRecommendations(dashboardData.getRecommendations());
        }
        if (dashboardData.getIsCurrent() != null) {
            existing.setIsCurrent(dashboardData.getIsCurrent());
        }

        return dashboardDataRepository.save(existing);
    }

    @Override
    public void deleteDashboardData(Long id) {
        dashboardDataRepository.deleteById(id);
    }

    @Override
    public List<DashboardData> getDashboardHistory(Long jobDescriptionId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return dashboardDataRepository.findByJobDescriptionIdAndGeneratedAtAfter(jobDescriptionId, since);
    }

    @Override
    public DashboardData generateComparativeDashboard(List<Long> jobDescriptionIds) {
        // Implémentation simplifiée - à compléter selon les besoins
        DashboardData comparativeDashboard = new DashboardData();
        comparativeDashboard.setGeneratedAt(LocalDateTime.now());
        comparativeDashboard.setIsCurrent(false);
        return comparativeDashboard;
    }

    @Override
    public DashboardData generateTrendAnalysis(Long jobDescriptionId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implémentation simplifiée - à compléter selon les besoins
        DashboardData trendAnalysis = new DashboardData();
        trendAnalysis.setGeneratedAt(LocalDateTime.now());
        trendAnalysis.setIsCurrent(false);
        return trendAnalysis;
    }

    @Override
    public DashboardData refreshDashboardData(Long jobDescriptionId) {
        return generateDashboardData(jobDescriptionId);
    }

    @Override
    public void archiveOldDashboardData(int daysThreshold) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysThreshold);
        List<DashboardData> oldDashboards = dashboardDataRepository.findByGeneratedAtBeforeAndIsCurrent(thresholdDate, false);
        dashboardDataRepository.deleteAll(oldDashboards);
    }

    @Override
    public List<DashboardData> searchDashboards(String keyword, LocalDateTime startDate, LocalDateTime endDate) {
        // Implémentation simplifiée - à compléter avec une recherche plus avancée
        return dashboardDataRepository.findByGeneratedAtBetween(startDate, endDate);
    }

    @Override
    public DashboardData generateSummaryDashboard(Long userId) {
        // Implémentation simplifiée - à compléter selon les besoins
        DashboardData summaryDashboard = new DashboardData();
        summaryDashboard.setGeneratedAt(LocalDateTime.now());
        summaryDashboard.setIsCurrent(false);
        return summaryDashboard;
    }
}