package com.cvanalyzer.cv_analyzer.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "cvs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    @Lob
    private byte[] data;

    private LocalDateTime uploadDate;

    @Lob
    private String parsedText;

    @ElementCollection
    private Set<String> skills; // Compétences techniques

    @OneToOne(mappedBy = "cv", cascade = CascadeType.ALL)
    private AnalysisResult analysisResult;
}