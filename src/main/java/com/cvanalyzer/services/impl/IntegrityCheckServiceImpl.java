package com.cvanalyzer.services.impl;

import com.cvanalyzer.models.*;
import com.cvanalyzer.services.IntegrityCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class IntegrityCheckServiceImpl implements IntegrityCheckService {

    // Patterns pour la détection d'incohérences
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");

    // Seuils pour la détection d'anomalies
    private static final double MAX_EXPERIENCE_DURATION = 50.0; // années
    private static final int MIN_AGE_FOR_EXPERIENCE = 16;
    private static final int MAX_JOBS_PER_YEAR = 3;

    @Override
    public IntegrityCheckResult checkCVIntegrity(CV cv) {
        log.info("Checking integrity for CV: {}", cv.getId());

        IntegrityCheckResult result = new IntegrityCheckResult();
        List<String> inconsistencies = new ArrayList<>();
        double score = calculateIntegrityScore(cv);

        // Exécuter toutes les vérifications
        if (!checkExperienceTimeline(cv)) {
            inconsistencies.add("Incohérences temporelles dans les expériences professionnelles");
        }

        if (!checkSkillsConsistency(cv)) {
            inconsistencies.add("Incohérences entre les compétences et les expériences");
        }

        if (!checkEducationAuthenticity(cv)) {
            inconsistencies.add("Problèmes de crédibilité dans les formations");
        }

        if (!checkPersonalInfoCompleteness(cv)) {
            inconsistencies.add("Informations personnelles incomplètes ou invalides");
        }

        if (detectSuspiciousPatterns(cv)) {
            inconsistencies.add("Patterns suspects détectés dans le CV");
        }

        result.setIntegrityScore(score);
        result.setInconsistencies(inconsistencies);
        result.setIsTrustworthy(score >= 0.7);
        result.setCvId(cv.getId());

        log.info("Integrity check completed for CV: {} - Score: {}", cv.getId(), score);
        return result;
    }

    @Override
    public boolean checkExperienceTimeline(CV cv) {
        if (cv.getExperiences() == null || cv.getExperiences().isEmpty()) {
            return true; // Aucune expérience à vérifier
        }

        List<WorkExperience> experiences = new ArrayList<>(cv.getExperiences());
        experiences.sort(Comparator.comparing(WorkExperience::getStartDate));

        // Vérifier les chevauchements et incohérences temporelles
        for (int i = 0; i < experiences.size() - 1; i++) {
            WorkExperience current = experiences.get(i);
            WorkExperience next = experiences.get(i + 1);

            if (current.getEndDate() != null && next.getStartDate() != null) {
                // Vérifier les chevauchements
                if (current.getEndDate().isAfter(next.getStartDate())) {
                    log.warn("Chevauchement détecté entre {} et {}", current.getCompanyName(), next.getCompanyName());
                    return false;
                }

                // Vérifier les gaps trop courts entre jobs
                Period gap = Period.between(current.getEndDate(), next.getStartDate());
                if (gap.getMonths() < 1) { // Moins d'un mois entre deux jobs
                    log.warn("Gap temporel suspect entre {} et {}", current.getCompanyName(), next.getCompanyName());
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean checkSkillsConsistency(CV cv) {
        if (cv.getSkills() == null || cv.getSkills().isEmpty()) {
            return true; // Aucune compétence à vérifier
        }

        Set<String> claimedSkills = new HashSet<>(cv.getSkills());
        Set<String> experienceSkills = new HashSet<>();

        // Extraire les compétences des expériences
        if (cv.getExperiences() != null) {
            for (WorkExperience exp : cv.getExperiences()) {
                if (exp.getSkills() != null) {
                    experienceSkills.addAll(exp.getSkills());
                }
            }
        }

        // Vérifier la cohérence
        for (String skill : claimedSkills) {
            if (!experienceSkills.contains(skill)) {
                log.warn("Compétence {} déclarée mais non supportée par les expériences", skill);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean checkEducationAuthenticity(CV cv) {
        if (cv.getEducations() == null || cv.getEducations().isEmpty()) {
            return true; // Aucune formation à vérifier
        }

        LocalDate now = LocalDate.now();

        for (Education education : cv.getEducations()) {
            // Vérifier les dates de formation
            if (education.getStartDate() != null && education.getEndDate() != null) {
                if (education.getEndDate().isAfter(now)) {
                    log.warn("Formation {} se termine dans le futur", education.getDegree());
                    return false;
                }

                if (education.getStartDate().isAfter(education.getEndDate())) {
                    log.warn("Dates incohérentes pour la formation {}", education.getDegree());
                    return false;
                }

                // Vérifier la durée raisonnable des études
                Period duration = Period.between(education.getStartDate(), education.getEndDate());
                if (duration.getYears() > 8) { // Durée maximale raisonnable pour des études
                    log.warn("Durée de formation {} suspecte: {} années",
                            education.getDegree(), duration.getYears());
                    return false;
                }
            }

            // Vérifier la crédibilité des établissements
            if (education.getInstitution() != null &&
                    containsSuspiciousKeywords(education.getInstitution())) {
                log.warn("Établissement suspect: {}", education.getInstitution());
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean checkPersonalInfoCompleteness(CV cv) {
        // Vérifier les informations de base
        if (cv.getCandidate() == null) {
            return false;
        }

        Candidate candidate = cv.getCandidate();

        // Vérifier l'email
        if (candidate.getEmail() == null || !EMAIL_PATTERN.matcher(candidate.getEmail()).matches()) {
            return false;
        }

        // Vérifier le téléphone
        if (candidate.getPhone() != null && !PHONE_PATTERN.matcher(candidate.getPhone()).matches()) {
            return false;
        }

        // Vérifier l'âge minimum pour l'expérience
        if (candidate.getDateOfBirth() != null && cv.getExperiences() != null) {
            for (WorkExperience exp : cv.getExperiences()) {
                if (exp.getStartDate() != null) {
                    Period ageAtStart = Period.between(candidate.getDateOfBirth(), exp.getStartDate());
                    if (ageAtStart.getYears() < MIN_AGE_FOR_EXPERIENCE) {
                        log.warn("Expérience professionnelle commencée à un âge suspect: {} ans",
                                ageAtStart.getYears());
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean detectSuspiciousPatterns(CV cv) {
        // Vérifier le nombre d'emplois sur une période courte
        if (cv.getExperiences() != null) {
            Map<Integer, Integer> jobsPerYear = new HashMap<>();

            for (WorkExperience exp : cv.getExperiences()) {
                if (exp.getStartDate() != null) {
                    int year = exp.getStartDate().getYear();
                    jobsPerYear.put(year, jobsPerYear.getOrDefault(year, 0) + 1);

                    if (jobsPerYear.get(year) > MAX_JOBS_PER_YEAR) {
                        log.warn("Trop d'emplois ({}) en une année: {}", jobsPerYear.get(year), year);
                        return true;
                    }
                }
            }
        }

        // Vérifier les durées d'expérience excessives
        if (cv.getExperiences() != null) {
            double totalExperience = calculateTotalExperienceYears(cv.getExperiences());
            if (totalExperience > MAX_EXPERIENCE_DURATION) {
                log.warn("Expérience totale excessive: {} années", totalExperience);
                return true;
            }
        }

        // Détecter les patterns de job hopping excessif
        if (isExcessiveJobHopping(cv.getExperiences())) {
            log.warn("Pattern de job hopping excessif détecté");
            return true;
        }

        return false;
    }

    @Override
    public double calculateIntegrityScore(CV cv) {
        double score = 1.0; // Score parfait initial

        // Pénalités pour chaque vérification échouée
        if (!checkExperienceTimeline(cv)) score -= 0.3;
        if (!checkSkillsConsistency(cv)) score -= 0.2;
        if (!checkEducationAuthenticity(cv)) score -= 0.2;
        if (!checkPersonalInfoCompleteness(cv)) score -= 0.1;
        if (detectSuspiciousPatterns(cv)) score -= 0.2;

        // Assurer que le score reste entre 0 et 1
        return Math.max(0, Math.min(1, score));
    }

    // Méthodes helper privées
    private boolean containsSuspiciousKeywords(String text) {
        if (text == null) return false;

        String[] suspiciousKeywords = {
                "diploma mill", "unaccredited", "non-accredited",
                "online degree", "instant degree", "life experience"
        };

        String lowerText = text.toLowerCase();
        return Arrays.stream(suspiciousKeywords)
                .anyMatch(lowerText::contains);
    }

    private double calculateTotalExperienceYears(List<WorkExperience> experiences) {
        if (experiences == null || experiences.isEmpty()) return 0.0;

        long totalMonths = experiences.stream()
                .filter(exp -> exp.getStartDate() != null)
                .mapToLong(exp -> {
                    LocalDate endDate = exp.getEndDate() != null ?
                            exp.getEndDate() : LocalDate.now();
                    return java.time.temporal.ChronoUnit.MONTHS.between(
                            exp.getStartDate(), endDate);
                })
                .sum();

        return totalMonths / 12.0;
    }

    private boolean isExcessiveJobHopping(List<WorkExperience> experiences) {
        if (experiences == null || experiences.size() < 3) return false;

        int shortTermJobs = 0;
        for (WorkExperience exp : experiences) {
            if (exp.getStartDate() != null && exp.getEndDate() != null) {
                Period duration = Period.between(exp.getStartDate(), exp.getEndDate());
                if (duration.getMonths() < 6) { // Moins de 6 mois = court terme
                    shortTermJobs++;
                }
            }
        }

        // Si plus de 50% des jobs sont de courte durée
        return shortTermJobs > experiences.size() * 0.5;
    }
}