package com.cvanalyzer.services;

import com.cvanalyzer.models.*;
import com.cvanalyzer.repositories.EsCvRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchSyncService {

    private final EsCvRepository esCvRepository;

    public void syncCvToElasticsearch(CV cv) {
        try {
            EsCvDocument esCvDocument = convertToEsDocument(cv);
            esCvRepository.save(esCvDocument);
            log.info("CV {} synchronized to Elasticsearch", cv.getId());
        } catch (Exception e) {
            log.error("Failed to sync CV {} to Elasticsearch: {}", cv.getId(), e.getMessage(), e);
        }
    }

    public void deleteCvFromElasticsearch(Long cvId) {
        try {
            esCvRepository.deleteById(cvId.toString());
            log.info("CV {} deleted from Elasticsearch", cvId);
        } catch (Exception e) {
            log.error("Failed to delete CV {} from Elasticsearch: {}", cvId, e.getMessage());
        }
    }

    private EsCvDocument convertToEsDocument(CV cv) {
        EsCvDocument document = new EsCvDocument();
        document.setId(cv.getId().toString());
        document.setCvId(cv.getId());
        document.setFileName(cv.getFileName());
        document.setFileType(cv.getFileType());
        document.setUploadDate(cv.getUploadDate());
        document.setParsedText(cv.getParsedText());

        if (cv.getSkills() != null) {
            document.setSkills(new ArrayList<>(cv.getSkills()));
        } else {
            document.setSkills(new ArrayList<>());
        }

        // Calculer les années d'expérience totales à partir des expériences
        document.setExperienceYears(calculateTotalExperienceYears(cv));
        document.setIndexedAt(LocalDateTime.now());

        // Convertir les informations du candidat
        if (cv.getCandidate() != null) {
            document.setCandidate(convertCandidateToEs(cv.getCandidate()));
        }

        // Convertir les formations
        if (cv.getEducations() != null && !cv.getEducations().isEmpty()) {
            document.setEducations(convertEducationsToEs(cv.getEducations()));
        }

        // Convertir les expériences professionnelles
        if (cv.getExperiences() != null && !cv.getExperiences().isEmpty()) {
            document.setExperiences(convertExperiencesToEs(cv.getExperiences()));
        }

        return document;
    }

    private Integer calculateTotalExperienceYears(CV cv) {
        if (cv.getExperiences() == null || cv.getExperiences().isEmpty()) {
            return 0;
        }

        // Calculer le total des mois d'expérience
        int totalMonths = cv.getExperiences().stream()
                .mapToInt(exp -> exp.getDurationInMonths() != null ? exp.getDurationInMonths() : 0)
                .sum();

        // Convertir en années (arrondi à l'entier inférieur)
        return totalMonths / 12;
    }

    private EsCvDocument.CandidateInfo convertCandidateToEs(Candidate candidate) {
        EsCvDocument.CandidateInfo candidateInfo = new EsCvDocument.CandidateInfo();
        candidateInfo.setId(candidate.getId());
        candidateInfo.setFullName(candidate.getFullName());
        candidateInfo.setEmail(candidate.getEmail());
        candidateInfo.setPhone(candidate.getPhone());
        candidateInfo.setAddress(candidate.getAddress());
        candidateInfo.setDateOfBirth(candidate.getDateOfBirth());
        return candidateInfo;
    }

    private List<EsCvDocument.EducationInfo> convertEducationsToEs(List<Education> educations) {
        return educations.stream().map(education -> {
            EsCvDocument.EducationInfo educationInfo = new EsCvDocument.EducationInfo();
            educationInfo.setId(education.getId());
            educationInfo.setDegree(education.getDegree());
            educationInfo.setInstitution(education.getInstitution());
            educationInfo.setStartDate(education.getStartDate());
            educationInfo.setEndDate(education.getEndDate());
            return educationInfo;
        }).collect(Collectors.toList());
    }

    private List<EsCvDocument.ExperienceInfo> convertExperiencesToEs(List<WorkExperience> experiences) {
        return experiences.stream().map(experience -> {
            EsCvDocument.ExperienceInfo experienceInfo = new EsCvDocument.ExperienceInfo();
            experienceInfo.setId(experience.getId());
            experienceInfo.setCompanyName(experience.getCompanyName());
            experienceInfo.setPositionTitle(experience.getPositionTitle());
            experienceInfo.setDescription(experience.getDescription());
            experienceInfo.setStartDate(experience.getStartDate());
            experienceInfo.setEndDate(experience.getEndDate());
            experienceInfo.setIsCurrentJob(experience.getIsCurrentJob());
            experienceInfo.setSkills(experience.getSkills());
            experienceInfo.setIndustry(experience.getIndustry());
            experienceInfo.setEmploymentType(experience.getEmploymentType());
            experienceInfo.setLocation(experience.getLocation());
            experienceInfo.setAchievements(experience.getAchievements());
            experienceInfo.setTechnologiesUsed(experience.getTechnologiesUsed());
            experienceInfo.setTeamSize(experience.getTeamSize());
            experienceInfo.setVerificationStatus(experience.getVerificationStatus());
            experienceInfo.setDurationInMonths(experience.getDurationInMonths());
            experienceInfo.setDurationInYears(experience.getDurationInYears());
            experienceInfo.setIsVerified(experience.isVerified());
            experienceInfo.setIsRecentExperience(experience.isRecentExperience());
            experienceInfo.setIsLongTermPosition(experience.isLongTermPosition());
            experienceInfo.setHasManagementExperience(experience.hasManagementExperience());
            experienceInfo.setIsExecutivePosition(experience.isExecutivePosition());
            return experienceInfo;
        }).collect(Collectors.toList());
    }

    public void reindexAllCvs(Iterable<CV> cvs) {
        log.info("Starting reindexing of all CVs to Elasticsearch");
        int count = 0;

        for (CV cv : cvs) {
            try {
                syncCvToElasticsearch(cv);
                count++;

                if (count % 100 == 0) {
                    log.info("Reindexed {} CVs so far", count);
                }
            } catch (Exception e) {
                log.error("Failed to reindex CV {}: {}", cv.getId(), e.getMessage());
            }
        }

        log.info("Completed reindexing of {} CVs", count);
    }

    public void reindexCv(Long cvId, CV cv) {
        try {
            syncCvToElasticsearch(cv);
            log.info("CV {} reindexed successfully", cvId);
        } catch (Exception e) {
            log.error("Failed to reindex CV {}: {}", cvId, e.getMessage());
        }
    }
}