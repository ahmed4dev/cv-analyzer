package com.cvanalyzer.controllers;

import com.cvanalyzer.models.CV;
import com.cvanalyzer.models.EsCvDocument;
import com.cvanalyzer.repositories.EsCvRepository;
import com.cvanalyzer.services.ElasticsearchSyncService;
import com.cvanalyzer.repositories.CVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final EsCvRepository esCvRepository;
    private final ElasticsearchSyncService elasticsearchSyncService;
    private final CVRepository cvRepository;

    @GetMapping("/skills")
    public ResponseEntity<List<EsCvDocument>> searchBySkills(@RequestParam List<String> skills) {
        List<EsCvDocument> results = esCvRepository.findBySkillsIn(skills);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/experience/years")
    public ResponseEntity<List<EsCvDocument>> searchByMinExperience(@RequestParam int minExperience) {
        List<EsCvDocument> results = esCvRepository.findByExperienceYearsGreaterThanEqual(minExperience);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/candidate/email")
    public ResponseEntity<List<EsCvDocument>> searchByCandidateEmail(@RequestParam String email) {
        List<EsCvDocument> results = esCvRepository.findByCandidateEmail(email);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/candidate/name")
    public ResponseEntity<List<EsCvDocument>> searchByCandidateName(@RequestParam String name) {
        List<EsCvDocument> results = esCvRepository.findByCandidateName(name);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/experience/company")
    public ResponseEntity<List<EsCvDocument>> searchByCompany(@RequestParam String company) {
        List<EsCvDocument> results = esCvRepository.findByExperienceCompany(company);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/experience/position")
    public ResponseEntity<List<EsCvDocument>> searchByPosition(@RequestParam String position) {
        List<EsCvDocument> results = esCvRepository.findByExperiencePosition(position);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/education/institution")
    public ResponseEntity<List<EsCvDocument>> searchByInstitution(@RequestParam String institution) {
        List<EsCvDocument> results = esCvRepository.findByEducationInstitution(institution);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/content")
    public ResponseEntity<Page<EsCvDocument>> searchByContent(@RequestParam String query,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EsCvDocument> results = esCvRepository.findByParsedTextContaining(query, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/experience/duration")
    public ResponseEntity<List<EsCvDocument>> searchByMinExperienceDuration(@RequestParam int minMonths) {
        List<EsCvDocument> results = esCvRepository.findByMinExperienceDuration(minMonths);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/experience/management")
    public ResponseEntity<List<EsCvDocument>> searchByManagementExperience() {
        List<EsCvDocument> results = esCvRepository.findByManagementExperience();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/experience/technology")
    public ResponseEntity<List<EsCvDocument>> searchByTechnology(@RequestParam String technology) {
        List<EsCvDocument> results = esCvRepository.findByTechnology(technology);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/upload-date-range")
    public ResponseEntity<List<EsCvDocument>> searchByUploadDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<EsCvDocument> results = esCvRepository.findByUploadDateBetween(start, end);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/candidate/birth-date-range")
    public ResponseEntity<List<EsCvDocument>> searchByBirthDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<EsCvDocument> results = esCvRepository.findByCandidateDateOfBirthBetween(start, end);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/advanced")
    public ResponseEntity<List<EsCvDocument>> advancedSearch(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) Integer minExperience) {

        if (query != null && minExperience != null) {
            return ResponseEntity.ok(esCvRepository.findByContentAndMinExperience(query, minExperience));
        } else if (skills != null && !skills.isEmpty()) {
            return ResponseEntity.ok(esCvRepository.findBySkills(skills));
        } else if (query != null) {
            return ResponseEntity.ok(esCvRepository.searchByContent(query, PageRequest.of(0, 50)).getContent());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats/top-skills")
    public ResponseEntity<Map<String, Long>> getTopSkills(@RequestParam(defaultValue = "10") int limit) {
        Iterable<EsCvDocument> allCVs = esCvRepository.findAll();

        Map<String, Long> skillCounts = new HashMap<>();

        for (EsCvDocument cv : allCVs) {
            if (cv.getSkills() != null) {
                for (String skill : cv.getSkills()) {
                    skillCounts.put(skill, skillCounts.getOrDefault(skill, 0L) + 1);
                }
            }
        }

        Map<String, Long> topSkills = skillCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));

        return ResponseEntity.ok(topSkills);
    }

    @PostMapping("/reindex")
    public ResponseEntity<String> reindexAll() {
        try {
            Iterable<CV> allCvs = cvRepository.findAll();
            elasticsearchSyncService.reindexAllCvs(allCvs);
            return ResponseEntity.ok("Reindexing completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Reindexing failed: " + e.getMessage());
        }
    }

    @PostMapping("/reindex/{cvId}")
    public ResponseEntity<String> reindexCv(@PathVariable Long cvId) {
        try {
            CV cv = cvRepository.findById(cvId)
                    .orElseThrow(() -> new RuntimeException("CV not found"));
            elasticsearchSyncService.reindexCv(cvId, cv);
            return ResponseEntity.ok("CV reindexed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Reindexing failed: " + e.getMessage());
        }
    }
}