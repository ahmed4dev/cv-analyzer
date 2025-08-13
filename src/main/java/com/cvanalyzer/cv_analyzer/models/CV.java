package com.cvanalyzer.cv_analyzer.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @OneToOne(mappedBy = "cv", cascade = CascadeType.ALL)
    private AnalysisResult analysisResult;
}