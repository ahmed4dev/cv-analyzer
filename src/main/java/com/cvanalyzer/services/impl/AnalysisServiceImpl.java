package com.cvanalyzer.services.impl;

import com.cvanalyzer.models.AnalysisResult;
import com.cvanalyzer.models.CV;
import com.cvanalyzer.repositories.AnalysisRepository;
import com.cvanalyzer.repositories.CVRepository;
import com.cvanalyzer.services.AnalysisService;
import com.cvanalyzer.services.NLPProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private CVRepository cvRepository;

    @Autowired
    private NLPProcessor nlpProcessor;

    @Override
    @Transactional
    public AnalysisResult analyzeCV(Long cvId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found"));

        String textContent = extractTextFromCV(cv);
        AnalysisResult result = nlpProcessor.analyze(textContent);
        result.setCv(cv);

        return analysisRepository.save(result);
    }

    @Override
    public AnalysisResult getAnalysisById(Long id) {
        return analysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analysis not found"));
    }

    private String extractTextFromCV(CV cv) {
        // Implémentation réelle utilisant PDFParser ou DocxParser
        return "";
    }
}