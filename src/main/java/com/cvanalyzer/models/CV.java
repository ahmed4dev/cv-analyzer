package com.cvanalyzer.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
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

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkExperience> experiences;

    @Lob
    private byte[] data;

    private LocalDateTime uploadDate;

    @Lob
    private String parsedText;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;                   // Données personnelles

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations;   // Formations

    @ElementCollection
    private Set<String> skills; // Compétences techniques

    @OneToOne(mappedBy = "cv", cascade = CascadeType.ALL)
    private AnalysisResult analysisResult;
}