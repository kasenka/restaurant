package org.example.repository;

import org.example.model.MenuItem;
import org.example.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Optional<MenuItem> findByName(String name);

    @Query("SELECT m from MenuItem m " +
            "WHERE m.type = :type")
    List<MenuItem> filterByType(@Param("type") String type);

    @Query("SELECT m from MenuItem m " +
            "WHERE m.name LIKE CONCAT('%', :string, '%') OR " +
            "m.description LIKE CONCAT('%', :string, '%') OR " +
            "m.ingredients LIKE CONCAT('%', :string, '%')")
    List<MenuItem> search(@Param("string") String string);

    @Query("SELECT m FROM MenuItem m " +
            "WHERE m.type = :type AND " +
            "(m.name LIKE CONCAT('%', :string, '%') OR " +
            " m.description LIKE CONCAT('%', :string, '%') OR " +
            " m.ingredients LIKE CONCAT('%', :string, '%'))")
    List<MenuItem> filterAndSearch(@Param("type") String type, @Param("string") String string);


}