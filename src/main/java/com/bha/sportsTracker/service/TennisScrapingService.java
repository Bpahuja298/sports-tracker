package com.bha.sportsTracker.service;

import com.bha.sportsTracker.entity.Match;
import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.entity.Team;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TennisScrapingService {
    
    private static final String ESPN_TENNIS_URL = "https://www.espn.com/tennis/";
    private static final String BBC_SPORT_TENNIS_URL = "https://www.bbc.com/sport/tennis";
    private static final String ATP_TOUR_URL = "https://www.atptour.com/en/scores/current";
    private static final String BETSAPI_WIMBLEDON_URL = "https://betsapi.com/l/11071/Wimbledon";
    
    // Alternative tennis data sources
    private static final String TENNIS_EXPLORER_URL = "https://www.tennisexplorer.com/matches/";
    private static final String FLASHSCORE_TENNIS_URL = "https://www.flashscore.com/tennis/";
    private static final String TENNIS_ABSTRACT_URL = "https://www.tennisabstract.com/";
    private static final String LIVE_TENNIS_URL = "https://live-tennis.eu/en";
    
    public List<Match> scrapeLiveTennisMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            // Try multiple sources in parallel for better success rate
            matches.addAll(scrapeTennisExplorerMatches());
            
            if (matches.isEmpty()) {
                matches.addAll(scrapeFlashscoreTennisMatches());
            }
            
            if (matches.isEmpty()) {
                matches.addAll(scrapeLiveTennisEuMatches());
            }
            
            // Try BetsAPI Wimbledon with enhanced approach
            if (matches.isEmpty()) {
                matches.addAll(scrapeBetsAPIWimbledonMatches());
            }
            
            // If no matches from alternative sources, try ESPN
            if (matches.isEmpty()) {
                matches.addAll(scrapeESPNLiveMatches());
            }
            
            // If no matches from ESPN, try BBC Sport
            if (matches.isEmpty()) {
                matches.addAll(scrapeBBCLiveMatches());
            }
            
            // If still no matches, try ATP Tour
            if (matches.isEmpty()) {
                matches.addAll(scrapeATPLiveMatches());
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping live tennis matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    public List<Match> scrapeUpcomingTennisMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            // Try ESPN first
            matches.addAll(scrapeESPNUpcomingMatches());
            
            // If no matches from ESPN, try BBC Sport
            if (matches.isEmpty()) {
                matches.addAll(scrapeBBCUpcomingMatches());
            }
            
            // If still no matches, try ATP Tour
            if (matches.isEmpty()) {
                matches.addAll(scrapeATPUpcomingMatches());
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping upcoming tennis matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeESPNLiveMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(ESPN_TENNIS_URL + "scoreboard")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // Look for live match containers
            Elements liveMatches = doc.select(".ScoreCell, .game-status, .live-indicator");
            Elements matchContainers = doc.select(".Table__TR, .scoreboard-row, .match-row");
            
            for (Element matchElement : matchContainers) {
                try {
                    // Check if match is live
                    Elements statusElements = matchElement.select(".game-status, .status, .live");
                    boolean isLive = false;
                    
                    for (Element statusEl : statusElements) {
                        String statusText = statusEl.text().toLowerCase();
                        if (statusText.contains("live") || statusText.contains("in progress") || 
                            statusText.contains("set") || statusText.matches(".*\\d+-\\d+.*")) {
                            isLive = true;
                            break;
                        }
                    }
                    
                    if (!isLive) continue;
                    
                    Match match = parseESPNMatch(matchElement, Match.MatchStatus.LIVE);
                    if (match != null) {
                        matches.add(match);
                    }
                } catch (Exception e) {
                    // Continue with next match if one fails
                    continue;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping ESPN live matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeESPNUpcomingMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(ESPN_TENNIS_URL + "schedule")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements matchContainers = doc.select(".Table__TR, .schedule-row, .match-row");
            
            for (Element matchElement : matchContainers) {
                try {
                    // Check if match is upcoming
                    Elements timeElements = matchElement.select(".game-time, .time, .schedule-time");
                    boolean isUpcoming = false;
                    
                    for (Element timeEl : timeElements) {
                        String timeText = timeEl.text().toLowerCase();
                        if (timeText.matches(".*\\d+:\\d+.*") || timeText.contains("pm") || 
                            timeText.contains("am") || timeText.contains("today") || 
                            timeText.contains("tomorrow")) {
                            isUpcoming = true;
                            break;
                        }
                    }
                    
                    if (!isUpcoming) continue;
                    
                    Match match = parseESPNMatch(matchElement, Match.MatchStatus.SCHEDULED);
                    if (match != null) {
                        matches.add(match);
                    }
                } catch (Exception e) {
                    // Continue with next match if one fails
                    continue;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping ESPN upcoming matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeBBCLiveMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(BBC_SPORT_TENNIS_URL + "/live-scores")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements liveMatches = doc.select(".sp-c-fixture, .live-match, .match-fixture");
            
            for (Element matchElement : liveMatches) {
                try {
                    // Check for live indicators
                    Elements liveIndicators = matchElement.select(".sp-c-fixture__status--live, .live, .in-play");
                    if (liveIndicators.isEmpty()) continue;
                    
                    Match match = parseBBCMatch(matchElement, Match.MatchStatus.LIVE);
                    if (match != null) {
                        matches.add(match);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping BBC live matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeBBCUpcomingMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(BBC_SPORT_TENNIS_URL + "/fixtures")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements upcomingMatches = doc.select(".sp-c-fixture, .fixture-match, .match-fixture");
            
            for (Element matchElement : upcomingMatches) {
                try {
                    // Check for upcoming time indicators
                    Elements timeElements = matchElement.select(".sp-c-fixture__date, .fixture-time, .match-time");
                    if (timeElements.isEmpty()) continue;
                    
                    Match match = parseBBCMatch(matchElement, Match.MatchStatus.SCHEDULED);
                    if (match != null) {
                        matches.add(match);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping BBC upcoming matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeATPLiveMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(ATP_TOUR_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements liveMatches = doc.select(".scores-results-content, .live-score, .match-score");
            
            for (Element matchElement : liveMatches) {
                try {
                    // Look for live indicators
                    if (matchElement.text().toLowerCase().contains("live") || 
                        matchElement.select(".live, .in-progress").size() > 0) {
                        
                        Match match = parseATPMatch(matchElement, Match.MatchStatus.LIVE);
                        if (match != null) {
                            matches.add(match);
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping ATP live matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeATPUpcomingMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(ATP_TOUR_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Elements upcomingMatches = doc.select(".scores-results-content, .upcoming-match, .match-schedule");
            
            for (Element matchElement : upcomingMatches) {
                try {
                    // Look for time indicators
                    if (matchElement.text().matches(".*\\d+:\\d+.*") || 
                        matchElement.select(".match-time, .start-time").size() > 0) {
                        
                        Match match = parseATPMatch(matchElement, Match.MatchStatus.SCHEDULED);
                        if (match != null) {
                            matches.add(match);
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error scraping ATP upcoming matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private Match parseESPNMatch(Element matchElement, Match.MatchStatus status) {
        try {
            Match match = new Match();
            
            // Extract player names
            Elements playerElements = matchElement.select(".Table__TD .AnchorLink, .team-name, .competitor-name");
            if (playerElements.size() < 2) {
                // Try alternative selectors
                playerElements = matchElement.select("a[href*='player'], .player-name, .athlete-name");
            }
            
            if (playerElements.size() >= 2) {
                Team player1 = new Team();
                player1.setName(cleanPlayerName(playerElements.get(0).text()));
                player1.setCountry(extractCountryFromName(player1.getName()));
                
                Team player2 = new Team();
                player2.setName(cleanPlayerName(playerElements.get(1).text()));
                player2.setCountry(extractCountryFromName(player2.getName()));
                
                match.setHomeTeam(player1);
                match.setAwayTeam(player2);
            } else {
                return null; // Skip if we can't get player names
            }
            
            // Extract tournament info
            Elements tournamentElements = matchElement.select(".tournament-name, .event-name, .competition-name");
            if (!tournamentElements.isEmpty()) {
                match.setTournament(tournamentElements.first().text());
            } else {
                match.setTournament("ATP/WTA Tour");
            }
            
            // Extract scores if available
            Elements scoreElements = matchElement.select(".score, .game-score, .set-score");
            if (scoreElements.size() >= 2) {
                try {
                    String score1 = scoreElements.get(0).text().replaceAll("[^0-9]", "");
                    String score2 = scoreElements.get(1).text().replaceAll("[^0-9]", "");
                    if (!score1.isEmpty()) match.setHomeScore(Integer.parseInt(score1));
                    if (!score2.isEmpty()) match.setAwayScore(Integer.parseInt(score2));
                } catch (NumberFormatException e) {
                    // Scores are optional
                }
            }
            
            // Set tennis sport
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            match.setStatus(status);
            match.setMatchDateTime(LocalDateTime.now());
            match.setVenue("Tennis Court");
            
            if (status == Match.MatchStatus.LIVE) {
                match.setMatchSummary("Live tennis match in progress");
            } else {
                match.setMatchSummary("Upcoming tennis match");
            }
            
            return match;
            
        } catch (Exception e) {
            System.err.println("Error parsing ESPN match: " + e.getMessage());
            return null;
        }
    }
    
    private Match parseBBCMatch(Element matchElement, Match.MatchStatus status) {
        try {
            Match match = new Match();
            
            // Extract player names from BBC structure
            Elements playerElements = matchElement.select(".sp-c-fixture__team-name, .team-name, .player-name");
            
            if (playerElements.size() >= 2) {
                Team player1 = new Team();
                player1.setName(cleanPlayerName(playerElements.get(0).text()));
                player1.setCountry(extractCountryFromName(player1.getName()));
                
                Team player2 = new Team();
                player2.setName(cleanPlayerName(playerElements.get(1).text()));
                player2.setCountry(extractCountryFromName(player2.getName()));
                
                match.setHomeTeam(player1);
                match.setAwayTeam(player2);
            } else {
                return null;
            }
            
            // Extract tournament
            Elements tournamentElements = matchElement.select(".sp-c-fixture__competition, .tournament, .event");
            if (!tournamentElements.isEmpty()) {
                match.setTournament(tournamentElements.first().text());
            } else {
                match.setTournament("Tennis Tournament");
            }
            
            // Set tennis sport
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            match.setStatus(status);
            match.setMatchDateTime(LocalDateTime.now());
            match.setVenue("Tennis Court");
            
            if (status == Match.MatchStatus.LIVE) {
                match.setMatchSummary("Live tennis match from BBC Sport");
            } else {
                match.setMatchSummary("Upcoming tennis match from BBC Sport");
            }
            
            return match;
            
        } catch (Exception e) {
            System.err.println("Error parsing BBC match: " + e.getMessage());
            return null;
        }
    }
    
    private Match parseATPMatch(Element matchElement, Match.MatchStatus status) {
        try {
            Match match = new Match();
            
            // Extract player names from ATP structure
            Elements playerElements = matchElement.select(".player-name, .competitor, .athlete");
            
            if (playerElements.size() >= 2) {
                Team player1 = new Team();
                player1.setName(cleanPlayerName(playerElements.get(0).text()));
                player1.setCountry(extractCountryFromName(player1.getName()));
                
                Team player2 = new Team();
                player2.setName(cleanPlayerName(playerElements.get(1).text()));
                player2.setCountry(extractCountryFromName(player2.getName()));
                
                match.setHomeTeam(player1);
                match.setAwayTeam(player2);
            } else {
                return null;
            }
            
            match.setTournament("ATP Tour");
            
            // Set tennis sport
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            match.setStatus(status);
            match.setMatchDateTime(LocalDateTime.now());
            match.setVenue("ATP Court");
            
            if (status == Match.MatchStatus.LIVE) {
                match.setMatchSummary("Live ATP match");
            } else {
                match.setMatchSummary("Upcoming ATP match");
            }
            
            return match;
            
        } catch (Exception e) {
            System.err.println("Error parsing ATP match: " + e.getMessage());
            return null;
        }
    }
    
    private String cleanPlayerName(String name) {
        if (name == null) return "";
        
        // Remove common prefixes/suffixes and clean up
        name = name.replaceAll("\\([^)]*\\)", ""); // Remove parentheses content
        name = name.replaceAll("\\d+", ""); // Remove numbers
        name = name.replaceAll("[^a-zA-Z\\s'-]", ""); // Keep only letters, spaces, hyphens, apostrophes
        name = name.trim();
        
        // Handle common name formats
        if (name.contains(",")) {
            String[] parts = name.split(",");
            if (parts.length >= 2) {
                name = parts[1].trim() + " " + parts[0].trim();
            }
        }
        
        return name;
    }
    
    private String extractCountryFromName(String playerName) {
        // Enhanced country extraction based on common tennis players
        if (playerName == null) return "Unknown";
        
        String name = playerName.toLowerCase();
        
        // Top current players
        if (name.contains("djokovic")) return "Serbia";
        if (name.contains("alcaraz")) return "Spain";
        if (name.contains("swiatek")) return "Poland";
        if (name.contains("gauff")) return "USA";
        if (name.contains("sinner")) return "Italy";
        if (name.contains("medvedev")) return "Russia";
        if (name.contains("rublev")) return "Russia";
        if (name.contains("tsitsipas")) return "Greece";
        if (name.contains("zverev")) return "Germany";
        if (name.contains("ruud")) return "Norway";
        if (name.contains("fritz")) return "USA";
        if (name.contains("hurkacz")) return "Poland";
        if (name.contains("rune")) return "Denmark";
        if (name.contains("sabalenka")) return "Belarus";
        if (name.contains("pegula")) return "USA";
        if (name.contains("jabeur")) return "Tunisia";
        if (name.contains("vondrousova")) return "Czech Republic";
        if (name.contains("ostapenko")) return "Latvia";
        if (name.contains("keys")) return "USA";
        
        // Tennis legends
        if (name.contains("federer")) return "Switzerland";
        if (name.contains("nadal")) return "Spain";
        if (name.contains("murray")) return "Great Britain";
        if (name.contains("williams")) return "USA";
        if (name.contains("sharapova")) return "Russia";
        
        return "Unknown";
    }
    
    private List<Match> scrapeBetsAPIWimbledonMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            System.out.println("🎾 Scraping real Wimbledon matches from BetsAPI...");
            
            // Enhanced anti-blocking measures
            Document doc = connectWithRetry(BETSAPI_WIMBLEDON_URL);
            if (doc == null) {
                System.err.println("❌ Failed to connect to BetsAPI after retries");
                return matches;
            }
            
            // Parse Results section (completed matches)
            matches.addAll(parseBetsAPIResultsSection(doc));
            
            // Parse Fixtures section (live and upcoming matches)
            matches.addAll(parseBetsAPIFixturesSection(doc));
            
            System.out.println("🎾 Found " + matches.size() + " real Wimbledon matches from BetsAPI");
            
        } catch (Exception e) {
            System.err.println("Error scraping BetsAPI Wimbledon matches: " + e.getMessage());
        }
        
        return matches;
    }
    
    private Document connectWithRetry(String url) {
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:121.0) Gecko/20100101 Firefox/121.0"
        };
        
        for (int attempt = 0; attempt < userAgents.length; attempt++) {
            try {
                System.out.println("🔄 Attempt " + (attempt + 1) + " to connect to BetsAPI...");
                
                // Add delay between attempts
                if (attempt > 0) {
                    Thread.sleep(2000 + (attempt * 1000)); // 2s, 3s, 4s, 5s, 6s delays
                }
                
                Document doc = Jsoup.connect(url)
                        .userAgent(userAgents[attempt])
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "en-US,en;q=0.5")
                        .header("Accept-Encoding", "gzip, deflate")
                        .header("DNT", "1")
                        .header("Connection", "keep-alive")
                        .header("Upgrade-Insecure-Requests", "1")
                        .header("Sec-Fetch-Dest", "document")
                        .header("Sec-Fetch-Mode", "navigate")
                        .header("Sec-Fetch-Site", "none")
                        .header("Cache-Control", "max-age=0")
                        .timeout(20000)
                        .followRedirects(true)
                        .get();
                
                System.out.println("✅ Successfully connected to BetsAPI on attempt " + (attempt + 1));
                return doc;
                
            } catch (Exception e) {
                System.err.println("❌ Attempt " + (attempt + 1) + " failed: " + e.getMessage());
                if (attempt == userAgents.length - 1) {
                    System.err.println("🚫 All connection attempts failed");
                }
            }
        }
        
        return null;
    }
    
    private List<Match> parseBetsAPIResultsSection(Document doc) {
        List<Match> matches = new ArrayList<>();
        
        try {
            // Look for Results section
            Elements resultsSections = doc.select("h3:contains(Results), .results-section, #results");
            
            for (Element resultsSection : resultsSections) {
                Element parentTable = resultsSection.nextElementSibling();
                if (parentTable != null && parentTable.tagName().equals("table")) {
                    Elements resultRows = parentTable.select("tr");
                    
                    for (Element row : resultRows) {
                        String rowText = row.text();
                        if (rowText.contains(" vs ") && rowText.length() > 10) {
                            Match match = parseBetsAPIResultMatch(row, rowText);
                            if (match != null) {
                                matches.add(match);
                                System.out.println("✅ Found completed match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + " (COMPLETED)");
                            }
                        }
                    }
                }
            }
            
            // Alternative selector for results
            if (matches.isEmpty()) {
                Elements allRows = doc.select("table tr");
                for (Element row : allRows) {
                    String rowText = row.text().toLowerCase();
                    if (rowText.contains(" vs ") && (rowText.contains("6-") || rowText.contains("7-") || rowText.contains("ret") || rowText.contains("w/o"))) {
                        Match match = parseBetsAPIResultMatch(row, row.text());
                        if (match != null) {
                            matches.add(match);
                            System.out.println("✅ Found completed match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + " (COMPLETED)");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing BetsAPI Results section: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> parseBetsAPIFixturesSection(Document doc) {
        List<Match> matches = new ArrayList<>();
        
        try {
            // Look for Fixtures section
            Elements fixturesSections = doc.select("h3:contains(Fixtures), .fixtures-section, #fixtures");
            
            for (Element fixturesSection : fixturesSections) {
                Element parentTable = fixturesSection.nextElementSibling();
                if (parentTable != null && parentTable.tagName().equals("table")) {
                    Elements fixtureRows = parentTable.select("tr");
                    
                    for (Element row : fixtureRows) {
                        String rowText = row.text();
                        if (rowText.contains(" vs ") && rowText.length() > 10) {
                            Match match = parseBetsAPIFixtureMatch(row, rowText);
                            if (match != null) {
                                matches.add(match);
                                String status = match.getStatus() == Match.MatchStatus.LIVE ? "LIVE" : "SCHEDULED";
                                System.out.println("✅ Found fixture match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + " (" + status + ")");
                            }
                        }
                    }
                }
            }
            
            // Alternative selector for fixtures
            if (matches.isEmpty()) {
                Elements allRows = doc.select("table tr");
                for (Element row : allRows) {
                    String rowText = row.text();
                    if (rowText.contains(" vs ") && rowText.length() > 10 && !isCompletedMatch(rowText)) {
                        Match match = parseBetsAPIFixtureMatch(row, rowText);
                        if (match != null) {
                            matches.add(match);
                            String status = match.getStatus() == Match.MatchStatus.LIVE ? "LIVE" : "SCHEDULED";
                            System.out.println("✅ Found fixture match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + " (" + status + ")");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parsing BetsAPI Fixtures section: " + e.getMessage());
        }
        
        return matches;
    }
    
    private boolean isCompletedMatch(String rowText) {
        String lowerText = rowText.toLowerCase();
        return lowerText.contains("6-") || lowerText.contains("7-") ||
               lowerText.contains("ret") || lowerText.contains("w/o") ||
               lowerText.contains("walkover") || lowerText.contains("retired");
    }
    
    private Match parseBetsAPIResultMatch(Element row, String rowText) {
        try {
            Match match = parseBetsAPIMatchBase(rowText);
            if (match == null) return null;
            
            // Set as finished match
            match.setStatus(Match.MatchStatus.COMPLETED);
            match.setMatchSummary("Completed Wimbledon match from BetsAPI");
            
            // Try to extract final scores
            Elements scoreCells = row.select("td");
            if (scoreCells.size() > 2) {
                String lastCellText = scoreCells.get(scoreCells.size() - 1).text();
                parseAndSetScores(match, lastCellText);
            }
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private Match parseBetsAPIFixtureMatch(Element row, String rowText) {
        try {
            Match match = parseBetsAPIMatchBase(rowText);
            if (match == null) return null;
            
            Elements cells = row.select("td");
            
            // Check if match has date (scheduled) or no date (live)
            boolean hasDate = false;
            boolean hasLiveScore = false;
            
            for (Element cell : cells) {
                String cellText = cell.text();
                
                // Check for date patterns (dd/mm, mm/dd, etc.)
                if (cellText.matches(".*\\d{1,2}[/\\-]\\d{1,2}.*") ||
                    cellText.matches(".*\\d{1,2}:\\d{2}.*") ||
                    cellText.toLowerCase().contains("jan") || cellText.toLowerCase().contains("feb") ||
                    cellText.toLowerCase().contains("mar") || cellText.toLowerCase().contains("apr") ||
                    cellText.toLowerCase().contains("may") || cellText.toLowerCase().contains("jun") ||
                    cellText.toLowerCase().contains("jul") || cellText.toLowerCase().contains("aug") ||
                    cellText.toLowerCase().contains("sep") || cellText.toLowerCase().contains("oct") ||
                    cellText.toLowerCase().contains("nov") || cellText.toLowerCase().contains("dec")) {
                    hasDate = true;
                }
                
                // Check for live scores in last column
                if (cellText.matches(".*\\d+-\\d+.*") && !hasDate) {
                    hasLiveScore = true;
                    parseAndSetScores(match, cellText);
                }
            }
            
            // Determine match status based on user's feedback
            if (!hasDate && hasLiveScore) {
                // No date + scores in last column = LIVE
                match.setStatus(Match.MatchStatus.LIVE);
                match.setMatchSummary("Live Wimbledon match from BetsAPI");
            } else if (!hasDate) {
                // No date + no scores = LIVE (just started)
                match.setStatus(Match.MatchStatus.LIVE);
                match.setMatchSummary("Live Wimbledon match from BetsAPI");
            } else {
                // Has date = SCHEDULED
                match.setStatus(Match.MatchStatus.SCHEDULED);
                match.setMatchSummary("Upcoming Wimbledon match from BetsAPI");
            }
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private Match parseBetsAPIMatchBase(String rowText) {
        try {
            if (!rowText.contains(" vs ")) return null;
            
            Match match = new Match();
            
            // Split by "vs"
            String[] parts = rowText.split(" vs ");
            if (parts.length < 2) return null;
            
            // Extract player names (improved parsing)
            String player1Name = extractPlayerName(parts[0].trim());
            String player2Name = extractPlayerName(parts[1].trim());
            
            if (player1Name == null || player2Name == null) return null;
            
            Team player1 = new Team();
            player1.setName(player1Name);
            player1.setCountry(extractCountryFromName(player1Name));
            
            Team player2 = new Team();
            player2.setName(player2Name);
            player2.setCountry(extractCountryFromName(player2Name));
            
            match.setHomeTeam(player1);
            match.setAwayTeam(player2);
            
            // Set match details
            match.setTournament("Wimbledon 2025");
            match.setVenue("All England Lawn Tennis Club");
            match.setMatchDateTime(LocalDateTime.now());
            
            // Set tennis sport
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private String extractPlayerName(String text) {
        // Remove common prefixes and suffixes
        text = text.replaceAll("^\\d+\\s*", ""); // Remove leading numbers
        text = text.replaceAll("\\s*\\d+$", ""); // Remove trailing numbers
        text = text.replaceAll("\\s*\\([^)]*\\)\\s*", " "); // Remove parentheses content
        text = text.trim();
        
        // Split by spaces and take first two words as name
        String[] words = text.split("\\s+");
        if (words.length >= 2) {
            return words[0] + " " + words[1];
        } else if (words.length == 1 && words[0].length() > 2) {
            return words[0];
        }
        
        return null;
    }
    
    private void parseAndSetScores(Match match, String scoreText) {
        try {
            // Look for score patterns like "6-4", "7-6", "3-2", etc.
            Pattern scorePattern = Pattern.compile("(\\d+)-(\\d+)");
            Matcher matcher = scorePattern.matcher(scoreText);
            
            if (matcher.find()) {
                int score1 = Integer.parseInt(matcher.group(1));
                int score2 = Integer.parseInt(matcher.group(2));
                match.setHomeScore(score1);
                match.setAwayScore(score2);
            }
        } catch (Exception e) {
            // Scores are optional
        }
    }
    
    private Match parseBetsAPIMatch(Element row) {
        try {
            Match match = new Match();
            
            // Extract match details from BetsAPI structure
            Elements homeAwayElements = row.select("td");
            if (homeAwayElements.size() < 3) return null;
            
            String matchText = row.text();
            
            // Look for "vs" pattern
            if (!matchText.contains(" vs ")) return null;
            
            String[] parts = matchText.split(" vs ");
            if (parts.length < 2) return null;
            
            // Extract player names
            String player1Name = cleanPlayerName(parts[0].trim());
            String player2Name = cleanPlayerName(parts[1].split("\\s+")[0] + " " + parts[1].split("\\s+")[1]);
            
            Team player1 = new Team();
            player1.setName(player1Name);
            player1.setCountry(extractCountryFromName(player1Name));
            
            Team player2 = new Team();
            player2.setName(player2Name);
            player2.setCountry(extractCountryFromName(player2Name));
            
            match.setHomeTeam(player1);
            match.setAwayTeam(player2);
            
            // Set match details
            match.setTournament("Wimbledon 2025");
            match.setVenue("All England Lawn Tennis Club");
            
            // Check if match is live or scheduled
            if (matchText.contains("LIVE") || matchText.contains("Live")) {
                match.setStatus(Match.MatchStatus.LIVE);
                match.setMatchSummary("Live Wimbledon match from BetsAPI");
            } else {
                match.setStatus(Match.MatchStatus.SCHEDULED);
                match.setMatchSummary("Upcoming Wimbledon match from BetsAPI");
            }
            
            // Set tennis sport
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            match.setMatchDateTime(LocalDateTime.now());
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private Match parseBetsAPIMatchFromText(String matchText) {
        try {
            if (!matchText.contains(" vs ")) return null;
            
            Match match = new Match();
            
            // Split by "vs"
            String[] parts = matchText.split(" vs ");
            if (parts.length < 2) return null;
            
            // Extract player names (take first two words from each part)
            String[] player1Parts = parts[0].trim().split("\\s+");
            String[] player2Parts = parts[1].trim().split("\\s+");
            
            if (player1Parts.length < 2 || player2Parts.length < 2) return null;
            
            String player1Name = player1Parts[player1Parts.length - 2] + " " + player1Parts[player1Parts.length - 1];
            String player2Name = player2Parts[0] + " " + player2Parts[1];
            
            Team player1 = new Team();
            player1.setName(cleanPlayerName(player1Name));
            player1.setCountry(extractCountryFromName(player1Name));
            
            Team player2 = new Team();
            player2.setName(cleanPlayerName(player2Name));
            player2.setCountry(extractCountryFromName(player2Name));
            
            match.setHomeTeam(player1);
            match.setAwayTeam(player2);
            
            // Set match details
            match.setTournament("Wimbledon 2025");
            match.setVenue("All England Lawn Tennis Club");
            match.setStatus(Match.MatchStatus.SCHEDULED);
            match.setMatchSummary("Wimbledon match from BetsAPI");
            
            // Set tennis sport
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            match.setMatchDateTime(LocalDateTime.now());
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private List<Match> scrapeTennisExplorerMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            System.out.println("🎾 Scraping Tennis Explorer for live matches...");
            
            Document doc = connectWithRetry(TENNIS_EXPLORER_URL);
            if (doc == null) return matches;
            
            // Tennis Explorer specific selectors
            Elements matchElements = doc.select(".match, .live-match, tr[data-match]");
            
            for (Element matchElement : matchElements) {
                try {
                    String matchText = matchElement.text();
                    if (matchText.contains(" vs ") || matchText.contains(" - ")) {
                        Match match = parseTennisExplorerMatch(matchElement);
                        if (match != null) {
                            matches.add(match);
                            System.out.println("✅ Found Tennis Explorer match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName());
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            System.out.println("🎾 Found " + matches.size() + " matches from Tennis Explorer");
            
        } catch (Exception e) {
            System.err.println("Error scraping Tennis Explorer: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeFlashscoreTennisMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            System.out.println("🎾 Scraping Flashscore for tennis matches...");
            
            Document doc = connectWithRetry(FLASHSCORE_TENNIS_URL);
            if (doc == null) return matches;
            
            // Flashscore specific selectors
            Elements matchElements = doc.select(".event__match, .match-row, [data-testid*='match']");
            
            for (Element matchElement : matchElements) {
                try {
                    Match match = parseFlashscoreMatch(matchElement);
                    if (match != null) {
                        matches.add(match);
                        System.out.println("✅ Found Flashscore match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName());
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            System.out.println("🎾 Found " + matches.size() + " matches from Flashscore");
            
        } catch (Exception e) {
            System.err.println("Error scraping Flashscore: " + e.getMessage());
        }
        
        return matches;
    }
    
    private List<Match> scrapeLiveTennisEuMatches() {
        List<Match> matches = new ArrayList<>();
        
        try {
            System.out.println("🎾 Scraping Live Tennis EU for current matches...");
            
            Document doc = connectWithRetry(LIVE_TENNIS_URL);
            if (doc == null) return matches;
            
            // Live Tennis specific selectors
            Elements matchElements = doc.select(".match-item, .live-match, .tennis-match");
            
            for (Element matchElement : matchElements) {
                try {
                    Match match = parseLiveTennisMatch(matchElement);
                    if (match != null) {
                        matches.add(match);
                        System.out.println("✅ Found Live Tennis EU match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName());
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            System.out.println("🎾 Found " + matches.size() + " matches from Live Tennis EU");
            
        } catch (Exception e) {
            System.err.println("Error scraping Live Tennis EU: " + e.getMessage());
        }
        
        return matches;
    }
    
    private Match parseTennisExplorerMatch(Element matchElement) {
        try {
            String matchText = matchElement.text();
            String[] parts;
            
            if (matchText.contains(" vs ")) {
                parts = matchText.split(" vs ");
            } else if (matchText.contains(" - ")) {
                parts = matchText.split(" - ");
            } else {
                return null;
            }
            
            if (parts.length < 2) return null;
            
            Match match = new Match();
            
            String player1Name = extractPlayerName(parts[0].trim());
            String player2Name = extractPlayerName(parts[1].trim());
            
            if (player1Name == null || player2Name == null) return null;
            
            Team player1 = new Team();
            player1.setName(player1Name);
            player1.setCountry(extractCountryFromName(player1Name));
            
            Team player2 = new Team();
            player2.setName(player2Name);
            player2.setCountry(extractCountryFromName(player2Name));
            
            match.setHomeTeam(player1);
            match.setAwayTeam(player2);
            match.setTournament("ATP/WTA Tour");
            match.setVenue("Tennis Court");
            match.setStatus(Match.MatchStatus.LIVE);
            match.setMatchSummary("Live tennis match from Tennis Explorer");
            match.setMatchDateTime(LocalDateTime.now());
            
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private Match parseFlashscoreMatch(Element matchElement) {
        try {
            Match match = new Match();
            
            // Try to find player names in Flashscore structure
            Elements playerElements = matchElement.select(".participant__participantName, .team-name, .player-name");
            
            if (playerElements.size() >= 2) {
                Team player1 = new Team();
                player1.setName(cleanPlayerName(playerElements.get(0).text()));
                player1.setCountry(extractCountryFromName(player1.getName()));
                
                Team player2 = new Team();
                player2.setName(cleanPlayerName(playerElements.get(1).text()));
                player2.setCountry(extractCountryFromName(player2.getName()));
                
                match.setHomeTeam(player1);
                match.setAwayTeam(player2);
            } else {
                // Fallback to text parsing
                String matchText = matchElement.text();
                if (!matchText.contains(" vs ") && !matchText.contains(" - ")) return null;
                
                String[] parts = matchText.contains(" vs ") ? matchText.split(" vs ") : matchText.split(" - ");
                if (parts.length < 2) return null;
                
                Team player1 = new Team();
                player1.setName(extractPlayerName(parts[0].trim()));
                player1.setCountry(extractCountryFromName(player1.getName()));
                
                Team player2 = new Team();
                player2.setName(extractPlayerName(parts[1].trim()));
                player2.setCountry(extractCountryFromName(player2.getName()));
                
                match.setHomeTeam(player1);
                match.setAwayTeam(player2);
            }
            
            // Check for live indicators
            String elementText = matchElement.text().toLowerCase();
            if (elementText.contains("live") || elementText.contains("in progress")) {
                match.setStatus(Match.MatchStatus.LIVE);
                match.setMatchSummary("Live tennis match from Flashscore");
            } else {
                match.setStatus(Match.MatchStatus.SCHEDULED);
                match.setMatchSummary("Upcoming tennis match from Flashscore");
            }
            
            match.setTournament("ATP/WTA Tour");
            match.setVenue("Tennis Court");
            match.setMatchDateTime(LocalDateTime.now());
            
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private Match parseLiveTennisMatch(Element matchElement) {
        try {
            Match match = new Match();
            String matchText = matchElement.text();
            
            // Parse player names from various patterns
            String[] parts = null;
            if (matchText.contains(" vs ")) {
                parts = matchText.split(" vs ");
            } else if (matchText.contains(" - ")) {
                parts = matchText.split(" - ");
            } else if (matchText.contains(" v ")) {
                parts = matchText.split(" v ");
            }
            
            if (parts == null || parts.length < 2) return null;
            
            String player1Name = extractPlayerName(parts[0].trim());
            String player2Name = extractPlayerName(parts[1].trim());
            
            if (player1Name == null || player2Name == null) return null;
            
            Team player1 = new Team();
            player1.setName(player1Name);
            player1.setCountry(extractCountryFromName(player1Name));
            
            Team player2 = new Team();
            player2.setName(player2Name);
            player2.setCountry(extractCountryFromName(player2Name));
            
            match.setHomeTeam(player1);
            match.setAwayTeam(player2);
            match.setTournament("ATP/WTA Tour");
            match.setVenue("Tennis Court");
            match.setStatus(Match.MatchStatus.LIVE);
            match.setMatchSummary("Live tennis match from Live Tennis");
            match.setMatchDateTime(LocalDateTime.now());
            
            Sport tennis = new Sport();
            tennis.setName("Tennis");
            tennis.setIconUrl("🎾");
            match.setSport(tennis);
            
            return match;
            
        } catch (Exception e) {
            return null;
        }
    }
}