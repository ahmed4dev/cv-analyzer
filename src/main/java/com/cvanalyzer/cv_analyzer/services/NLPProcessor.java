package com.cvanalyzer.cv_analyzer.services;

import com.cvanalyzer.cv_analyzer.models.AnalysisResult;
import com.cvanalyzer.cv_analyzer.models.Skill;
import com.cvanalyzer.cv_analyzer.models.Suggestion;

import java.util.List;
import java.util.Set;

public interface NLPProcessor {
    AnalysisResult analyze(String text);
    Set<String> extractSkills(String text);
    List<String> extractExperiences(String text);
    List<String> extractEducations(String text);

}

