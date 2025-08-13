package com.cvanalyzer.cv_analyzer.repositories;

import com.cvanalyzer.cv_analyzer.models.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {
}