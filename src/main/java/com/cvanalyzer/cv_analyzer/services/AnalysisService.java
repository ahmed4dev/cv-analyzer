package com.cvanalyzer.cv_analyzer.services;

import com.cvanalyzer.cv_analyzer.models.AnalysisResult;


public interface AnalysisService {
    AnalysisResult analyzeCV(Long cvId);
    AnalysisResult getAnalysisById(Long id);
}
