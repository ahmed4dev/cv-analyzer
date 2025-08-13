package com.cvanalyzer.cv_analyzer.services;

import com.cvanalyzer.cv_analyzer.models.CV;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CVService {
    CV saveCV(MultipartFile file);
    CV getCVById(Long id);
    List<CV> getAllCVs();
}
