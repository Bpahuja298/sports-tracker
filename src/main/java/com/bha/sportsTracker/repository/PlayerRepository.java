package com.bha.sportsTracker.repository;

import com.bha.sportsTracker.entity.Player;
import com.bha.sportsTracker.entity.Team;
import com.bha.sportsTracker.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    List<Player> findByActiveTrue();
    
    List<Player> findByTeamAndActiveTrue(Team team);
    
    List<Player> findBySportAndActiveTrue(Sport sport);
    
    List<Player> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    
    List<Player> findByNationalityIgnoreCaseAndActiveTrue(String nationality);
}