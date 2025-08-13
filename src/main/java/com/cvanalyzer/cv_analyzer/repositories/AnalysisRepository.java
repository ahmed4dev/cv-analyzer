package com.cvanalyzer.cv_analyzer.repositories;

import com.cvanalyzer.cv_analyzer.models.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisResult, Long> {
}
