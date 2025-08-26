package com.cvanalyzer.utils;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public class TextExtractor {
    public static String extractFromPdf(MultipartFile file) throws IOException, TikaException, SAXException {
        InputStream stream = file.getInputStream();
        ContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        Parser parser = new AutoDetectParser();
        parser.parse(stream, handler, metadata, new ParseContext());
        return handler.toString();
    }
}