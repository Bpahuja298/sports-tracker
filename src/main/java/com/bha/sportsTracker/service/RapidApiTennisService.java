package com.bha.sportsTracker.service;

import com.bha.sportsTracker.entity.Match;
import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.entity.Team;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RapidApiTennisService {

    private static final Logger logger = LoggerFactory.getLogger(RapidApiTennisService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // RapidAPI Tennis API configuration
    private static final String RAPIDAPI_HOST = "tennisapi1.p.rapidapi.com";
    private static final String RAPIDAPI_KEY = "16524b8396mshf23ce8404749be1p12d4ffjsn0b0116bee314";
    private static final String BASE_URL = "https://tennisapi1.p.rapidapi.com";

    public RapidApiTennisService() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("x-rapidapi-host", RAPIDAPI_HOST)
                .defaultHeader("x-rapidapi-key", RAPIDAPI_KEY)
                .defaultHeader("User-Agent", "SportsTracker/1.0")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Fetch live and recent tennis matches from RapidAPI
     */
    public List<Match> getLiveTennisMatches() {
        logger.info("🎾 Fetching live tennis matches from RapidAPI...");
        
        try {
            // Use current date (December 7, 2025)
            LocalDateTime now = LocalDateTime.now();
            int day = now.getDayOfMonth();
            int month = now.getMonthValue();
            int year = now.getYear();
            
            String endpoint = String.format("/api/tennis/events/%d/%d/%d", day, month, year);
            logger.info("🔗 Calling RapidAPI endpoint: {} (Date: {}/{}/{})", endpoint, day, month, year);
            
            String response = webClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null && !response.isEmpty()) {
                List<Match> matches = parseTennisMatches(response, true);
                if (!matches.isEmpty()) {
                    return matches;
                }
            }
            
            logger.warn("⚠️ No live matches found for current date, trying recent dates...");
            
            // Try previous few days if no matches found for today
            for (int daysBack = 1; daysBack <= 7; daysBack++) {
                LocalDateTime pastDate = now.minusDays(daysBack);
                int pastDay = pastDate.getDayOfMonth();
                int pastMonth = pastDate.getMonthValue();
                int pastYear = pastDate.getYear();
                
                String pastEndpoint = String.format("/api/tennis/events/%d/%d/%d", pastDay, pastMonth, pastYear);
                logger.info("🔗 Trying past date: {} (Date: {}/{}/{})", pastEndpoint, pastDay, pastMonth, pastYear);
                
                try {
                    String pastResponse = webClient.get()
                            .uri(pastEndpoint)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();
                    
                    if (pastResponse != null && !pastResponse.isEmpty()) {
                        List<Match> pastMatches = parseTennisMatches(pastResponse, true);
                        if (!pastMatches.isEmpty()) {
                            logger.info("✅ Found {} matches from {} days ago", pastMatches.size(), daysBack);
                            return pastMatches;
                        }
                    }
                } catch (Exception ex) {
                    logger.debug("No matches found for date {} days back", daysBack);
                }
            }
            
            logger.warn("⚠️ No matches found in recent dates, using fallback");
            return getEnhancedMockLiveMatches();

        } catch (Exception e) {
            logger.error("❌ Error fetching live tennis matches from RapidAPI: {}", e.getMessage());
            return getEnhancedMockLiveMatches();
        }
    }

    /**
     * Fetch upcoming tennis matches from RapidAPI
     */
    public List<Match> getUpcomingTennisMatches() {
        logger.info("🎾 Fetching upcoming tennis matches from RapidAPI...");
        
        try {
            // Try next few days for upcoming matches
            LocalDateTime now = LocalDateTime.now();
            
            for (int daysAhead = 1; daysAhead <= 7; daysAhead++) {
                LocalDateTime futureDate = now.plusDays(daysAhead);
                int day = futureDate.getDayOfMonth();
                int month = futureDate.getMonthValue();
                int year = futureDate.getYear();
                
                String endpoint = String.format("/api/tennis/events/%d/%d/%d", day, month, year);
                logger.info("🔗 Calling RapidAPI endpoint for upcoming: {} (Date: {}/{}/{})", endpoint, day, month, year);
                
                try {
                    String response = webClient.get()
                            .uri(endpoint)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

                    if (response != null && !response.isEmpty()) {
                        List<Match> matches = parseTennisMatches(response, false);
                        if (!matches.isEmpty()) {
                            logger.info("✅ Found {} upcoming matches for {} days ahead", matches.size(), daysAhead);
                            return matches;
                        }
                    }
                } catch (Exception ex) {
                    logger.debug("No upcoming matches found for {} days ahead", daysAhead);
                }
            }
            
            logger.warn("⚠️ No upcoming matches found, using fallback");
            return getEnhancedMockUpcomingMatches();

        } catch (Exception e) {
            logger.error("❌ Error fetching upcoming tennis matches from RapidAPI: {}", e.getMessage());
            return getEnhancedMockUpcomingMatches();
        }
    }

    /**
     * Fetch tennis matches for a specific date
     */
    public List<Match> getTennisMatchesByDate(int day, int month, int year) {
        logger.info("🎾 Fetching tennis matches for date: {}/{}/{}", day, month, year);
        
        try {
            String endpoint = String.format("/api/tennis/events/%d/%d/%d", day, month, year);
            logger.info("🔗 Calling RapidAPI endpoint: {}", endpoint);
            
            String response = webClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null && !response.isEmpty()) {
                List<Match> matches = parseTennisMatches(response, false);
                if (!matches.isEmpty()) {
                    logger.info("✅ Found {} matches for date {}/{}/{}", matches.size(), day, month, year);
                    return matches;
                }
            }
            
            logger.warn("⚠️ No matches found for date {}/{}/{}, using fallback data", day, month, year);
            return getEnhancedMockMatchesForDate(day, month, year);

        } catch (Exception e) {
            logger.error("❌ Error fetching tennis matches for date {}/{}/{}: {}", day, month, year, e.getMessage());
            logger.info("🎾 Using enhanced mock tennis matches as fallback for date {}/{}/{}", day, month, year);
            return getEnhancedMockMatchesForDate(day, month, year);
        }
    }

    /**
     * Generate enhanced mock tennis matches for a specific date
     */
    private List<Match> getEnhancedMockMatchesForDate(int day, int month, int year) {
        List<Match> matches = new ArrayList<>();
        
        // Determine if this is today's date to provide appropriate match types
        LocalDateTime requestedDate = LocalDateTime.of(year, month, day, 12, 0);
        LocalDateTime now = LocalDateTime.now();
        boolean isToday = requestedDate.toLocalDate().equals(now.toLocalDate());
        boolean isPast = requestedDate.isBefore(now);
        boolean isFuture = requestedDate.isAfter(now.plusDays(1));
        
        try {
            // Create sport
            Sport tennis = new Sport();
            tennis.setId(1L);
            tennis.setName("🎾 Wimbledon");
            tennis.setDescription("Tennis Championship");
            
            if (isToday) {
                // Today's date - mix of live and completed matches
                matches.addAll(createMockMatchesForDate(tennis, requestedDate, "LIVE", 3));
                matches.addAll(createMockMatchesForDate(tennis, requestedDate, "COMPLETED", 2));
            } else if (isPast) {
                // Past date - completed matches
                matches.addAll(createMockMatchesForDate(tennis, requestedDate, "COMPLETED", 5));
            } else if (isFuture) {
                // Future date - scheduled matches
                matches.addAll(createMockMatchesForDate(tennis, requestedDate, "SCHEDULED", 5));
            } else {
                // Default case - mix of matches
                matches.addAll(createMockMatchesForDate(tennis, requestedDate, "LIVE", 2));
                matches.addAll(createMockMatchesForDate(tennis, requestedDate, "COMPLETED", 2));
                matches.addAll(createMockMatchesForDate(tennis, requestedDate, "SCHEDULED", 1));
            }
            
            logger.info("✅ Generated {} enhanced mock matches for date {}/{}/{}", matches.size(), day, month, year);
            
        } catch (Exception e) {
            logger.error("❌ Error generating mock matches for date: {}", e.getMessage());
        }
        
        return matches;
    }

    /**
     * Create mock matches for a specific date and status
     */
    private List<Match> createMockMatchesForDate(Sport tennis, LocalDateTime matchDate, String status, int count) {
        List<Match> matches = new ArrayList<>();
        
        String[] players = {
            "Jannik Sinner", "Carlos Alcaraz", "Daniil Medvedev", "Alexander Zverev",
            "Casper Ruud", "Holger Rune", "Felix Auger-Aliassime", "Taylor Fritz",
            "Tommy Paul", "Grigor Dimitrov", "Ben Shelton", "Frances Tiafoe",
            "Sebastian Korda", "Lorenzo Musetti", "Alex de Minaur", "Ugo Humbert"
        };
        
        String[] venues = {
            "Centre Court", "Court 1", "Court 2", "Court 3", "Court 12", "Court 18"
        };
        
        for (int i = 0; i < count && i < players.length - 1; i += 2) {
            try {
                Match match = new Match();
                match.setId(System.currentTimeMillis() + i);
                match.setSport(tennis);
                
                // Create teams (players)
                Team homeTeam = new Team();
                homeTeam.setId(System.currentTimeMillis() + i * 10);
                homeTeam.setName(players[i]);
                homeTeam.setCountry("UK");
                
                Team awayTeam = new Team();
                awayTeam.setId(System.currentTimeMillis() + i * 10 + 1);
                awayTeam.setName(players[i + 1]);
                awayTeam.setCountry("UK");
                
                match.setHomeTeam(homeTeam);
                match.setAwayTeam(awayTeam);
                
                // Set match details
                match.setMatchDateTime(matchDate.plusHours(i * 2));
                match.setVenue(venues[i % venues.length]);
                
                // Set status and scores based on match type
                switch (status) {
                    case "LIVE":
                        match.setStatus(Match.MatchStatus.LIVE);
                        match.setHomeScore((int) (Math.random() * 3) + 1);
                        match.setAwayScore((int) (Math.random() * 3) + 1);
                        match.setMatchSummary("Set " + (match.getHomeScore() + match.getAwayScore()) + " in progress");
                        break;
                    case "COMPLETED":
                        match.setStatus(Match.MatchStatus.COMPLETED);
                        match.setHomeScore((int) (Math.random() * 2) + 2);
                        match.setAwayScore((int) (Math.random() * 2) + 1);
                        match.setMatchSummary("Match completed in " + (match.getHomeScore() + match.getAwayScore()) + " sets");
                        break;
                    case "SCHEDULED":
                        match.setStatus(Match.MatchStatus.SCHEDULED);
                        match.setHomeScore(0);
                        match.setAwayScore(0);
                        match.setMatchSummary("Scheduled match");
                        break;
                }
                
                matches.add(match);
                
            } catch (Exception e) {
                logger.warn("⚠️ Error creating mock match: {}", e.getMessage());
            }
        }
        
        return matches;
    }

    /**
     * Parse tennis matches from RapidAPI JSON response
     */
    private List<Match> parseTennisMatches(String jsonResponse, boolean isLive) {
        List<Match> matches = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode events = root.get("events");
            
            if (events != null && events.isArray()) {
                logger.info("📊 Found {} tennis events in RapidAPI response", events.size());
                
                for (JsonNode event : events) {
                    try {
                        Match match = parseRapidApiMatch(event, isLive);
                        if (match != null) {
                            matches.add(match);
                        }
                    } catch (Exception e) {
                        logger.warn("⚠️ Error parsing individual match: {}", e.getMessage());
                    }
                }
                
                logger.info("✅ Successfully parsed {} tennis matches from RapidAPI", matches.size());
            } else {
                logger.warn("⚠️ No events array found in RapidAPI response");
            }
            
        } catch (Exception e) {
            logger.error("❌ Error parsing RapidAPI JSON response: {}", e.getMessage());
        }
        
        return matches.isEmpty() ? (isLive ? getEnhancedMockLiveMatches() : getEnhancedMockUpcomingMatches()) : matches;
    }

    /**
     * Parse individual match from RapidAPI event data
     */
    private Match parseRapidApiMatch(JsonNode event, boolean isLive) {
        try {
            // Extract basic match info
            Long matchId = event.has("id") ? event.get("id").asLong() : System.currentTimeMillis();
            
            // Extract tournament info
            String tournament = "Wimbledon";
            if (event.has("tournament") && event.get("tournament").has("name")) {
                tournament = event.get("tournament").get("name").asText();
            }
            
            // Extract players
            String homePlayerName = "Unknown Player";
            String awayPlayerName = "Unknown Player";
            
            if (event.has("homeTeam") && event.get("homeTeam").has("name")) {
                homePlayerName = event.get("homeTeam").get("name").asText();
            }
            if (event.has("awayTeam") && event.get("awayTeam").has("name")) {
                awayPlayerName = event.get("awayTeam").get("name").asText();
            }
            
            // Extract scores
            Integer homeScore = 0;
            Integer awayScore = 0;
            String scoreDetails = "";
            
            if (event.has("homeScore") && event.get("homeScore").has("current")) {
                homeScore = event.get("homeScore").get("current").asInt();
            }
            if (event.has("awayScore") && event.get("awayScore").has("current")) {
                awayScore = event.get("awayScore").get("current").asInt();
            }
            
            // Build score details with sets
            StringBuilder scoreBuilder = new StringBuilder();
            if (event.has("homeScore") && event.has("awayScore")) {
                JsonNode homeScoreNode = event.get("homeScore");
                JsonNode awayScoreNode = event.get("awayScore");
                
                // Add set scores
                for (int i = 1; i <= 5; i++) {
                    String setProp = "period" + i;
                    if (homeScoreNode.has(setProp) && awayScoreNode.has(setProp)) {
                        int homeSet = homeScoreNode.get(setProp).asInt();
                        int awaySet = awayScoreNode.get(setProp).asInt();
                        if (homeSet > 0 || awaySet > 0) {
                            if (scoreBuilder.length() > 0) scoreBuilder.append(", ");
                            scoreBuilder.append(homeSet).append("-").append(awaySet);
                        }
                    }
                }
                
                // Add current game score if available
                if (homeScoreNode.has("point") && awayScoreNode.has("point")) {
                    String homePoint = homeScoreNode.get("point").asText();
                    String awayPoint = awayScoreNode.get("point").asText();
                    if (!homePoint.isEmpty() && !awayPoint.isEmpty()) {
                        if (scoreBuilder.length() > 0) scoreBuilder.append(" | ");
                        scoreBuilder.append(homePoint).append("-").append(awayPoint);
                    }
                }
            }
            scoreDetails = scoreBuilder.toString();
            
            // Determine match status with proper handling of retired matches
            Match.MatchStatus status = Match.MatchStatus.SCHEDULED;
            if (event.has("status")) {
                JsonNode statusNode = event.get("status");
                
                // Check for description field (like "Retired")
                if (statusNode.has("description")) {
                    String description = statusNode.get("description").asText().toLowerCase();
                    if (description.contains("retired")) {
                        status = Match.MatchStatus.COMPLETED;
                        logger.info("🏃‍♂️ Match marked as COMPLETED due to retirement: {}", description);
                    }
                }
                
                // Check status type
                if (statusNode.has("type")) {
                    String statusType = statusNode.get("type").asText().toLowerCase();
                    switch (statusType) {
                        case "finished":
                        case "ended":
                            status = Match.MatchStatus.COMPLETED;
                            break;
                        case "inprogress":
                        case "live":
                            // Only mark as LIVE if not retired
                            if (status != Match.MatchStatus.COMPLETED) {
                                status = Match.MatchStatus.LIVE;
                            }
                            break;
                        case "postponed":
                            status = Match.MatchStatus.POSTPONED;
                            break;
                        case "cancelled":
                            status = Match.MatchStatus.CANCELLED;
                            break;
                        case "notstarted":
                        case "scheduled":
                            status = Match.MatchStatus.SCHEDULED;
                            break;
                        default:
                            status = isLive ? Match.MatchStatus.LIVE : Match.MatchStatus.SCHEDULED;
                    }
                }
            } else {
                status = isLive ? Match.MatchStatus.LIVE : Match.MatchStatus.SCHEDULED;
            }
            
            // Skip retired matches from live matches display
            if (isLive && status == Match.MatchStatus.COMPLETED) {
                logger.info("⏭️ Skipping completed/retired match: {} vs {}", homePlayerName, awayPlayerName);
                return null;
            }
            
            // Extract match time
            LocalDateTime matchTime = LocalDateTime.now();
            if (event.has("startTimestamp")) {
                long timestamp = event.get("startTimestamp").asLong();
                matchTime = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochSecond(timestamp), 
                    ZoneId.systemDefault()
                );
            }
            
            // Create match entity
            Match match = new Match();
            match.setId(matchId);
            match.setHomeScore(homeScore);
            match.setAwayScore(awayScore);
            match.setStatus(status);
            match.setMatchDateTime(matchTime);
            match.setVenue(tournament);
            
            // Create sport
            Sport sport = new Sport();
            sport.setId(1L);
            sport.setName("🎾 " + tournament);
            match.setSport(sport);
            
            // Create teams (players)
            Team homeTeam = new Team();
            homeTeam.setId(matchId * 2);
            homeTeam.setName(homePlayerName);
            match.setHomeTeam(homeTeam);
            
            Team awayTeam = new Team();
            awayTeam.setId(matchId * 2 + 1);
            awayTeam.setName(awayPlayerName);
            match.setAwayTeam(awayTeam);
            
            // Add score details as description
            if (!scoreDetails.isEmpty()) {
                match.setMatchSummary(scoreDetails);
            }
            
            logger.debug("✅ Parsed match: {} vs {} ({}) - Status: {}", 
                homePlayerName, awayPlayerName, tournament, status);
            
            return match;
            
        } catch (Exception e) {
            logger.error("❌ Error parsing RapidAPI match: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Enhanced mock live matches as fallback
     */
    private List<Match> getEnhancedMockLiveMatches() {
        logger.info("🎾 Using enhanced mock live tennis matches as fallback");
        
        List<Match> matches = new ArrayList<>();
        
        // Create realistic live tennis matches with current active players (no retired players)
        String[][] liveMatches = {
            {"Jannik Sinner", "Alex de Minaur", "2", "1", "6-4, 4-6, 6-3", "LIVE"},
            {"Carlos Alcaraz", "Grigor Dimitrov", "1", "0", "6-2, 3-1", "LIVE"},
            {"Daniil Medvedev", "Stefanos Tsitsipas", "2", "0", "7-6, 6-4", "LIVE"},
            {"Alexander Zverev", "Casper Ruud", "1", "1", "6-7, 6-4", "LIVE"},
            {"Holger Rune", "Felix Auger-Aliassime", "0", "1", "3-6", "LIVE"}
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
        
        // Create realistic upcoming tennis matches with current active players (no retired players)
        String[][] upcomingMatches = {
            {"Novak Djokovic", "Taylor Fritz", "ATP Finals"},
            {"Stefanos Tsitsipas", "Alexander Zverev", "ATP Tour"},
            {"Daniil Medvedev", "Casper Ruud", "ATP Masters"},
            {"Jannik Sinner", "Holger Rune", "ATP Tour"},
            {"Felix Auger-Aliassime", "Cameron Norrie", "ATP 500"}
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