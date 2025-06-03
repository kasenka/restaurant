package org.example.repository;

import org.example.model.Reservation;
import org.example.model.Subordination;
import org.example.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubordinationRepository extends JpaRepository<Subordination, Long> {

    @Query("SELECT s.subordinate " +
            "FROM Subordination s " +
            "WHERE s.supervisor = :admin")
    List<Worker> findAllSubordinationByWorker (@Param("admin") Worker admin);

    @Query("SELECT s.id  " +
            "FROM Subordination s " +
            "WHERE s.supervisor = :admin AND s.subordinate = :manager")
    Long findSubordinationByManager (@Param("admin") Worker admin,@Param("manager") Worker manager);
}