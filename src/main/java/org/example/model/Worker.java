package org.example.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "encrypted_password")
    @NotBlank()
    private String encryptedPassword;

    @NotBlank
    private String role;

    @OneToMany(mappedBy = "supervisorId")
    private List<Subordination> subordinates;

    @OneToOne(mappedBy = "subordinateId")
    private Subordination supervisor;
}
