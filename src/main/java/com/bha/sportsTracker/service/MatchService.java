package com.bha.sportsTracker.service;

import com.bha.sportsTracker.entity.Match;
import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.entity.Team;
import com.bha.sportsTracker.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    
    private final MatchRepository matchRepository;
    
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }
    
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }
    
    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }
    
    public List<Match> getLiveMatches() {
        return matchRepository.findLiveMatches();
    }
    
    public List<Match> getUpcomingMatches() {
        return matchRepository.findUpcomingMatches(LocalDateTime.now());
    }
    
    public List<Match> getUpcomingMatchesBySport(Sport sport) {
        return matchRepository.findUpcomingMatchesBySport(sport, LocalDateTime.now());
    }
    
    public List<Match> getMatchesByTeam(Team team) {
        return matchRepository.findByTeam(team);
    }
    
    public List<Match> getMatchesByTeamAndStatus(Team team, Match.MatchStatus status) {
        return matchRepository.findByTeamAndStatus(team, status);
    }
    
    public List<Match> getMatchesBySport(Sport sport) {
        return matchRepository.findBySportAndStatus(sport, Match.MatchStatus.LIVE);
    }
    
    public List<Match> getMatchesInDateRange(LocalDateTime start, LocalDateTime end) {
        return matchRepository.findByMatchDateTimeBetween(start, end);
    }
    
    public Match createMatch(Match match) {
        return matchRepository.save(match);
    }
    
    public Match updateMatch(Long id, Match matchDetails) {
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        
        match.setHomeTeam(matchDetails.getHomeTeam());
        match.setAwayTeam(matchDetails.getAwayTeam());
        match.setSport(matchDetails.getSport());
        match.setMatchDateTime(matchDetails.getMatchDateTime());
        match.setVenue(matchDetails.getVenue());
        match.setHomeScore(matchDetails.getHomeScore());
        match.setAwayScore(matchDetails.getAwayScore());
        match.setStatus(matchDetails.getStatus());
        match.setTournament(matchDetails.getTournament());
        match.setLiveStreamUrl(matchDetails.getLiveStreamUrl());
        match.setMatchSummary(matchDetails.getMatchSummary());
        
        return matchRepository.save(match);
    }
    
    public Match updateMatchScore(Long id, Integer homeScore, Integer awayScore) {
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        
        return matchRepository.save(match);
    }
    
    public Match updateMatchStatus(Long id, Match.MatchStatus status) {
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        
        match.setStatus(status);
        
        return matchRepository.save(match);
    }
    
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }
}