package com.bha.sportsTracker.service;

import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.repository.SportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SportService {
    
    private final SportRepository sportRepository;
    
    public SportService(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }
    
    public List<Sport> getAllActiveSports() {
        return sportRepository.findByActiveTrue();
    }
    
    public Optional<Sport> getSportById(Long id) {
        return sportRepository.findById(id);
    }
    
    public Optional<Sport> getSportByName(String name) {
        return sportRepository.findByNameIgnoreCase(name);
    }
    
    public Sport createSport(Sport sport) {
        if (sportRepository.existsByNameIgnoreCase(sport.getName())) {
            throw new RuntimeException("Sport with name '" + sport.getName() + "' already exists");
        }
        return sportRepository.save(sport);
    }
    
    public Sport updateSport(Long id, Sport sportDetails) {
        Sport sport = sportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sport not found with id: " + id));
        
        sport.setName(sportDetails.getName());
        sport.setDescription(sportDetails.getDescription());
        sport.setIconUrl(sportDetails.getIconUrl());
        sport.setActive(sportDetails.isActive());
        
        return sportRepository.save(sport);
    }
    
    public void deleteSport(Long id) {
        Sport sport = sportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sport not found with id: " + id));
        sport.setActive(false);
        sportRepository.save(sport);
    }
}