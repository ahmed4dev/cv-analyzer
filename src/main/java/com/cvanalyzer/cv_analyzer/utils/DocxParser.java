package com.cvanalyzer.cv_analyzer.utils;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class DocxParser {

    public String parseDocx(byte[] docxData) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docxData))) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }
}
