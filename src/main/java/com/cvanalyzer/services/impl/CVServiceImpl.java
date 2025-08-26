package com.cvanalyzer.services.impl;


import com.cvanalyzer.models.CV;
import com.cvanalyzer.repositories.CVRepository;
import com.cvanalyzer.services.CVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CVServiceImpl implements CVService {

    @Autowired
    private CVRepository cvRepository;

    @Override
    public CV saveCV(MultipartFile file) {
        try {
            CV cv = new CV();
            cv.setFileName(file.getOriginalFilename());
            cv.setFileType(file.getContentType());
            cv.setData(file.getBytes());
            cv.setUploadDate(LocalDateTime.now());

            return cvRepository.save(cv);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store CV file", e);
        }
    }

    @Override
    public CV getCVById(Long id) {
        return cvRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CV not found"));
    }

    @Override
    public List<CV> getAllCVs() {
        return cvRepository.findAll();
    }
}
