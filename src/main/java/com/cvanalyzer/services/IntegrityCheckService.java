package com.cvanalyzer.services;

import com.cvanalyzer.models.CV;
import com.cvanalyzer.models.IntegrityCheckResult;

public interface IntegrityCheckService {

    /**
     * Vérifie l'intégrité complète d'un CV
     */
    IntegrityCheckResult checkCVIntegrity(CV cv);

    /**
     * Vérifie les incohérences temporelles dans les expériences
     */
    boolean checkExperienceTimeline(CV cv);

    /**
     * Vérifie la cohérence des compétences avec les expériences
     */
    boolean checkSkillsConsistency(CV cv);

    /**
     * Vérifie l'authenticité des formations et certifications
     */
    boolean checkEducationAuthenticity(CV cv);

    /**
     * Vérifie la complétude des informations personnelles
     */
    boolean checkPersonalInfoCompleteness(CV cv);

    /**
     * Détecte les patterns suspects ou incohérents
     */
    boolean detectSuspiciousPatterns(CV cv);

    /**
     * Calcule un score d'intégrité global (0-1)
     */
    double calculateIntegrityScore(CV cv);
}