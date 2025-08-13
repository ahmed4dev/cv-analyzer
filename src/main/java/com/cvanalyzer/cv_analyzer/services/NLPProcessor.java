package com.cvanalyzer.cv_analyzer.services;

import com.cvanalyzer.cv_analyzer.models.AnalysisResult;

public interface NLPProcessor {
        AnalysisResult analyze(String textContent);
    }

