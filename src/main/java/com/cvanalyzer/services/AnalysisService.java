package com.cvanalyzer.services;

import com.cvanalyzer.models.AnalysisResult;


public interface AnalysisService {
    AnalysisResult analyzeCV(Long cvId);
    AnalysisResult getAnalysisById(Long id);
}
