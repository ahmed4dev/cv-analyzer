package com.cvanalyzer.controllers;

import com.cvanalyzer.models.AnalysisResult;
import com.cvanalyzer.services.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @PostMapping("/{cvId}")
    public ResponseEntity<AnalysisResult> analyzeCV(@PathVariable Long cvId) {
        return ResponseEntity.ok(analysisService.analyzeCV(cvId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResult> getAnalysis(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getAnalysisById(id));
    }
}