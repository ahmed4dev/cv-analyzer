package com.cvanalyzer.controllers;

import com.cvanalyzer.models.DashboardData;
import com.cvanalyzer.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/job/{jobId}")
    public ResponseEntity<DashboardData> getDashboardData(@PathVariable Long jobId) {
        try {
            DashboardData dashboard = dashboardService.generateDashboardData(jobId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}