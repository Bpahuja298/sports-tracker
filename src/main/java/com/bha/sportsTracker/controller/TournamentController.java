package com.bha.sportsTracker.controller;

import com.bha.sportsTracker.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Tournament Controller
 * 
 * REST API endpoints for tournament and event data.
 */
@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {
    
    @Autowired
    private TournamentService tournamentService;
    
    /**
     * Get all tennis tournaments
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getTournaments() {
        List<Map<String, Object>> tournaments = tournamentService.getTennisTournaments();
        return ResponseEntity.ok(tournaments);
    }
    
    /**
     * Get events for a specific tournament
     */
    @GetMapping("/{tournamentId}/events")
    public ResponseEntity<List<Map<String, Object>>> getTournamentEvents(@PathVariable String tournamentId) {
        List<Map<String, Object>> events = tournamentService.getTournamentEvents(tournamentId);
        return ResponseEntity.ok(events);
    }
}