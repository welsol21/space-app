package com.example.spaceapp.repository;

import com.example.spaceapp.model.Moon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoonRepository extends JpaRepository<Moon, Long> {

    List<Moon> findByPlanet_NameIgnoreCase(String planetName);

    long countByPlanet_Id(Long planetId);
}
