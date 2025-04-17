package org.example.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "Subordination")
public class Subordination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Worker supervisor;

    @OneToOne
    @JoinColumn(name = "subordinate_id", nullable = false)
    private Worker subordinate;
}
