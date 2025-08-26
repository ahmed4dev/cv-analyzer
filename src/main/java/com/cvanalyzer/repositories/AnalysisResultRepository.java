package com.cvanalyzer.repositories;

import com.cvanalyzer.models.AnalysisResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    // Trouver les résultats d'analyse par ID de CV
    @Query("SELECT ar FROM AnalysisResult ar WHERE ar.cv.id = :cvId")
    List<AnalysisResult> findByCvId(Long cvId);

    // Trouver les résultats d'analyse par ID de description de poste
    List<AnalysisResult> findByJobDescriptionId(Long jobDescriptionId);

    // Trouver un résultat d'analyse spécifique par CV et JobDescription
    Optional<AnalysisResult> findByCvIdAndJobDescriptionId(Long cvId, Long jobDescriptionId);

    // Trouver les résultats avec un score minimum
    List<AnalysisResult> findByOverallScoreGreaterThanEqual(Double minScore);

    // Trouver les résultats avec un score maximum
    List<AnalysisResult> findByOverallScoreLessThanEqual(Double maxScore);

    // Trouver les résultats dans une plage de scores
    List<AnalysisResult> findByOverallScoreBetween(Double minScore, Double maxScore);

    // Trouver les résultats avec un score d'intégrité minimum
    List<AnalysisResult> findByIntegrityScoreGreaterThanEqual(Double minIntegrityScore);

    // Trouver les résultats avec des incohérences
    List<AnalysisResult> findByInconsistenciesIsNotEmpty();

    // Trouver les résultats sans incohérences
    List<AnalysisResult> findByInconsistenciesIsEmpty();

    // Trouver les résultats par date d'analyse
    List<AnalysisResult> findByAnalysisDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Trouver les résultats récents
    List<AnalysisResult> findByAnalysisDateAfter(LocalDateTime date);

    // Trouver les résultats les plus récents pour un CV
    @Query("SELECT a FROM AnalysisResult a WHERE a.cv.id = :cvId ORDER BY a.analysisDate DESC")
    List<AnalysisResult> findLatestByCvId(@Param("cvId") Long cvId, Pageable pageable);

    // Trouver les résultats les plus récents pour un job description
    @Query("SELECT a FROM AnalysisResult a WHERE a.jobDescription.id = :jobDescriptionId ORDER BY a.analysisDate DESC")
    List<AnalysisResult> findLatestByJobDescriptionId(@Param("jobDescriptionId") Long jobDescriptionId, Pageable pageable);

    // Compter le nombre d'analyses par job description
    @Query("SELECT COUNT(a) FROM AnalysisResult a WHERE a.jobDescription.id = :jobDescriptionId")
    Long countByJobDescriptionId(@Param("jobDescriptionId") Long jobDescriptionId);

    // Compter le nombre d'analyses par CV
    @Query("SELECT COUNT(a) FROM AnalysisResult a WHERE a.cv.id = :cvId")
    Long countByCvId(@Param("cvId") Long cvId);

    // Calculer le score moyen par job description
    @Query("SELECT AVG(a.overallScore) FROM AnalysisResult a WHERE a.jobDescription.id = :jobDescriptionId")
    Double findAverageScoreByJobDescriptionId(@Param("jobDescriptionId") Long jobDescriptionId);

    // Calculer le score d'intégrité moyen par job description
    @Query("SELECT AVG(a.integrityScore) FROM AnalysisResult a WHERE a.jobDescription.id = :jobDescriptionId")
    Double findAverageIntegrityScoreByJobDescriptionId(@Param("jobDescriptionId") Long jobDescriptionId);

    // Trouver les top N résultats par score
    @Query("SELECT a FROM AnalysisResult a WHERE a.jobDescription.id = :jobDescriptionId ORDER BY a.overallScore DESC")
    List<AnalysisResult> findTopByJobDescriptionId(@Param("jobDescriptionId") Long jobDescriptionId, Pageable pageable);

    // Recherche des résultats avec des mots-clés dans les recommandations
    @Query("SELECT a FROM AnalysisResult a WHERE LOWER(a.recommendations) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AnalysisResult> findByKeywordInRecommendations(@Param("keyword") String keyword);

    // Statistiques détaillées par job description
    @Query("SELECT NEW map(" +
            "AVG(a.overallScore) as avgScore, " +
            "MAX(a.overallScore) as maxScore, " +
            "MIN(a.overallScore) as minScore, " +
            "COUNT(a) as totalAnalyses, " +
            "AVG(a.integrityScore) as avgIntegrity) " +
            "FROM AnalysisResult a WHERE a.jobDescription.id = :jobDescriptionId")
    Object findStatisticsByJobDescriptionId(@Param("jobDescriptionId") Long jobDescriptionId);

    // Pagination des résultats
    Page<AnalysisResult> findByJobDescriptionId(Long jobDescriptionId, Pageable pageable);

    // Trouver les résultats avec des scores de compatibilité élevés
    @Query("SELECT a FROM AnalysisResult a WHERE a.compatibilityScore >= :minCompatibilityScore")
    List<AnalysisResult> findByHighCompatibility(@Param("minCompatibilityScore") Double minCompatibilityScore);

    // Vérifier si une analyse existe pour un CV et job description
    boolean existsByCvIdAndJobDescriptionId(Long cvId, Long jobDescriptionId);

    // Supprimer les analyses anciennes
    void deleteByAnalysisDateBefore(LocalDateTime date);
}