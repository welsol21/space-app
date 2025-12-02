package com.example.universe.repository;

import com.example.universe.entity.Moon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoonRepository extends JpaRepository<Moon, Long> {
    List<Moon> findByPlanetNameIgnoreCase(String planetName);
    long countByPlanetId(Long planetId);
}
