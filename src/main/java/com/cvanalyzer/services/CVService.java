package com.cvanalyzer.services;

import com.cvanalyzer.models.CV;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CVService {
    CV saveCV(MultipartFile file);
    CV getCVById(Long id);
    List<CV> getAllCVs();
}
