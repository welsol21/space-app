package com.example.spaceapp.repository;

import com.example.spaceapp.model.Planet;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlanetRepository extends JpaRepository<Planet, Long> {

    List<Planet> findByTypeIgnoreCase(String type);

    Optional<Planet> findByNameIgnoreCase(String name);

    @Query("select p.name from Planet p")
    List<String> findAllNames();
}
