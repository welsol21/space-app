INSERT INTO planet (id, name, type, radius_km, mass_kg, orbital_period_days) VALUES
  (1, 'Earth', 'Terrestrial', 6371, 5.972e24, 365.25),
  (2, 'Mars', 'Terrestrial', 3389.5, 6.39e23, 687),
  (3, 'Jupiter', 'Gas Giant', 69911, 1.898e27, 4332.59);

INSERT INTO moon (id, name, diameter_km, orbital_period_days, planet_id) VALUES
  (1, 'Moon', 3474.2, 27.3, 1),
  (2, 'Phobos', 22.2, 0.3, 2),
  (3, 'Deimos', 12.4, 1.3, 2),
  (4, 'Io', 3643.2, 1.8, 3);
