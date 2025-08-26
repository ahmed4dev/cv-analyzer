package com.cvanalyzer.repositories;

import com.cvanalyzer.models.DashboardData;
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
public interface DashboardDataRepository extends JpaRepository<DashboardData, Long> {

    // Trouver les dashboards par ID de job description
    List<DashboardData> findByJobDescriptionId(Long jobDescriptionId);

    // Trouver le dashboard actuel pour un job description
    Optional<DashboardData> findByJobDescriptionIdAndIsCurrent(Long jobDescriptionId, Boolean isCurrent);

    // Trouver tous les dashboards actuels
    List<DashboardData> findByIsCurrent(Boolean isCurrent);

    // Trouver les dashboards par période
    List<DashboardData> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);

    // Trouver les dashboards par créateur
    List<DashboardData> findByCreatedBy(Long userId);

    // Trouver le dashboard le plus récent pour un job description
    @Query("SELECT d FROM DashboardData d WHERE d.jobDescription.id = :jobId " +
            "AND d.generatedAt = (SELECT MAX(d2.generatedAt) FROM DashboardData d2 WHERE d2.jobDescription.id = :jobId)")
    Optional<DashboardData> findLatestByJobDescriptionId(@Param("jobId") Long jobId);

    // Trouver les dashboards par plage de dates pour un job description
    @Query("SELECT d FROM DashboardData d WHERE d.jobDescription.id = :jobId " +
            "AND d.generatedAt >= :startDate ORDER BY d.generatedAt DESC")
    List<DashboardData> findByJobDescriptionIdAndDateRange(@Param("jobId") Long jobId,
                                                           @Param("startDate") LocalDateTime startDate);

    // Compter les dashboards par job description
    @Query("SELECT COUNT(d) FROM DashboardData d WHERE d.jobDescription.id = :jobId")
    Long countByJobDescriptionId(@Param("jobId") Long jobId);

    // Trouver les dashboards par plage de score moyen
    @Query("SELECT d FROM DashboardData d WHERE d.averageScore >= :minScore " +
            "AND d.averageScore <= :maxScore")
    List<DashboardData> findByAverageScoreRange(@Param("minScore") Double minScore,
                                                @Param("maxScore") Double maxScore);

    // Trouver les dashboards avec un score moyen minimum
    List<DashboardData> findByAverageScoreGreaterThanEqual(Double minScore);

    // Trouver les dashboards avec un nombre minimum de violations d'intégrité
    List<DashboardData> findByIntegrityViolationsCountGreaterThan(Integer count);

    // Trouver les dashboards avec un taux de complétion minimum
    @Query("SELECT d FROM DashboardData d WHERE d.completionRate >= :minRate")
    List<DashboardData> findByCompletionRateGreaterThanEqual(@Param("minRate") Double minRate);

    // Trouver les dashboards avec un taux de réussite élevé
    @Query("SELECT d FROM DashboardData d WHERE d.completionRate >= 80.0")
    List<DashboardData> findHighCompletionDashboards();

    // Statistiques des dashboards par période
    @Query("SELECT NEW map(" +
            "COUNT(d) as totalDashboards, " +
            "AVG(d.averageScore) as avgDashboardScore, " +
            "MAX(d.generatedAt) as latestGeneration) " +
            "FROM DashboardData d WHERE d.generatedAt BETWEEN :start AND :end")
    Object findDashboardStatisticsByPeriod(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    // Trouver les dashboards avec des insights spécifiques
    @Query("SELECT d FROM DashboardData d WHERE LOWER(d.insights) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<DashboardData> findByInsightsContaining(@Param("keyword") String keyword);

    // Pagination des dashboards
    Page<DashboardData> findByJobDescriptionId(Long jobDescriptionId, Pageable pageable);

    // Trouver les dashboards par type de période
    List<DashboardData> findByTimePeriod(String timePeriod);

    // Trouver les dashboards avec des métriques spécifiques
    @Query("SELECT d FROM DashboardData d WHERE d.additionalMetrics IS NOT EMPTY")
    List<DashboardData> findDashboardsWithAdditionalMetrics();

    // Mettre à jour le statut "current"
    @Query("UPDATE DashboardData d SET d.isCurrent = :isCurrent WHERE d.jobDescription.id = :jobId")
    void updateCurrentStatus(@Param("jobId") Long jobId, @Param("isCurrent") Boolean isCurrent);

    // Supprimer les dashboards anciens
    void deleteByGeneratedAtBefore(LocalDateTime date);

    // Vérifier l'existence d'un dashboard actuel
    boolean existsByJobDescriptionIdAndIsCurrent(Long jobDescriptionId, Boolean isCurrent);

    List<DashboardData> findByJobDescriptionIdAndGeneratedAtAfter(Long jobDescriptionId, LocalDateTime since);

    // Vérifier l’existence d’un dashboard actuel avant une date donnée

    List<DashboardData> findByGeneratedAtBeforeAndIsCurrent(LocalDateTime thresholdDate, boolean isCurrent);
}