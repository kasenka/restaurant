package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    @Min(value = 1, message = "Минимум 1 человек")
    @Max(value = 20, message = "Кажется мы должны уделить Вам больше времни.\nПожалуйста, позвоните нам для оформления брони")
    private int amountOfPeople;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    private String comm;

}
