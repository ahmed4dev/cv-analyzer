package com.cvanalyzer.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PDFParser {

    public String parsePDF(byte[] pdfData) throws IOException {
        try (PDDocument document = PDDocument.load(pdfData)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
