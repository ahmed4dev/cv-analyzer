package com.cvanalyzer.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "work_experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", nullable = false)
    private CV cv;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "position_title", nullable = false)
    private String positionTitle;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current_job")
    private Boolean isCurrentJob;

    @Column(name = "description", length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "work_experience_skills",
            joinColumns = @JoinColumn(name = "work_experience_id")
    )
    @Column(name = "skill")
    private List<String> skills;

    @Column(name = "industry")
    private String industry;

    @Column(name = "employment_type")
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, etc.

    @Column(name = "location")
    private String location;

    @Column(name = "achievements", length = 3000)
    private String achievements;

    @Column(name = "technologies_used", length = 1000)
    private String technologiesUsed;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "reasons_for_leaving", length = 1000)
    private String reasonsForLeaving;

    @Column(name = "supervisor_name")
    private String supervisorName;

    @Column(name = "supervisor_contact")
    private String supervisorContact;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "currency")
    private String currency;

    @Column(name = "verification_status")
    private String verificationStatus; // VERIFIED, PENDING, UNVERIFIED

    @Column(name = "verification_notes", length = 1000)
    private String verificationNotes;

    @Column(name = "relevance_score")
    private Double relevanceScore;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @Column(name = "duration_in_months")
    private Integer durationInMonths;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();

        // Déterminer automatiquement si c'est le poste actuel
        if (isCurrentJob == null) {
            isCurrentJob = endDate == null;
        }

        // Déterminer automatiquement le statut de vérification
        if (verificationStatus == null) {
            verificationStatus = "UNVERIFIED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();

        // Mettre à jour isCurrentJob si endDate change
        if (endDate != null) {
            isCurrentJob = false;
        }
    }

    // Méthodes utilitaires
    public Integer getDurationInMonths() {
        if (startDate == null) {
            return 0;
        }

        LocalDate end = endDate != null ? endDate : LocalDate.now();

        Period period = Period.between(startDate, end);
        return period.getYears() * 12 + period.getMonths();
    }

    public Double getDurationInYears() {
        Integer months = getDurationInMonths();
        return months / 12.0;
    }

    public boolean isOverlapping(WorkExperience other) {
        if (this.startDate == null || other.startDate == null) {
            return false;
        }

        LocalDate thisEnd = this.endDate != null ? this.endDate : LocalDate.now();
        LocalDate otherEnd = other.endDate != null ? other.endDate : LocalDate.now();

        return this.startDate.isBefore(otherEnd) && other.startDate.isBefore(thisEnd);
    }

    public boolean isValid() {
        if (startDate == null) {
            return false;
        }

        // Vérifier que endDate est après startDate si spécifié
        if (endDate != null && endDate.isBefore(startDate)) {
            return false;
        }

        // Vérifier que la date de fin n'est pas dans le futur (sauf pour le poste actuel)
        if (endDate != null && endDate.isAfter(LocalDate.now()) && !isCurrentJob) {
            return false;
        }

        return companyName != null && !companyName.trim().isEmpty() &&
                positionTitle != null && !positionTitle.trim().isEmpty();
    }

    public String getFormattedDuration() {
        Integer months = getDurationInMonths();
        int years = months / 12;
        int remainingMonths = months % 12;

        if (years > 0 && remainingMonths > 0) {
            return years + " an" + (years > 1 ? "s" : "") + " et " + remainingMonths + " mois";
        } else if (years > 0) {
            return years + " an" + (years > 1 ? "s" : "");
        } else {
            return remainingMonths + " mois";
        }
    }

    public boolean isVerified() {
        return "VERIFIED".equals(verificationStatus);
    }

    public boolean needsVerification() {
        return "PENDING".equals(verificationStatus) || "UNVERIFIED".equals(verificationStatus);
    }

    public boolean isRecentExperience() {
        if (endDate != null) {
            return endDate.isAfter(LocalDate.now().minusYears(2));
        }
        return true; // Poste actuel
    }

    public boolean isLongTermPosition() {
        Integer months = getDurationInMonths();
        return months >= 24; // 2 ans ou plus
    }

    // Méthodes pour calculer la pertinence (à utiliser dans les algorithmes de matching)
    public double calculateRelevance(List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty() || skills == null) {
            return 0.0;
        }

        long matchingSkills = skills.stream()
                .filter(requiredSkills::contains)
                .count();

        return (double) matchingSkills / requiredSkills.size();
    }

    public boolean hasManagementExperience() {
        if (positionTitle == null) {
            return false;
        }

        String lowerTitle = positionTitle.toLowerCase();
        return lowerTitle.contains("manager") ||
                lowerTitle.contains("director") ||
                lowerTitle.contains("head of") ||
                lowerTitle.contains("lead") ||
                lowerTitle.contains("supervisor");
    }

    public boolean isExecutivePosition() {
        if (positionTitle == null) {
            return false;
        }

        String lowerTitle = positionTitle.toLowerCase();
        return lowerTitle.contains("ceo") ||
                lowerTitle.contains("cto") ||
                lowerTitle.contains("cfo") ||
                lowerTitle.contains("vp") ||
                lowerTitle.contains("vice president");
    }
}