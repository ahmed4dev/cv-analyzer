package com.cvanalyzer.repositories;

import com.cvanalyzer.models.EsCvDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EsCvRepository extends ElasticsearchRepository<EsCvDocument, String> {

    // Recherche par compétences
    List<EsCvDocument> findBySkillsIn(List<String> skills);
    List<EsCvDocument> findBySkills(String skill);

    // Recherche par années d'expérience
    List<EsCvDocument> findByExperienceYearsGreaterThanEqual(Integer minExperience);

    // Recherche full-text
    Page<EsCvDocument> findByParsedTextContaining(String content, Pageable pageable);

    // Recherche par email du candidat
    @Query("{\"term\": {\"candidate.email\": \"?0\"}}")
    List<EsCvDocument> findByCandidateEmail(String email);

    // Recherche par nom du candidat
    @Query("{\"match\": {\"candidate.fullName\": \"?0\"}}")
    List<EsCvDocument> findByCandidateName(String name);

    // Recherche par entreprise
    @Query("{\"nested\": {\"path\": \"experiences\", \"query\": {\"match\": {\"experiences.companyName\": \"?0\"}}}}")
    List<EsCvDocument> findByExperienceCompany(String company);

    // Recherche par poste
    @Query("{\"nested\": {\"path\": \"experiences\", \"query\": {\"match\": {\"experiences.positionTitle\": \"?0\"}}}}")
    List<EsCvDocument> findByExperiencePosition(String position);

    // Recherche par établissement de formation
    @Query("{\"nested\": {\"path\": \"educations\", \"query\": {\"match\": {\"educations.institution\": \"?0\"}}}}")
    List<EsCvDocument> findByEducationInstitution(String institution);

    // Recherche avancée combinée
    @Query("{\"bool\": {\"must\": [{\"match\": {\"parsedText\": \"?0\"}}, {\"range\": {\"experienceYears\": {\"gte\": ?1}}}]}}")
    List<EsCvDocument> findByContentAndMinExperience(String content, Integer minExperience);

    @Query("{\"bool\": {\"must\": {\"terms\": {\"skills\": ?0}}}}")
    List<EsCvDocument> findBySkills(List<String> skills);

    @Query("{\"match\": {\"parsedText\": \"?0\"}}")
    Page<EsCvDocument> searchByContent(String query, Pageable pageable);

    // Recherche par durée d'expérience dans les postes
    @Query("{\"nested\": {\"path\": \"experiences\", \"query\": {\"range\": {\"experiences.durationInMonths\": {\"gte\": ?0}}}}}")
    List<EsCvDocument> findByMinExperienceDuration(int minMonths);

    // Recherche par postes de management
    @Query("{\"nested\": {\"path\": \"experiences\", \"query\": {\"term\": {\"experiences.hasManagementExperience\": true}}}}")
    List<EsCvDocument> findByManagementExperience();

    // Recherche par technologies
    @Query("{\"nested\": {\"path\": \"experiences\", \"query\": {\"match\": {\"experiences.technologiesUsed\": \"?0\"}}}}")
    List<EsCvDocument> findByTechnology(String technology);

    // Recherche par dates
    List<EsCvDocument> findByUploadDateBetween(LocalDate start, LocalDate end);

    @Query("{\"range\": {\"candidate.dateOfBirth\": {\"gte\": \"?0\", \"lte\": \"?1\"}}}")
    List<EsCvDocument> findByCandidateDateOfBirthBetween(LocalDate start, LocalDate end);
}