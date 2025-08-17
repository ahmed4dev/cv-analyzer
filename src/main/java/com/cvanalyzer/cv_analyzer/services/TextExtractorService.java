package com.cvanalyzer.cv_analyzer.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TextExtractorService {
    String extractContent(MultipartFile file) throws IOException;
    boolean isSupportedFileType(String mimeType);
}
