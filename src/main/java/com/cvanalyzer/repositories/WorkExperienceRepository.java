package com.cvanalyzer.repositories;

import com.cvanalyzer.models.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {

    List<WorkExperience> findByCvId(Long cvId);

    List<WorkExperience> findByCompanyName(String companyName);

    List<WorkExperience> findByPositionTitleContaining(String positionTitle);

    List<WorkExperience> findByIndustry(String industry);

    List<WorkExperience> findByIsCurrentJobTrue();

    List<WorkExperience> findByStartDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT we FROM WorkExperience we WHERE we.cv.id = :cvId ORDER BY we.startDate DESC")
    List<WorkExperience> findRecentExperiencesByCv(@Param("cvId") Long cvId,  Pageable pageable);

    @Query("SELECT we FROM WorkExperience we WHERE we.cv.id = :cvId AND we.verificationStatus = 'VERIFIED'")
    List<WorkExperience> findVerifiedExperiencesByCv(@Param("cvId") Long cvId);

    @Query("SELECT DISTINCT we.companyName FROM WorkExperience we WHERE we.cv.id = :cvId")
    List<String> findDistinctCompaniesByCv(@Param("cvId") Long cvId);

    @Query("SELECT we FROM WorkExperience we WHERE we.cv.id = :cvId AND we.durationInMonths >= :minMonths")
    List<WorkExperience> findLongTermExperiences(@Param("cvId") Long cvId, @Param("minMonths") Integer minMonths);
}