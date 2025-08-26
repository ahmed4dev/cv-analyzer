package com.cvanalyzer.services.impl;

import com.cvanalyzer.models.*;
import com.cvanalyzer.services.JobCompatibilityService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobCompatibilityServiceImpl implements JobCompatibilityService {

    @Override
    public CompatibilityResult analyzeCompatibility(Long cvId, JobDescription jobDescription) {
        // Ici, tu pourrais charger le CV depuis la base si nécessaire
        // Exemple fictif :
        CV cv = new CV(); // Remplacer par un vrai chargement
        return analyzeCompatibility(cv, jobDescription);
    }

    @Override
            public CompatibilityResult analyzeCompatibility(CV cv, JobDescription jobDescription) {
                // Exemple fictif de compatibilité
                double skillScore = calculateSkillMatchScore(cv, jobDescription);
                double expScore = calculateExperienceScore(cv, jobDescription);
                double qualScore = calculateQualificationScore(cv, jobDescription);
                double eduScore = calculateEducationScore(cv, jobDescription);

                double overall = (skillScore + expScore + qualScore + eduScore) / 4.0;

                return CompatibilityResult.builder()
                        .overallScore(overall)
                        .skillMatchScore(skillScore)
                        .experienceScore(expScore)
                        .qualificationScore(qualScore)
                        .educationScore(eduScore)
                        .strengths(List.of("Teamwork", "Communication"))
                        .weaknesses(List.of("Java", "Leadership"))
                        .build();
    }

    @Override
    public Double calculateSkillMatchScore(CV cv, JobDescription jobDescription) {
        return 0.75; // Simulé
    }

    @Override
    public Double calculateExperienceScore(CV cv, JobDescription jobDescription) {
        return 0.8; // Simulé
    }

    @Override
    public Double calculateQualificationScore(CV cv, JobDescription jobDescription) {
        return 0.7; // Simulé
    }

    @Override
    public Double calculateEducationScore(CV cv, JobDescription jobDescription) {
        return 0.9; // Simulé
    }

    @Override
    public Map<String, Double> getDetailedCompatibilityScores(CV cv, JobDescription jobDescription) {
        Map<String, Double> scores = new HashMap<>();
        scores.put("skill", calculateSkillMatchScore(cv, jobDescription));
        scores.put("experience", calculateExperienceScore(cv, jobDescription));
        scores.put("qualification", calculateQualificationScore(cv, jobDescription));
        scores.put("education", calculateEducationScore(cv, jobDescription));
        return scores;
    }
}
