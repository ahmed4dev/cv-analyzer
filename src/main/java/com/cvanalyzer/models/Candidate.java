package com.cvanalyzer.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Entity // ✅ Obligatoire pour que Hibernate la reconnaisse
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;         // ex: "Jean Dupont"
    private String email;            // ex: "jean.dupont@example.com"
    private String phone;            // ex: "+33 6 12 34 56 78"
    private LocalDate dateOfBirth;   // ex: LocalDate.of(1990, 5, 15)

    private String address;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkExperience> workExperiences;
}
