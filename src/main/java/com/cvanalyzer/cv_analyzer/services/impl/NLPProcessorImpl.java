package com.cvanalyzer.cv_analyzer.services.impl;

import com.cvanalyzer.cv_analyzer.models.AnalysisResult;
import com.cvanalyzer.cv_analyzer.models.Suggestion;
import com.cvanalyzer.cv_analyzer.models.enums.PriorityLevel;
import com.cvanalyzer.cv_analyzer.models.enums.SuggestionType;
import com.cvanalyzer.cv_analyzer.services.NLPProcessor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

import java.util.*;
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
                .matchScore(calculateOverallScore(textContent))
                .suggestions(generateSuggestions(textContent))
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

/*    private List<String> extractSkills(String text) {
        String lowerText = text.toLowerCase();
        return TECHNICAL_SKILLS.stream()
                .filter(skill -> lowerText.contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }*/

    public List<String> extractSkills(String text) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<String> skills = new ArrayList<>();
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if ("SKILL".equals(ner)) skills.add(token.word());
            }
        }
        return skills;
    }

  /*  private  List<Suggestion> generateSuggestions(String text) {
        List<String> missingSkills = TECHNICAL_SKILLS.stream()
                .filter(skill -> !text.toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());

        return missingSkills.stream()
                .map(skill -> new Suggestion("Ajouter la compétence : " + skill))
                .collect(Collectors.toList());
    }*/

    private List<Suggestion> generateSuggestions(String text) {
        return TECHNICAL_SKILLS.stream()
                .filter(skill -> !text.toLowerCase().contains(skill.toLowerCase()))
                .map(skill -> {
                    Suggestion suggestion = new Suggestion();
                    suggestion.setType(SuggestionType.KEYWORD); // ou autre selon ton contexte
                    suggestion.setMessage("Ajoutez la compétence : " + skill);
                    suggestion.setPriority(PriorityLevel.MEDIUM); // ou selon une règle métier
                    return suggestion;
                })
                .collect(Collectors.toList());
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