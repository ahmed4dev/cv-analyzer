package com.cvanalyzer.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "job_descriptions")
@Getter
@Setter
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Job description is required")
    @Column(nullable = false, length = 5000)
    private String description;

    @Column(length = 100)
    private String department;

    @Column(name = "experience_level", length = 50)
    private String experienceLevel; // JUNIOR, MID, SENIOR, LEAD, etc.

    @Column(name = "posting_date")
    private LocalDate postingDate;

    @NotNull(message = "Required experience years is required")
    @Column(name = "required_experience_years", nullable = false)
    private Integer requiredExperienceYears;

    @ElementCollection
    @CollectionTable(name = "job_required_skills",
            joinColumns = @JoinColumn(name = "job_id"))
    @MapKeyColumn(name = "skill_name")
    @Column(name = "skill_importance")
    private Map<String, Double> requiredSkills; // Skill -> Importance (0.0 to 1.0)

    @ElementCollection
    @CollectionTable(name = "job_required_qualifications",
            joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "qualification", length = 200)
    private List<String> requiredQualifications;

    @ElementCollection
    @CollectionTable(name = "job_preferred_skills",
            joinColumns = @JoinColumn(name = "job_id"))
    @MapKeyColumn(name = "skill_name")
    @Column(name = "skill_weight")
    private Map<String, Double> preferredSkills;

    @Column(name = "min_salary")
    private Double minSalary;

    @Column(name = "max_salary")
    private Double maxSalary;

    @Column(length = 100)
    private String location;

    @Column(name = "work_type", length = 50)
    private String workType; // ONSITE, REMOTE, HYBRID

    @Column(name = "employment_type", length = 50)
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany
    @JoinTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;

    @Column(name = "created_by")
    private Long createdBy; // User ID who created this job description

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "jobDescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalysisResult> analysisResults;

    @PrePersist
    protected void onCreate() {
        if (postingDate == null) {
            postingDate = LocalDate.now();
        }
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDate.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (requiredExperienceYears == null) {
            requiredExperienceYears = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    public JobDescription() {
        // Constructeur par défaut
    }

    public JobDescription(String title, String description, Integer requiredExperienceYears) {
        this.title = title;
        this.description = description;
        this.requiredExperienceYears = requiredExperienceYears;
        this.postingDate = LocalDate.now();
        this.isActive = true;
    }

    // Méthodes utilitaires
    public boolean isRemote() {
        return "REMOTE".equalsIgnoreCase(workType);
    }

    public boolean isFullTime() {
        return "FULL_TIME".equalsIgnoreCase(employmentType);
    }

    public boolean requiresExperience() {
        return requiredExperienceYears != null && requiredExperienceYears > 0;
    }

    public boolean hasRequiredSkills() {
        return requiredSkills != null && !requiredSkills.isEmpty();
    }

    public boolean hasPreferredSkills() {
        return preferredSkills != null && !preferredSkills.isEmpty();
    }
}