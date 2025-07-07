package com.bha.sportsTracker.repository;

import com.bha.sportsTracker.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    
    List<Sport> findByActiveTrue();
    
    Optional<Sport> findByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCase(String name);
}