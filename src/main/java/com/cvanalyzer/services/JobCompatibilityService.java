package com.cvanalyzer.services;


import com.cvanalyzer.models.CV;
import com.cvanalyzer.models.JobDescription;
import com.cvanalyzer.models.CompatibilityResult;

import java.util.Map;

public interface JobCompatibilityService {

    CompatibilityResult analyzeCompatibility(Long cvId, JobDescription jobDescription);

    CompatibilityResult analyzeCompatibility(CV cv, JobDescription jobDescription);

    Double calculateSkillMatchScore(CV cv, JobDescription jobDescription);

    Double calculateExperienceScore(CV cv, JobDescription jobDescription);

    Double calculateQualificationScore(CV cv, JobDescription jobDescription);

    Double calculateEducationScore(CV cv, JobDescription jobDescription);

    Map<String, Double> getDetailedCompatibilityScores(CV cv, JobDescription jobDescription);
}