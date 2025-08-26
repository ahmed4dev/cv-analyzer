package com.cvanalyzer.controllers;


import com.cvanalyzer.models.CV;
import com.cvanalyzer.services.CVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cv")
public class CVController {

    @Autowired
    private CVService cvService;

    @PostMapping("/upload")
    public ResponseEntity<CV> uploadCV(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(cvService.saveCV(file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CV> getCV(@PathVariable Long id) {
        return ResponseEntity.ok(cvService.getCVById(id));
    }

    @GetMapping
    public ResponseEntity<List<CV>> getAllCVs() {
        return ResponseEntity.ok(cvService.getAllCVs());
    }
}