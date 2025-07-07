package com.bha.sportsTracker.controller;

import com.bha.sportsTracker.entity.Match;
import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.entity.Team;
import com.bha.sportsTracker.service.ExternalSportsApiService;
import com.bha.sportsTracker.service.MatchService;
import com.bha.sportsTracker.service.SportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {
    
    private final MatchService matchService;
    private final SportService sportService;
    private final ExternalSportsApiService externalSportsApiService;
    
    public MatchController(MatchService matchService, SportService sportService, ExternalSportsApiService externalSportsApiService) {
        this.matchService = matchService;
        this.sportService = sportService;
        this.externalSportsApiService = externalSportsApiService;
    }
    
    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        return ResponseEntity.ok(matches);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return matchService.getMatchById(id)
            .map(match -> ResponseEntity.ok(match))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/live")
    public ResponseEntity<List<Match>> getLiveMatches() {
        // Use external API service for real-time live matches
        List<Match> liveMatches = externalSportsApiService.getLiveMatches();
        return ResponseEntity.ok(liveMatches);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Match>> getUpcomingMatches() {
        // Use external API service for real-time upcoming matches
        List<Match> upcomingMatches = externalSportsApiService.getUpcomingMatches();
        return ResponseEntity.ok(upcomingMatches);
    }
    
    @GetMapping("/upcoming/sport/{sportId}")
    public ResponseEntity<List<Match>> getUpcomingMatchesBySport(@PathVariable Long sportId) {
        return sportService.getSportById(sportId)
            .map(sport -> {
                List<Match> matches = matchService.getUpcomingMatchesBySport(sport);
                return ResponseEntity.ok(matches);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/sport/{sportId}")
    public ResponseEntity<List<Match>> getMatchesBySport(@PathVariable Long sportId) {
        return sportService.getSportById(sportId)
            .map(sport -> {
                List<Match> matches = matchService.getMatchesBySport(sport);
                return ResponseEntity.ok(matches);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody Match match) {
        try {
            Match createdMatch = matchService.createMatch(match);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMatch);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(@PathVariable Long id, @RequestBody Match match) {
        try {
            Match updatedMatch = matchService.updateMatch(id, match);
            return ResponseEntity.ok(updatedMatch);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/score")
    public ResponseEntity<Match> updateMatchScore(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> scoreUpdate) {
        try {
            Integer homeScore = scoreUpdate.get("homeScore");
            Integer awayScore = scoreUpdate.get("awayScore");
            Match updatedMatch = matchService.updateMatchScore(id, homeScore, awayScore);
            return ResponseEntity.ok(updatedMatch);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Match> updateMatchStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String statusStr = statusUpdate.get("status");
            Match.MatchStatus status = Match.MatchStatus.valueOf(statusStr.toUpperCase());
            Match updatedMatch = matchService.updateMatchStatus(id, status);
            return ResponseEntity.ok(updatedMatch);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        try {
            matchService.deleteMatch(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}