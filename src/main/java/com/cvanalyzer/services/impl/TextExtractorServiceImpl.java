package com.cvanalyzer.services.impl;

import com.cvanalyzer.services.TextExtractorService;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
public class TextExtractorServiceImpl implements TextExtractorService {

    private static final Set<String> SUPPORTED_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private final Tika tika = new Tika();
    private final Parser parser = new AutoDetectParser();

    @Override
    public String extractContent(MultipartFile file) throws IOException {
        if (!isSupportedFileType(tika.detect(file.getBytes()))) {
            throw new IOException("Unsupported file type");
        }

        try (InputStream stream = file.getInputStream()) {
            BodyContentHandler handler = new BodyContentHandler(10 * 1024 * 1024); // 10MB
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(stream, handler, metadata, context);
            return handler.toString();
        } catch (TikaException | SAXException e) {
            throw new IOException("Text extraction failed", e);
        }
    }

    @Override
    public boolean isSupportedFileType(String mimeType) {
        return SUPPORTED_TYPES.contains(mimeType);
    }
}