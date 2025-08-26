    package com.cvanalyzer.services.impl;

    import com.cvanalyzer.models.AnalysisResult;
    import com.cvanalyzer.models.enums.SuggestionType;
    import com.cvanalyzer.models.enums.PriorityLevel;
    import com.cvanalyzer.models.Skill;
    import com.cvanalyzer.models.Suggestion;
    import com.cvanalyzer.services.NLPProcessor;
    import edu.stanford.nlp.ling.CoreAnnotations;
    import edu.stanford.nlp.ling.CoreLabel;
    import edu.stanford.nlp.pipeline.Annotation;
    import edu.stanford.nlp.pipeline.StanfordCoreNLP;
    import edu.stanford.nlp.util.CoreMap;
    import org.springframework.stereotype.Service;
    import java.util.*;

    @Service
    public class StanfordNlpServiceImpl implements NLPProcessor {
        private final StanfordCoreNLP pipeline;

        public StanfordNlpServiceImpl(StanfordCoreNLP pipeline) {
            this.pipeline = pipeline;
        }

        @Override
        public Set<String> extractSkills(String text) {
            Annotation document = new Annotation(text);
            pipeline.annotate(document);

            Set<String> skills = new HashSet<>();
            Map<String, List<String>> technicalKeywords = Map.of(

                    "INFORMATIQUE", List.of(
                            "java", "python", "spring", "docker", "kubernetes", "git", "sql", "nosql",
                            "mongodb", "react", "angular", "vuejs", "nodejs", "typescript", "aws", "azure",
                            "gcp", "linux", "jenkins", "kafka", "api rest", "microservices", "graphql", "html", "css"
                    ),

                    "FINANCE", List.of(
                            "comptabilité", "audit", "trésorerie", "analyse financière", "bilan", "budget",
                            "erp", "sap", "sage", "trading", "actions", "obligations", "risque", "kyc", "aml",
                            "valuation", "levée de fonds", "reporting", "power bi", "excel", "vba"
                    ),

                    "MARKETING", List.of(
                            "seo", "sem", "google ads", "meta ads", "analytics", "crm", "hubspot", "mailchimp",
                            "branding", "ux", "ui", "social media", "content marketing", "growth hacking", "lead generation"
                    ),

                    "RESSOURCES_HUMAINES", List.of(
                            "recrutement", "paie", "gestion du personnel", "silae", "adp", "entretiens annuels",
                            "onboarding", "offboarding", "bilan social", "grh", "plan de carrière"
                    ),

                    "SANTÉ", List.of(
                            "soins infirmiers", "pharmacologie", "diagnostic", "imagerie médicale", "chirurgie",
                            "biologie", "anatomie", "dossier médical", "soins intensifs", "pédiatrie"
                    ),

                    "INGÉNIERIE", List.of(
                            "cao", "dao", "autocad", "solidworks", "catia", "matlab", "r&d", "analyse de matériaux",
                            "thermodynamique", "électronique", "robotique", "process", "lean", "six sigma"
                    ),

                    "JURIDIQUE", List.of(
                            "droit du travail", "règlementation", "rgpd", "compliance", "contrats", "jurisprudence",
                            "droit fiscal", "droit pénal", "droit des sociétés", "contentieux", "avocat"
                    ),

                    "ÉDUCATION", List.of(
                            "pédagogie", "enseignement", "didactique", "programme scolaire", "évaluation", "élèves",
                            "cours en ligne", "e-learning", "tice", "formation continue"
                    )
            );

            for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    String word = token.word().toLowerCase();

                    for (Map.Entry<String, List<String>> entry : technicalKeywords.entrySet()) {
                        if (entry.getValue().contains(word)) {
                            Skill skill = new Skill();
                            skill.setName(word);
                            skill.setCategory(entry.getKey());
                            skill.setRelevance(1.0); // fictif
                            skills.add(String.valueOf(skill));
                            break;
                        }
                    }
                }
            }
            return skills;
        }

        @Override
        public List<String> extractExperiences(String text) {
            Annotation document = new Annotation(text);
            pipeline.annotate(document);

            List<String> experiences = new ArrayList<>();
            for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
                String sentenceText = sentence.get(CoreAnnotations.TextAnnotation.class);
                if (sentenceText.matches("(?i).*(expérience|experience|worked|travail|job|poste).*")) {
                    experiences.add(sentenceText);
                }
            }
            return experiences;
        }

        @Override
        public List<String> extractEducations(String text) {
            Annotation document = new Annotation(text);
            pipeline.annotate(document);

            List<String> educations = new ArrayList<>();
            for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
                String sentenceText = sentence.get(CoreAnnotations.TextAnnotation.class);
                if (sentenceText.matches("(?i).*(formation|education|diplôme|diploma|école|university).*")) {
                    educations.add(sentenceText);
                }
            }
            return educations;
        }

        @Override
        public AnalysisResult analyze(String text) {
            Set<String> rawSkills = extractSkills(text);

            // Convert Set<String> -> List<Skill> avec score fictif
            List<Skill> skills = rawSkills.stream().map(skillName -> {
                Skill s = new Skill();
                s.setName(skillName);
                s.setRelevance(1.0); // Score fictif pour l'instant
                s.setCategory("TECHNIQUE"); // Tu peux améliorer la catégorisation plus tard
                return s;
            }).toList();

            List<String> experiences = extractExperiences(text);
            List<String> educations = extractEducations(text);

            // Suggestions basées sur le contenu manquant (exemple simple)
            List<Suggestion> suggestions = new ArrayList<>();
            if (experiences.isEmpty()) {
                suggestions.add(new Suggestion(SuggestionType.EDUCATION ,"Aucune expérience trouvée", PriorityLevel.MEDIUM));
            }
            if (educations.isEmpty()) {
                suggestions.add(new Suggestion(SuggestionType.EXPERIENCE,"Aucune formation trouvée", PriorityLevel.MEDIUM));
            }

            // Score fictif basé sur le nombre de compétences trouvées
            double matchScore = Math.min(1.0, skills.size() / 10.0);

            String summary = String.format(
                    "CV analysé avec %d compétences, %d expériences et %d formations.",
                    skills.size(), experiences.size(), educations.size()
            );

            return AnalysisResult.builder()
                    .matchScore(matchScore)
                    .extractedSkills(skills)
                    .suggestions(suggestions)
                    .summary(summary)
                    .cv(null) // ou associer un CV si disponible
                    .build();
        }

    }