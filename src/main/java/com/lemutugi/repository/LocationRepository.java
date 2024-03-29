package com.lemutugi.repository;

import com.lemutugi.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
