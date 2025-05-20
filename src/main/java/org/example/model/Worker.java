package org.example.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "restaurant_workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank()
    private String username;

    @Column(nullable = true)
    private String fullName;

    @Column(nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true)
    private String photo;

    @Column(name = "encrypted_password")
    @NotBlank()
    private String encryptedPassword;

    @NotBlank
    private String role;

    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subordination> subordinates;

    @OneToOne(mappedBy = "subordinate", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subordination supervisor;
}
