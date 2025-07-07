package com.bha.sportsTracker.controller;

import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.service.ExternalSportsApiService;
import com.bha.sportsTracker.service.SportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@CrossOrigin(origins = "*")
public class SportController {
    
    private final SportService sportService;
    private final ExternalSportsApiService externalSportsApiService;
    
    public SportController(SportService sportService, ExternalSportsApiService externalSportsApiService) {
        this.sportService = sportService;
        this.externalSportsApiService = externalSportsApiService;
    }
    
    @GetMapping
    public ResponseEntity<List<Sport>> getAllSports() {
        // Use external API service for available sports
        List<Sport> sports = externalSportsApiService.getAvailableSports();
        return ResponseEntity.ok(sports);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Sport> getSportById(@PathVariable Long id) {
        return sportService.getSportById(id)
            .map(sport -> ResponseEntity.ok(sport))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Sport> getSportByName(@PathVariable String name) {
        return sportService.getSportByName(name)
            .map(sport -> ResponseEntity.ok(sport))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Sport> createSport(@RequestBody Sport sport) {
        try {
            Sport createdSport = sportService.createSport(sport);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSport);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Sport> updateSport(@PathVariable Long id, @RequestBody Sport sport) {
        try {
            Sport updatedSport = sportService.updateSport(id, sport);
            return ResponseEntity.ok(updatedSport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSport(@PathVariable Long id) {
        try {
            sportService.deleteSport(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}