package com.cvanalyzer.services;


import com.cvanalyzer.models.CV;
import com.cvanalyzer.models.JobDescription;
import com.cvanalyzer.models.CandidateScore;
import com.cvanalyzer.models.CandidateRanking;
import java.util.List;
import java.util.Map;

public interface ScoringRankingService {

    /**
     * Calcule le score global d'un candidat pour un poste spécifique
     */
    CandidateScore calculateOverallScore(CV cv, JobDescription jobDescription);

    /**
     * Classe une liste de CVs pour un poste spécifique
     */
    List<CandidateRanking> rankCandidates(List<CV> cvs, JobDescription jobDescription);

    /**
     * Classe les candidats avec des options personnalisées
     */
    List<CandidateRanking> rankCandidatesWithOptions(List<CV> cvs, JobDescription jobDescription,
                                                     Map<String, Double> weightFactors);


    /**
     * Filtre les candidats basé sur un score minimum
     */
    List<CandidateRanking> filterCandidatesByScore(List<CandidateRanking> rankings, Double minScore);

    /**
     * Génère un rapport de classement détaillé
     */
    Map<String, Object> generateRankingReport(List<CandidateRanking> rankings, JobDescription jobDescription);

    /**
     * Calcule les statistiques de distribution des scores
     */

    Map<Long, Double> calculatePercentiles(List<CandidateScore> scores);
    Map<String, Object> calculateScoreStatistics(List<CandidateRanking> rankings);

    /**
     * Identifie les top N candidats
     */
    List<CandidateRanking> getTopCandidates(List<CandidateRanking> rankings, int topN);

    /**
     * Normalise les scores entre 0 et 100
     */
    List<CandidateRanking> normalizeScores(List<CandidateRanking> rankings);

    /**
     * Applique des ajustements de score basés sur des facteurs supplémentaires
     */
    CandidateScore applyScoreAdjustments(CandidateScore originalScore, Map<String, Double> adjustmentFactors);

    /**
     * Vérifie si un candidat répond aux exigences minimales
     */
    boolean meetsMinimumRequirements(CandidateScore score, JobDescription jobDescription);

    /**
     * Calcule le score de compatibilité détaillé
     */
    Map<String, Double> calculateDetailedCompatibility(CV cv, JobDescription jobDescription);
}