package com.cvanalyzer.repositories;

import com.cvanalyzer.models.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisResult, Long> {
}
