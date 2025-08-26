package com.cvanalyzer.services;

import com.cvanalyzer.models.AnalysisResult;

import java.util.List;
import java.util.Set;

public interface NLPProcessor {
    AnalysisResult analyze(String text);
    Set<String> extractSkills(String text);
    List<String> extractExperiences(String text);
    List<String> extractEducations(String text);

}

