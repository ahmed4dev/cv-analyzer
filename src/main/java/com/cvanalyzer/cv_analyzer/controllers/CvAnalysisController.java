package com.cvanalyzer.cv_analyzer.controllers;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.cvanalyzer.cv_analyzer.services.NLPProcessor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cv")
public class CvAnalysisController {

    private final NLPProcessor nlpProcessor;
    private final Tika tika = new Tika();

    public CvAnalysisController(NLPProcessor nlpProcessor) {
        this.nlpProcessor = nlpProcessor;
    }
    @Operation(
            summary = "Analyser un CV",
            description = "Analyse un fichier CV (PDF, DOCX...) pour extraire les compétences, expériences et formations."
    )
    @PostMapping(value ="/analyze" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> analyzeCv(@RequestParam("file") MultipartFile file) throws IOException, TikaException {
        String text = tika.parseToString(file.getInputStream());

        Map<String, Object> result = new HashMap<>();
        result.put("skills", nlpProcessor.extractSkills(text));
        result.put("experiences", nlpProcessor.extractExperiences(text));
        result.put("educations", nlpProcessor.extractEducations(text));

        return result;
    }
}