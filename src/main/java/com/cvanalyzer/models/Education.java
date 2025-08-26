package com.cvanalyzer.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "education")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String degree;           // ex: "Master en Informatique"
    private String institution;      // ex: "Université de Paris"
    private LocalDate startDate;
    private LocalDate endDate;

    /*@OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<WorkExperience> workExperiences;*/

    @ManyToOne
    @JoinColumn(name = "cv_id")
    private CV cv;
}
