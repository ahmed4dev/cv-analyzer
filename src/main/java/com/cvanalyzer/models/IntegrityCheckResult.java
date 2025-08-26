package com.cvanalyzer.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntegrityCheckResult {

    private Long cvId;
    private Double integrityScore;
    private Boolean isTrustworthy;
    private List<String> inconsistencies;
    private List<String> warnings;
    private List<String> passedChecks;

    // Détails des vérifications
    private Boolean experienceTimelineValid;
    private Boolean skillsConsistent;
    private Boolean educationAuthentic;
    private Boolean personalInfoComplete;
    private Boolean noSuspiciousPatterns;

    // Statistiques
    private Integer totalChecks;
    private Integer passedChecksCount;
    private Integer failedChecksCount;
    private Integer warningCount;

    public IntegrityCheckResult(Long cvId) {
        this.cvId = cvId;
        this.integrityScore = 0.0;
        this.isTrustworthy = false;
        this.inconsistencies = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.passedChecks = new ArrayList<>();
        this.totalChecks = 0;
        this.passedChecksCount = 0;
        this.failedChecksCount = 0;
        this.warningCount = 0;
    }

    // Méthodes utilitaires pour construire le résultat
    public void addInconsistency(String inconsistency) {
        if (this.inconsistencies == null) {
            this.inconsistencies = new ArrayList<>();
        }
        this.inconsistencies.add(inconsistency);
        this.failedChecksCount++;
    }

    public void addWarning(String warning) {
        if (this.warnings == null) {
            this.warnings = new ArrayList<>();
        }
        this.warnings.add(warning);
        this.warningCount++;
    }

    public void addPassedCheck(String checkName) {
        if (this.passedChecks == null) {
            this.passedChecks = new ArrayList<>();
        }
        this.passedChecks.add(checkName);
        this.passedChecksCount++;
    }

    public void calculateFinalScore() {
        if (totalChecks == 0) {
            this.integrityScore = 0.0;
            return;
        }

        // Calcul basé sur le ratio de checks passés
        double baseScore = (double) passedChecksCount / totalChecks;

        // Pénalités pour les warnings (moindre impact)
        double warningPenalty = warningCount * 0.05;

        this.integrityScore = Math.max(0, Math.min(1, baseScore - warningPenalty));
        this.isTrustworthy = this.integrityScore >= 0.7;
    }

    public void setCheckResults(boolean experienceValid, boolean skillsValid,
                                boolean educationValid, boolean personalInfoValid,
                                boolean noSuspicious) {
        this.experienceTimelineValid = experienceValid;
        this.skillsConsistent = skillsValid;
        this.educationAuthentic = educationValid;
        this.personalInfoComplete = personalInfoValid;
        this.noSuspiciousPatterns = noSuspicious;

        this.totalChecks = 5;
        this.passedChecksCount = 0;
        this.failedChecksCount = 0;

        if (experienceValid) passedChecksCount++; else failedChecksCount++;
        if (skillsValid) passedChecksCount++; else failedChecksCount++;
        if (educationValid) passedChecksCount++; else failedChecksCount++;
        if (personalInfoValid) passedChecksCount++; else failedChecksCount++;
        if (noSuspicious) passedChecksCount++; else failedChecksCount++;

        calculateFinalScore();
    }

    // Méthodes pour obtenir des résumés
    public String getSummary() {
        return String.format("Integrity Score: %.2f/1.00 - %s (%d/%d checks passed)",
                integrityScore,
                isTrustworthy ? "Trustworthy" : "Needs Review",
                passedChecksCount,
                totalChecks);
    }

    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== INTEGRITY CHECK REPORT ===\n");
        report.append("CV ID: ").append(cvId).append("\n");
        report.append("Overall Score: ").append(String.format("%.2f", integrityScore * 100)).append("%\n");
        report.append("Status: ").append(isTrustworthy ? "✓ TRUSTWORTHY" : "✗ NEEDS REVIEW").append("\n\n");

        report.append("CHECKS PERFORMED:\n");
        report.append("• Experience Timeline: ").append(experienceTimelineValid ? "✓ PASS" : "✗ FAIL").append("\n");
        report.append("• Skills Consistency: ").append(skillsConsistent ? "✓ PASS" : "✗ FAIL").append("\n");
        report.append("• Education Authenticity: ").append(educationAuthentic ? "✓ PASS" : "✗ FAIL").append("\n");
        report.append("• Personal Info Completeness: ").append(personalInfoComplete ? "✓ PASS" : "✗ FAIL").append("\n");
        report.append("• Suspicious Patterns: ").append(noSuspiciousPatterns ? "✓ PASS" : "✗ FAIL").append("\n\n");

        if (!inconsistencies.isEmpty()) {
            report.append("INCONSISTENCIES FOUND:\n");
            for (String issue : inconsistencies) {
                report.append("• ").append(issue).append("\n");
            }
            report.append("\n");
        }

        if (!warnings.isEmpty()) {
            report.append("WARNINGS:\n");
            for (String warning : warnings) {
                report.append("• ").append(warning).append("\n");
            }
            report.append("\n");
        }

        report.append("STATISTICS:\n");
        report.append("• Total Checks: ").append(totalChecks).append("\n");
        report.append("• Passed: ").append(passedChecksCount).append("\n");
        report.append("• Failed: ").append(failedChecksCount).append("\n");
        report.append("• Warnings: ").append(warningCount).append("\n");

        return report.toString();
    }

    // Méthodes de vérification d'état
    public boolean hasCriticalIssues() {
        return failedChecksCount > 0;
    }

    public boolean hasOnlyWarnings() {
        return failedChecksCount == 0 && warningCount > 0;
    }

    public boolean isPerfectScore() {
        return integrityScore >= 0.95 && failedChecksCount == 0 && warningCount == 0;
    }

    // Méthodes pour l'interface fluide (Fluent Interface)
    public IntegrityCheckResult withInconsistency(String inconsistency) {
        addInconsistency(inconsistency);
        return this;
    }

    public IntegrityCheckResult withWarning(String warning) {
        addWarning(warning);
        return this;
    }

    public IntegrityCheckResult withPassedCheck(String checkName) {
        addPassedCheck(checkName);
        return this;
    }

    public IntegrityCheckResult withCvId(Long cvId) {
        this.cvId = cvId;
        return this;
    }

    // Getters avec valeurs par défaut
    public List<String> getInconsistencies() {
        return inconsistencies != null ? inconsistencies : new ArrayList<>();
    }

    public List<String> getWarnings() {
        return warnings != null ? warnings : new ArrayList<>();
    }

    public List<String> getPassedChecks() {
        return passedChecks != null ? passedChecks : new ArrayList<>();
    }

    public Double getIntegrityScore() {
        return integrityScore != null ? integrityScore : 0.0;
    }

    public Boolean getIsTrustworthy() {
        return isTrustworthy != null ? isTrustworthy : false;
    }
}