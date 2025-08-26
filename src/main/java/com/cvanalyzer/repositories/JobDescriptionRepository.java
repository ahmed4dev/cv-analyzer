package com.cvanalyzer.repositories;

import com.cvanalyzer.models.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    // Recherche par titre (insensible à la casse)
    List<JobDescription> findByTitleContainingIgnoreCase(String title);

    // Recherche par département
    List<JobDescription> findByDepartment(String department);

    // Recherche par nom d'entreprise (insensible à la casse)
    List<JobDescription> findByCompanyNameContainingIgnoreCase(String companyName);

    // Recherche par industrie
    List<JobDescription> findByIndustry(String industry);

    // Recherche par statut actif/inactif
    List<JobDescription> findByIsActive(Boolean isActive);

    // Recherche par type de travail
    List<JobDescription> findByWorkType(String workType);

    // Recherche par type d'emploi
    List<JobDescription> findByEmploymentType(String employmentType);

    // Recherche par localisation (insensible à la casse)
    List<JobDescription> findByLocationContainingIgnoreCase(String location);

    // Recherche par expérience requise maximum
    List<JobDescription> findByRequiredExperienceYearsLessThanEqual(Integer years);

    // Recherche par créateur
    List<JobDescription> findByCreatedBy(Long userId);

    // Recherche par plage de salaire
    @Query("SELECT j FROM JobDescription j WHERE j.minSalary >= :minSalary AND j.maxSalary <= :maxSalary")
    List<JobDescription> findBySalaryRange(@Param("minSalary") Double minSalary,
                                           @Param("maxSalary") Double maxSalary);

    // Recherche par mots-clés dans le titre ou la description
    @Query("SELECT j FROM JobDescription j WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<JobDescription> searchByKeyword(@Param("keyword") String keyword);

    // Recherche par plage d'expérience
    @Query("SELECT j FROM JobDescription j WHERE j.requiredExperienceYears BETWEEN :minExp AND :maxExp")
    List<JobDescription> findByExperienceRange(@Param("minExp") Integer minExp,
                                               @Param("maxExp") Integer maxExp);

    // Recherche par ID avec statut actif
    Optional<JobDescription> findByIdAndIsActive(Long id, Boolean isActive);

    // Compter les job descriptions actifs/inactifs
    long countByIsActive(Boolean isActive);

    // Trier par date de publication (décroissant)
    @Query("SELECT j FROM JobDescription j ORDER BY j.postingDate DESC")
    List<JobDescription> findAllOrderByPostingDateDesc();

    // Trier par date de création (décroissant)
    @Query("SELECT j FROM JobDescription j ORDER BY j.createdAt DESC")
    List<JobDescription> findAllOrderByCreatedAtDesc();

    // Recherche avancée avec multiples critères
    @Query("SELECT j FROM JobDescription j WHERE " +
            "(:department IS NULL OR j.department = :department) AND " +
            "(:workType IS NULL OR j.workType = :workType) AND " +
            "(:employmentType IS NULL OR j.employmentType = :employmentType) AND " +
            "(:minExperience IS NULL OR j.requiredExperienceYears >= :minExperience) AND " +
            "(:maxExperience IS NULL OR j.requiredExperienceYears <= :maxExperience) AND " +
            "(:minSalary IS NULL OR j.minSalary >= :minSalary) AND " +
            "(:maxSalary IS NULL OR j.maxSalary <= :maxSalary) AND " +
            "j.isActive = true")
    List<JobDescription> advancedSearch(@Param("department") String department,
                                        @Param("workType") String workType,
                                        @Param("employmentType") String employmentType,
                                        @Param("minExperience") Integer minExperience,
                                        @Param("maxExperience") Integer maxExperience,
                                        @Param("minSalary") Double minSalary,
                                        @Param("maxSalary") Double maxSalary);

    // Pagination des job descriptions actifs
    Page<JobDescription> findByIsActiveTrue(Pageable pageable);

    // Trouver les job descriptions avec des compétences spécifiques
/*    @Query("SELECT j FROM JobDescription j WHERE " +
            "EXISTS (SELECT 1 FROM j.requiredSkills rs WHERE LOWER(rs.key) LIKE LOWER(CONCAT('%', :skill, '%')))")*/

    @Query("SELECT jd FROM JobDescription jd JOIN jd.skills s WHERE s.name = :skill")

    List<JobDescription> findBySkill(@Param("skill") String skill);

    // Compter les job descriptions par département
    @Query("SELECT j.department, COUNT(j) FROM JobDescription j WHERE j.isActive = true GROUP BY j.department")
    List<Object[]> countByDepartment();

    // Compter les job descriptions par type de travail
    @Query("SELECT j.workType, COUNT(j) FROM JobDescription j WHERE j.isActive = true GROUP BY j.workType")
    List<Object[]> countByWorkType();

    // Mettre à jour le statut actif
    @Query("UPDATE JobDescription j SET j.isActive = :isActive WHERE j.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("isActive") Boolean isActive);

    // Vérifier l'existence d'un titre
    boolean existsByTitleAndCompanyName(String title, String companyName);

    // Trouver les job descriptions similaires
    @Query("SELECT j FROM JobDescription j WHERE " +
            "j.id != :excludeId AND " +
            "(j.department = :department OR j.industry = :industry) AND " +
            "j.isActive = true " +
            "ORDER BY j.postingDate DESC")
    List<JobDescription> findSimilarJobs(@Param("excludeId") Long excludeId,
                                         @Param("department") String department,
                                         @Param("industry") String industry,
                                         Pageable pageable);
}