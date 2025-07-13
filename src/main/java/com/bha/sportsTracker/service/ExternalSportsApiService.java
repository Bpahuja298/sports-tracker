package com.bha.sportsTracker.service;

import com.bha.sportsTracker.entity.Match;
import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalSportsApiService {
    
    @Autowired
    private TennisApiService tennisApiService;
    
    public ExternalSportsApiService() {
        // Constructor
    }
    
    public List<Match> getLiveMatches() {
        try {
            System.out.println("🎾 Fetching live tennis matches from APIs...");
            
            // Use tennis API service to get real tennis data
            List<Match> apiMatches = tennisApiService.getLiveTennisMatches();
            
            if (!apiMatches.isEmpty()) {
                System.out.println("✅ Found " + apiMatches.size() + " live tennis matches from API");
                return apiMatches;
            }
            
            System.out.println("⚠️ No live matches found from APIs, using enhanced mock data");
            return getMockLiveTennisMatches();
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching live tennis matches: " + e.getMessage());
            return getMockLiveTennisMatches();
        }
    }
    
    public List<Match> getUpcomingMatches() {
        try {
            System.out.println("🎾 Fetching upcoming tennis matches from APIs...");
            
            // Use tennis API service to get real tennis data
            List<Match> apiMatches = tennisApiService.getUpcomingTennisMatches();
            
            if (!apiMatches.isEmpty()) {
                System.out.println("✅ Found " + apiMatches.size() + " upcoming tennis matches from API");
                return apiMatches;
            }
            
            System.out.println("⚠️ No upcoming matches found from APIs, using enhanced mock data");
            return getMockUpcomingTennisMatches();
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching upcoming tennis matches: " + e.getMessage());
            return getMockUpcomingTennisMatches();
        }
    }
    
    public List<Match> getMatchesByDate(int day, int month, int year) {
        try {
            System.out.println("🎾 Fetching tennis matches for date: " + day + "/" + month + "/" + year);
            
            // Use tennis API service to get matches for specific date
            List<Match> apiMatches = tennisApiService.getTennisMatchesByDate(day, month, year);
            
            if (!apiMatches.isEmpty()) {
                System.out.println("✅ Found " + apiMatches.size() + " tennis matches for date " + day + "/" + month + "/" + year);
                return apiMatches;
            }
            
            System.out.println("⚠️ No matches found for date " + day + "/" + month + "/" + year);
            return new ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("❌ Error fetching tennis matches for date: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Sport> getAvailableSports() {
        // Focus on tennis tournaments
        List<Sport> sports = new ArrayList<>();
        
        Sport wimbledon = new Sport();
        wimbledon.setName("Wimbledon");
        wimbledon.setDescription("The Championships, Wimbledon - The most prestigious tennis tournament");
        wimbledon.setIconUrl("🎾");
        wimbledon.setActive(true);
        
        Sport usOpen = new Sport();
        usOpen.setName("US Open");
        usOpen.setDescription("US Open Tennis Championships");
        usOpen.setIconUrl("🎾");
        usOpen.setActive(true);
        
        Sport frenchOpen = new Sport();
        frenchOpen.setName("French Open");
        frenchOpen.setDescription("Roland Garros - French Open Tennis");
        frenchOpen.setIconUrl("🎾");
        frenchOpen.setActive(true);
        
        Sport australianOpen = new Sport();
        australianOpen.setName("Australian Open");
        australianOpen.setDescription("Australian Open Tennis Championships");
        australianOpen.setIconUrl("🎾");
        australianOpen.setActive(true);
        
        Sport atpTour = new Sport();
        atpTour.setName("ATP Tour");
        atpTour.setDescription("ATP Tour professional tennis matches");
        atpTour.setIconUrl("🎾");
        atpTour.setActive(true);
        
        Sport wtaTour = new Sport();
        wtaTour.setName("WTA Tour");
        wtaTour.setDescription("WTA Tour professional women's tennis matches");
        wtaTour.setIconUrl("🎾");
        wtaTour.setActive(true);
        
        sports.add(wimbledon);
        sports.add(usOpen);
        sports.add(frenchOpen);
        sports.add(australianOpen);
        sports.add(atpTour);
        sports.add(wtaTour);
        
        return sports;
    }
    
    // Enhanced mock tennis data with more realistic current matches
    
    // Enhanced mock tennis data as fallback when scraping fails
    private List<Match> getMockLiveTennisMatches() {
        List<Match> matches = new ArrayList<>();
        
        // Match 1: Wimbledon Men's Final
        Match liveMatch1 = new Match();
        Team djokovic = new Team();
        djokovic.setName("Novak Djokovic");
        djokovic.setCountry("Serbia");
        
        Team alcaraz = new Team();
        alcaraz.setName("Carlos Alcaraz");
        alcaraz.setCountry("Spain");
        
        Sport tennis = new Sport();
        tennis.setName("Tennis");
        tennis.setIconUrl("🎾");
        
        liveMatch1.setHomeTeam(djokovic);
        liveMatch1.setAwayTeam(alcaraz);
        liveMatch1.setSport(tennis);
        liveMatch1.setHomeScore(2); // Sets won
        liveMatch1.setAwayScore(1); // Sets won
        liveMatch1.setStatus(Match.MatchStatus.LIVE);
        liveMatch1.setMatchDateTime(LocalDateTime.now().minusMinutes(95));
        liveMatch1.setVenue("Centre Court, Wimbledon");
        liveMatch1.setTournament("Wimbledon 2025");
        liveMatch1.setMatchSummary("🔥 Men's Singles Final - Set 4: Djokovic leads 5-4, serving for the championship!");
        
        matches.add(liveMatch1);
        
        // Match 2: WTA Live Match
        Match liveMatch2 = new Match();
        Team swiatek = new Team();
        swiatek.setName("Iga Swiatek");
        swiatek.setCountry("Poland");
        
        Team sabalenka = new Team();
        sabalenka.setName("Aryna Sabalenka");
        sabalenka.setCountry("Belarus");
        
        liveMatch2.setHomeTeam(swiatek);
        liveMatch2.setAwayTeam(sabalenka);
        liveMatch2.setSport(tennis);
        liveMatch2.setHomeScore(1); // Sets won
        liveMatch2.setAwayScore(1); // Sets won
        liveMatch2.setStatus(Match.MatchStatus.LIVE);
        liveMatch2.setMatchDateTime(LocalDateTime.now().minusMinutes(75));
        liveMatch2.setVenue("Court Philippe-Chatrier");
        liveMatch2.setTournament("French Open 2025");
        liveMatch2.setMatchSummary("🎾 Women's Semi-Final - Deciding set! Current: 3-2 Swiatek");
        
        matches.add(liveMatch2);
        
        // Match 3: ATP Masters Live
        Match liveMatch3 = new Match();
        Team sinner = new Team();
        sinner.setName("Jannik Sinner");
        sinner.setCountry("Italy");
        
        Team medvedev = new Team();
        medvedev.setName("Daniil Medvedev");
        medvedev.setCountry("Russia");
        
        liveMatch3.setHomeTeam(sinner);
        liveMatch3.setAwayTeam(medvedev);
        liveMatch3.setSport(tennis);
        liveMatch3.setHomeScore(1); // Sets won
        liveMatch3.setAwayScore(0); // Sets won
        liveMatch3.setStatus(Match.MatchStatus.LIVE);
        liveMatch3.setMatchDateTime(LocalDateTime.now().minusMinutes(45));
        liveMatch3.setVenue("Stadium Court");
        liveMatch3.setTournament("ATP Masters 1000 - Indian Wells");
        liveMatch3.setMatchSummary("⚡ Quarter-Final - Set 2: Sinner breaks serve, leads 4-2");
        
        matches.add(liveMatch3);
        
        return matches;
    }
    
    private List<Match> getMockUpcomingTennisMatches() {
        List<Match> matches = new ArrayList<>();
        
        Sport tennis = new Sport();
        tennis.setName("Tennis");
        tennis.setIconUrl("🎾");
        
        // Match 1: Wimbledon Women's Final
        Match upcomingMatch1 = new Match();
        Team gauff = new Team();
        gauff.setName("Coco Gauff");
        gauff.setCountry("USA");
        
        Team pegula = new Team();
        pegula.setName("Jessica Pegula");
        pegula.setCountry("USA");
        
        upcomingMatch1.setHomeTeam(gauff);
        upcomingMatch1.setAwayTeam(pegula);
        upcomingMatch1.setSport(tennis);
        upcomingMatch1.setStatus(Match.MatchStatus.SCHEDULED);
        upcomingMatch1.setMatchDateTime(LocalDateTime.now().plusHours(3));
        upcomingMatch1.setVenue("Centre Court, Wimbledon");
        upcomingMatch1.setTournament("Wimbledon 2025");
        upcomingMatch1.setMatchSummary("🏆 Women's Singles Final - All-American showdown!");
        
        matches.add(upcomingMatch1);
        
        // Match 2: ATP Masters Semi-Final
        Match upcomingMatch2 = new Match();
        Team rune = new Team();
        rune.setName("Holger Rune");
        rune.setCountry("Denmark");
        
        Team fritz = new Team();
        fritz.setName("Taylor Fritz");
        fritz.setCountry("USA");
        
        upcomingMatch2.setHomeTeam(rune);
        upcomingMatch2.setAwayTeam(fritz);
        upcomingMatch2.setSport(tennis);
        upcomingMatch2.setStatus(Match.MatchStatus.SCHEDULED);
        upcomingMatch2.setMatchDateTime(LocalDateTime.now().plusHours(5));
        upcomingMatch2.setVenue("Stadium Court");
        upcomingMatch2.setTournament("ATP Masters 1000 - Miami");
        upcomingMatch2.setMatchSummary("🎾 Semi-Final - Young guns battle for final spot");
        
        matches.add(upcomingMatch2);
        
        // Match 3: WTA Tournament
        Match upcomingMatch3 = new Match();
        Team vondrousova = new Team();
        vondrousova.setName("Marketa Vondrousova");
        vondrousova.setCountry("Czech Republic");
        
        Team ostapenko = new Team();
        ostapenko.setName("Jelena Ostapenko");
        ostapenko.setCountry("Latvia");
        
        upcomingMatch3.setHomeTeam(vondrousova);
        upcomingMatch3.setAwayTeam(ostapenko);
        upcomingMatch3.setSport(tennis);
        upcomingMatch3.setStatus(Match.MatchStatus.SCHEDULED);
        upcomingMatch3.setMatchDateTime(LocalDateTime.now().plusHours(8));
        upcomingMatch3.setVenue("Court 1");
        upcomingMatch3.setTournament("WTA 1000 - Indian Wells");
        upcomingMatch3.setMatchSummary("⚡ Quarter-Final - Former Grand Slam champions clash");
        
        matches.add(upcomingMatch3);
        
        // Match 4: ATP Next Gen
        Match upcomingMatch4 = new Match();
        Team musetti = new Team();
        musetti.setName("Lorenzo Musetti");
        musetti.setCountry("Italy");
        
        Team shelton = new Team();
        shelton.setName("Ben Shelton");
        shelton.setCountry("USA");
        
        upcomingMatch4.setHomeTeam(musetti);
        upcomingMatch4.setAwayTeam(shelton);
        upcomingMatch4.setSport(tennis);
        upcomingMatch4.setStatus(Match.MatchStatus.SCHEDULED);
        upcomingMatch4.setMatchDateTime(LocalDateTime.now().plusDays(1).plusHours(2));
        upcomingMatch4.setVenue("Court 2");
        upcomingMatch4.setTournament("ATP 500 - Barcelona");
        upcomingMatch4.setMatchSummary("🌟 Next Gen stars - Future of tennis on display");
        
        matches.add(upcomingMatch4);
        
        return matches;
    }
}