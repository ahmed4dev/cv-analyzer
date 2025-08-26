package com.cvanalyzer.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(indexName = "cv_documents")
@Setting(settingPath = "/elasticsearch/analyzer-settings.json")
public class EsCvDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long cvId;

    @Field(type = FieldType.Text)
    private String fileName;

    @Field(type = FieldType.Keyword)
    private String fileType;

    @Field(type = FieldType.Date)
    private LocalDateTime uploadDate;

    @Field(type = FieldType.Text, analyzer = "french_analyzer")
    private String parsedText;

    @Field(type = FieldType.Keyword)
    private List<String> skills;

    @Field(type = FieldType.Integer)
    private Integer experienceYears; // Champ nécessaire pour les requêtes existantes

    // Informations du candidat
    @Field(type = FieldType.Object)
    private CandidateInfo candidate;

    // Formations
    @Field(type = FieldType.Nested)
    private List<EducationInfo> educations;

    // Expériences professionnelles
    @Field(type = FieldType.Nested)
    private List<ExperienceInfo> experiences;

    @Field(type = FieldType.Date)
    private LocalDateTime indexedAt;

    // Classes internes pour structurer les données
    @Data
    public static class CandidateInfo {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Text)
        private String fullName;

        @Field(type = FieldType.Keyword)
        private String email;

        @Field(type = FieldType.Keyword)
        private String phone;

        @Field(type = FieldType.Text)
        private String address;

        @Field(type = FieldType.Date)
        private LocalDate dateOfBirth;
    }

    @Data
    public static class EducationInfo {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Text)
        private String degree;

        @Field(type = FieldType.Text)
        private String institution;

        @Field(type = FieldType.Date)
        private LocalDate startDate;

        @Field(type = FieldType.Date)
        private LocalDate endDate;
    }

    @Data
    public static class ExperienceInfo {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Text)
        private String companyName;

        @Field(type = FieldType.Text)
        private String positionTitle;

        @Field(type = FieldType.Text)
        private String description;

        @Field(type = FieldType.Date)
        private LocalDate startDate;

        @Field(type = FieldType.Date)
        private LocalDate endDate;

        @Field(type = FieldType.Boolean)
        private Boolean isCurrentJob;

        @Field(type = FieldType.Keyword)
        private List<String> skills;

        @Field(type = FieldType.Keyword)
        private String industry;

        @Field(type = FieldType.Keyword)
        private String employmentType;

        @Field(type = FieldType.Text)
        private String location;

        @Field(type = FieldType.Text)
        private String achievements;

        @Field(type = FieldType.Text)
        private String technologiesUsed;

        @Field(type = FieldType.Integer)
        private Integer teamSize;

        @Field(type = FieldType.Keyword)
        private String verificationStatus;

        @Field(type = FieldType.Integer)
        private Integer durationInMonths;

        @Field(type = FieldType.Double)
        private Double durationInYears;

        @Field(type = FieldType.Boolean)
        private Boolean isVerified;

        @Field(type = FieldType.Boolean)
        private Boolean isRecentExperience;

        @Field(type = FieldType.Boolean)
        private Boolean isLongTermPosition;

        @Field(type = FieldType.Boolean)
        private Boolean hasManagementExperience;

        @Field(type = FieldType.Boolean)
        private Boolean isExecutivePosition;
    }
}