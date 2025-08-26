package com.cvanalyzer.services;

import com.cvanalyzer.models.DashboardData;
import com.cvanalyzer.models.JobDescription;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DashboardService {

    DashboardData generateDashboardData(Long jobDescriptionId);

    DashboardData generateDashboardData(JobDescription jobDescription);

    Optional<DashboardData> getDashboardDataById(Long id);

    List<DashboardData> getDashboardDataByJobDescription(Long jobDescriptionId);

    Optional<DashboardData> getCurrentDashboardData(Long jobDescriptionId);

    DashboardData updateDashboardData(Long id, DashboardData dashboardData);

    void deleteDashboardData(Long id);

    List<DashboardData> getDashboardHistory(Long jobDescriptionId, int days);

    DashboardData generateComparativeDashboard(List<Long> jobDescriptionIds);

    DashboardData generateTrendAnalysis(Long jobDescriptionId, LocalDateTime startDate, LocalDateTime endDate);

    DashboardData refreshDashboardData(Long jobDescriptionId);

    void archiveOldDashboardData(int daysThreshold);

    List<DashboardData> searchDashboards(String keyword, LocalDateTime startDate, LocalDateTime endDate);

    DashboardData generateSummaryDashboard(Long userId);
}