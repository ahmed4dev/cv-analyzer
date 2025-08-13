package com.cvanalyzer.cv_analyzer.services.impl;

import com.cvanalyzer.cv_analyzer.models.AnalysisResult;
import com.cvanalyzer.cv_analyzer.services.NLPProcessor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NLPProcessorImpl implements NLPProcessor {

    private static final List<String> TECHNICAL_SKILLS = List.of(
            "Java", "Spring", "Python", "SQL", "JavaScript",
            "React", "Angular", "Docker", "Kubernetes", "AWS"
    );

    @Override
    public AnalysisResult analyze(String textContent) {
        return AnalysisResult.builder()
                .score(calculateOverallScore(textContent))
                .suggestions(generateSuggestions(textContent))
                .sectionScores(calculateSectionScores(textContent))
                .build();
    }

    private Double calculateOverallScore(String text) {
        long skillsFound = extractSkills(text).size();
        long totalSkills = TECHNICAL_SKILLS.size();
        return (double) skillsFound / totalSkills * 10; // Score sur 10
    }

    private Map<String, Double> calculateSectionScores(String text) {
        Map<String, Double> scores = new HashMap<>();

        // Score pour les compétences techniques
        double techSkillsScore = (double) extractSkills(text).size() / TECHNICAL_SKILLS.size() * 10;
        scores.put("Compétences techniques", techSkillsScore);

        // Scores pour les autres sections
        scores.put("Expérience professionnelle", detectExperienceScore(text));
        scores.put("Formation", detectEducationScore(text));
        scores.put("Langues", detectLanguagesScore(text));

        return scores;
    }

    private List<String> extractSkills(String text) {
        String lowerText = text.toLowerCase();
        return TECHNICAL_SKILLS.stream()
                .filter(skill -> lowerText.contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }

    private String generateSuggestions(String text) {
        List<String> missingSkills = TECHNICAL_SKILLS.stream()
                .filter(skill -> !text.toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());

        return missingSkills.isEmpty()
                ? "Compétences techniques complètes."
                : "Compétences à développer: " + String.join(", ", missingSkills);
    }

    private double detectExperienceScore(String text) {
        // Logique simplifiée - à améliorer
        boolean hasExperience = text.toLowerCase().contains("expérience")
                || text.toLowerCase().contains("expériences");
        return hasExperience ? 8.0 : 3.0;
    }

    private double detectEducationScore(String text) {
        long educationTerms = Stream.of("diplôme", "université", "école", "formation")
                .filter(term -> text.toLowerCase().contains(term))
                .count();
        return Math.min(10.0, educationTerms * 3.0);
    }

    private double detectLanguagesScore(String text) {
        long languages = Stream.of("anglais", "espagnol", "allemand", "français")
                .filter(lang -> text.toLowerCase().contains(lang))
                .count();
        return Math.min(10.0, languages * 2.5);
    }
}