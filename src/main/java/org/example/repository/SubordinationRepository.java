package org.example.repository;

import org.example.model.Reservation;
import org.example.model.Subordination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubordinationRepository extends JpaRepository<Subordination, Long> {
}