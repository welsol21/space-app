package com.example.universe.repository;

import com.example.universe.entity.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanetRepository extends JpaRepository<Planet, Long> {
    List<Planet> findByTypeIgnoreCase(String type);
}
