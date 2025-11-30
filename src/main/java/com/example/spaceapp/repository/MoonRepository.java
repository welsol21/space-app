package com.example.spaceapp.repository;

import com.example.spaceapp.model.Moon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoonRepository extends JpaRepository<Moon, Long> {
    List<Moon> findByPlanet_NameIgnoreCase(String planetName);
    long countByPlanet_Id(Long planetId);
}
