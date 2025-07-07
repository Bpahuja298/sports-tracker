package com.bha.sportsTracker.repository;

import com.bha.sportsTracker.entity.Match;
import com.bha.sportsTracker.entity.Team;
import com.bha.sportsTracker.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findByStatus(Match.MatchStatus status);
    
    List<Match> findBySportAndStatus(Sport sport, Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE (m.homeTeam = :team OR m.awayTeam = :team)")
    List<Match> findByTeam(@Param("team") Team team);
    
    @Query("SELECT m FROM Match m WHERE (m.homeTeam = :team OR m.awayTeam = :team) AND m.status = :status")
    List<Match> findByTeamAndStatus(@Param("team") Team team, @Param("status") Match.MatchStatus status);
    
    List<Match> findByMatchDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT m FROM Match m WHERE m.matchDateTime >= :now ORDER BY m.matchDateTime ASC")
    List<Match> findUpcomingMatches(@Param("now") LocalDateTime now);
    
    @Query("SELECT m FROM Match m WHERE m.status = 'LIVE' ORDER BY m.matchDateTime ASC")
    List<Match> findLiveMatches();
    
    @Query("SELECT m FROM Match m WHERE m.sport = :sport AND m.matchDateTime >= :now ORDER BY m.matchDateTime ASC")
    List<Match> findUpcomingMatchesBySport(@Param("sport") Sport sport, @Param("now") LocalDateTime now);
}