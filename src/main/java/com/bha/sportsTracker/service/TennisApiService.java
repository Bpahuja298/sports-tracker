package com.bha.sportsTracker.service;

import com.bha.sportsTracker.entity.Match;
import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.entity.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TennisApiService {

    private static final Logger logger = LoggerFactory.getLogger(TennisApiService.class);

    @Autowired
    private RapidApiTennisService rapidApiTennisService;

    @Autowired
    private Tennis24ScrapingService tennis24ScrapingService;

    /**
     * Get live tennis matches from multiple sources with fallback
     */
    public List<Match> getLiveTennisMatches() {
        logger.info("🎾 Fetching live tennis matches from APIs...");
        
        try {
            // Primary source: RapidAPI Tennis
            List<Match> matches = rapidApiTennisService.getLiveTennisMatches();
            if (matches != null && !matches.isEmpty()) {
                logger.info("✅ Found {} live tennis matches from RapidAPI", matches.size());
                return matches;
            }
        } catch (Exception e) {
            logger.warn("⚠️ RapidAPI tennis failed: {}", e.getMessage());
        }
        
        try {
            // Secondary source: Tennis24 scraping
            List<Match> matches = tennis24ScrapingService.scrapeLiveTennisMatches();
            if (matches != null && !matches.isEmpty()) {
                logger.info("✅ Found {} live tennis matches from Tennis24", matches.size());
                return matches;
            }
        } catch (Exception e) {
            logger.warn("⚠️ Tennis24 scraping failed: {}", e.getMessage());
        }
        
        // Fallback: Enhanced mock data
        logger.info("🔄 Using enhanced mock tennis data as fallback");
        return getEnhancedMockLiveMatches();
    }

    /**
     * Get upcoming tennis matches from multiple sources with fallback
     */
    public List<Match> getUpcomingTennisMatches() {
        logger.info("🎾 Fetching upcoming tennis matches from APIs...");
        
        try {
            // Primary source: RapidAPI Tennis
            List<Match> matches = rapidApiTennisService.getUpcomingTennisMatches();
            if (matches != null && !matches.isEmpty()) {
                logger.info("✅ Found {} upcoming tennis matches from RapidAPI", matches.size());
                return matches;
            }
        } catch (Exception e) {
            logger.warn("⚠️ RapidAPI tennis upcoming failed: {}", e.getMessage());
        }
        
        try {
            // Secondary source: Tennis24 scraping
            List<Match> matches = tennis24ScrapingService.scrapeUpcomingTennisMatches();
            if (matches != null && !matches.isEmpty()) {
                logger.info("✅ Found {} upcoming tennis matches from Tennis24", matches.size());
                return matches;
            }
        } catch (Exception e) {
            logger.warn("⚠️ Tennis24 upcoming scraping failed: {}", e.getMessage());
        }
        
        // Fallback: Enhanced mock data
        logger.info("🔄 Using enhanced mock tennis data as fallback");
        return getEnhancedMockUpcomingMatches();
    }

    /**
     * Get tennis matches for a specific date from multiple sources with fallback
     */
    public List<Match> getTennisMatchesByDate(int day, int month, int year) {
        logger.info("🎾 Fetching tennis matches for date: {}/{}/{}", day, month, year);
        
        try {
            // Primary source: RapidAPI Tennis
            List<Match> matches = rapidApiTennisService.getTennisMatchesByDate(day, month, year);
            if (matches != null && !matches.isEmpty()) {
                logger.info("✅ Found {} tennis matches from RapidAPI for date {}/{}/{}", matches.size(), day, month, year);
                return matches;
            }
        } catch (Exception e) {
            logger.warn("⚠️ RapidAPI tennis failed for date {}/{}/{}: {}", day, month, year, e.getMessage());
        }
        
        // No fallback to scraping for specific dates as it's not reliable
        logger.info("⚠️ No matches found for date {}/{}/{}", day, month, year);
        return new ArrayList<>();
    }

    /**
     * Enhanced mock live matches as fallback
     */
    private List<Match> getEnhancedMockLiveMatches() {
        logger.info("🎾 Using enhanced mock live tennis matches as fallback");
        
        List<Match> matches = new ArrayList<>();
        
        // Create realistic live tennis matches based on RapidAPI structure
        String[][] liveMatches = {
            {"Karen Khachanov", "Kamil Majchrzak", "3", "0", "6-4, 6-2, 6-3", "COMPLETED"},
            {"Taylor Fritz", "Jordan Thompson", "1", "0", "6-1, 3-0", "LIVE"},
            {"Novak Djokovic", "Daniel Evans", "2", "1", "6-4, 4-6, 6-3", "LIVE"},
            {"Carlos Alcaraz", "Oliver Tarvet", "2", "0", "7-6, 6-4", "LIVE"},
            {"Botic Van De Zandschulp", "Alejandro Davidovich Fokina", "1", "1", "6-7, 6-4", "LIVE"},
            {"Iga Swiatek", "Aryna Sabalenka", "2", "1", "6-3, 4-6, 5-3", "LIVE"},
            {"Jannik Sinner", "Daniil Medvedev", "2", "0", "7-5, 6-4", "COMPLETED"},
            {"Coco Gauff", "Jessica Pegula", "1", "1", "6-4, 3-6", "LIVE"}
        };
        
        for (int i = 0; i < liveMatches.length; i++) {
            String[] matchData = liveMatches[i];
            
            Match match = new Match();
            match.setId((long) (i + 1000));
            match.setHomeScore(Integer.parseInt(matchData[2]));
            match.setAwayScore(Integer.parseInt(matchData[3]));
            match.setStatus(Match.MatchStatus.valueOf(matchData[5]));
            match.setMatchDateTime(LocalDateTime.now().minusHours(i));
            match.setVenue("Wimbledon");
            match.setMatchSummary(matchData[4]);
            
            // Create sport
            Sport sport = new Sport();
            sport.setId(1L);
            sport.setName("🎾 Wimbledon");
            match.setSport(sport);
            
            // Create teams (players)
            Team homeTeam = new Team();
            homeTeam.setId((long) (i * 2 + 2000));
            homeTeam.setName(matchData[0]);
            match.setHomeTeam(homeTeam);
            
            Team awayTeam = new Team();
            awayTeam.setId((long) (i * 2 + 2001));
            awayTeam.setName(matchData[1]);
            match.setAwayTeam(awayTeam);
            
            matches.add(match);
        }
        
        return matches;
    }

    /**
     * Enhanced mock upcoming matches as fallback
     */
    private List<Match> getEnhancedMockUpcomingMatches() {
        logger.info("🎾 Using enhanced mock upcoming tennis matches as fallback");
        
        List<Match> matches = new ArrayList<>();
        
        // Create realistic upcoming tennis matches
        String[][] upcomingMatches = {
            {"Rafael Nadal", "Andy Murray", "Wimbledon"},
            {"Stefanos Tsitsipas", "Alexander Zverev", "ATP Tour"},
            {"Daniil Medvedev", "Casper Ruud", "French Open"},
            {"Jannik Sinner", "Holger Rune", "Wimbledon"},
            {"Felix Auger-Aliassime", "Cameron Norrie", "ATP Tour"},
            {"Emma Raducanu", "Petra Kvitova", "Wimbledon"},
            {"Simona Halep", "Karolina Pliskova", "WTA Tour"},
            {"Ons Jabeur", "Elena Rybakina", "Wimbledon"}
        };
        
        for (int i = 0; i < upcomingMatches.length; i++) {
            String[] matchData = upcomingMatches[i];
            
            Match match = new Match();
            match.setId((long) (i + 3000));
            match.setHomeScore(0);
            match.setAwayScore(0);
            match.setStatus(Match.MatchStatus.SCHEDULED);
            match.setMatchDateTime(LocalDateTime.now().plusHours(i + 1));
            match.setVenue(matchData[2]);
            
            // Create sport
            Sport sport = new Sport();
            sport.setId(1L);
            sport.setName("🎾 " + matchData[2]);
            match.setSport(sport);
            
            // Create teams (players)
            Team homeTeam = new Team();
            homeTeam.setId((long) (i * 2 + 4000));
            homeTeam.setName(matchData[0]);
            match.setHomeTeam(homeTeam);
            
            Team awayTeam = new Team();
            awayTeam.setId((long) (i * 2 + 4001));
            awayTeam.setName(matchData[1]);
            match.setAwayTeam(awayTeam);
            
            matches.add(match);
        }
        
        return matches;
    }
}