package com.bha.sportsTracker.repository;

import com.bha.sportsTracker.entity.Team;
import com.bha.sportsTracker.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    List<Team> findByActiveTrue();
    
    List<Team> findBySportAndActiveTrue(Sport sport);
    
    List<Team> findByCountryIgnoreCaseAndActiveTrue(String country);
    
    List<Team> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}