package com.bha.sportsTracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TournamentService {

    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // RapidAPI Odds API configuration
    private static final String RAPIDAPI_HOST = "odds-api1.p.rapidapi.com";
    private static final String RAPIDAPI_KEY = "16524b8396mshf23ce8404749be1p12d4ffjsn0b0116bee314";
    private static final String BASE_URL = "https://odds-api1.p.rapidapi.com";

    public TournamentService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get tennis tournaments from static data
     */
    public List<Map<String, Object>> getTennisTournaments() {
        logger.info("🏆 Loading tennis tournaments from static data...");
        
        try {
            List<Map<String, Object>> tournaments = getStaticTournaments();
            logger.info("✅ Loaded {} tennis tournaments", tournaments.size());
            return tournaments;
        } catch (Exception e) {
            logger.error("❌ Error loading static tournaments: {}", e.getMessage());
            return getMockTournaments();
        }
    }

    /**
     * Fetch events for a specific tournament
     */
    public List<Map<String, Object>> getTournamentEvents(String tournamentId) {
        logger.info("🎾 Fetching events for tournament ID: {}", tournamentId);
        
        try {
            String url = BASE_URL + "/events?tournamentId=" + tournamentId + "&media=false";
            logger.info("🔗 Calling RapidAPI URL: {}", url);
            
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-rapidapi-host", RAPIDAPI_HOST);
            headers.set("x-rapidapi-key", RAPIDAPI_KEY);
            headers.set("User-Agent", "SportsTracker/1.0");
            headers.set("Accept", "application/json");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // Make the API call
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            String response = responseEntity.getBody();
            logger.info("📥 API Response Status: {}", responseEntity.getStatusCode());
            logger.info("📥 API Response Body: {}", response != null ? response.substring(0, Math.min(500, response.length())) + "..." : "null");
            
            if (response != null && !response.isEmpty()) {
                List<Map<String, Object>> events = parseEvents(response);
                logger.info("📊 parseEvents returned {} events", events.size());
                if (!events.isEmpty()) {
                    logger.info("✅ Found {} events for tournament {}", events.size(), tournamentId);
                    return events;
                } else {
                    logger.warn("⚠️ parseEvents returned empty list for tournament {}", tournamentId);
                }
            } else {
                logger.warn("⚠️ API response is null or empty for tournament {}", tournamentId);
            }
            
            logger.warn("⚠️ No events found for tournament {}", tournamentId);
            return new ArrayList<>();

        } catch (Exception e) {
            logger.error("❌ Error fetching events for tournament {}: {}", tournamentId, e.getMessage(), e);
            
            // Check if it's a "no matches found" error - return empty list instead of mock data
            if (e.getMessage() != null &&
                (e.getMessage().contains("No matches found") ||
                 e.getMessage().contains("404 Not Found") ||
                 e.getMessage().contains("tournament exists but is not active"))) {
                logger.info("📅 No matches scheduled for tournament {}", tournamentId);
                return new ArrayList<>();
            }
            
            // For other errors (network issues, etc.), still return empty list to avoid misleading mock data
            logger.info("📅 No matches available for tournament {}", tournamentId);
            return new ArrayList<>();
        }
    }

    /**
     * Parse tournaments from RapidAPI JSON response
     */
    private List<Map<String, Object>> parseTournaments(String jsonResponse) {
        List<Map<String, Object>> tournaments = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            logger.info("🔍 JSON root type: {}, isArray: {}, isObject: {}", root.getNodeType(), root.isArray(), root.isObject());
            
            // Handle both array format and object with numbered keys
            if (root.isArray()) {
                logger.info("📋 Processing array format with {} elements", root.size());
                // Standard array format
                for (JsonNode tournament : root) {
                    Map<String, Object> tournamentData = parseTournamentNode(tournament);
                    if (tournamentData != null) {
                        tournaments.add(tournamentData);
                    }
                }
            } else if (root.isObject()) {
                logger.info("📋 Processing object format with {} fields", root.size());
                // Object with numbered keys format (like {"0": {...}, "1": {...}})
                root.fields().forEachRemaining(entry -> {
                    logger.info("🔑 Processing field: {}", entry.getKey());
                    JsonNode tournament = entry.getValue();
                    Map<String, Object> tournamentData = parseTournamentNode(tournament);
                    if (tournamentData != null) {
                        tournaments.add(tournamentData);
                        logger.info("✅ Added tournament: {}", tournamentData.get("name"));
                    } else {
                        logger.warn("⚠️ Failed to parse tournament from field: {}", entry.getKey());
                    }
                });
            } else {
                logger.warn("⚠️ Unexpected JSON format: {}", root.getNodeType());
            }
            
            logger.info("🎾 Parsed {} tournaments from API response", tournaments.size());
            
        } catch (Exception e) {
            logger.error("❌ Error parsing tournaments JSON response: {}", e.getMessage(), e);
        }
        
        return tournaments;
    }
    
    /**
     * Parse individual tournament node
     */
    private Map<String, Object> parseTournamentNode(JsonNode tournament) {
        try {
            Map<String, Object> tournamentData = new HashMap<>();
            
            // Try different possible field names for ID
            String tournamentId = null;
            if (tournament.has("id")) {
                tournamentId = tournament.get("id").asText();
            } else if (tournament.has("tournament_id")) {
                tournamentId = tournament.get("tournament_id").asText();
            } else if (tournament.has("key")) {
                tournamentId = tournament.get("key").asText();
            }
            
            // Try different possible field names for name
            String name = null;
            if (tournament.has("name")) {
                name = tournament.get("name").asText();
            } else if (tournament.has("title")) {
                name = tournament.get("title").asText();
            } else if (tournament.has("tournament_name")) {
                name = tournament.get("tournament_name").asText();
            }
            
            // Only add if we have both ID and name
            if (tournamentId != null && name != null && !tournamentId.isEmpty() && !name.isEmpty()) {
                tournamentData.put("tournamentId", tournamentId);
                tournamentData.put("name", name);
                return tournamentData;
            }
            
        } catch (Exception e) {
            logger.warn("⚠️ Error parsing individual tournament node: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Parse events from RapidAPI JSON response
     */
    private List<Map<String, Object>> parseEvents(String jsonResponse) {
        List<Map<String, Object>> events = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            logger.info("🔍 Parsing events JSON response...");
            
            // Check if response has events object
            if (root.has("events")) {
                JsonNode eventsNode = root.get("events");
                logger.info("📋 Found events node with {} entries", eventsNode.size());
                
                // Handle object format with numbered keys (like {"0": {...}, "1": {...}})
                if (eventsNode.isObject()) {
                    eventsNode.fields().forEachRemaining(entry -> {
                        JsonNode event = entry.getValue();
                        Map<String, Object> eventData = parseEventNode(event);
                        if (eventData != null) {
                            events.add(eventData);
                            logger.info("✅ Added event: {} vs {}",
                                ((List<String>) eventData.get("participants")).get(0),
                                ((List<String>) eventData.get("participants")).get(1));
                        }
                    });
                } else if (eventsNode.isArray()) {
                    // Handle array format
                    for (JsonNode event : eventsNode) {
                        Map<String, Object> eventData = parseEventNode(event);
                        if (eventData != null) {
                            events.add(eventData);
                        }
                    }
                }
            } else if (root.isArray()) {
                // Handle direct array format
                for (JsonNode event : root) {
                    Map<String, Object> eventData = parseEventNode(event);
                    if (eventData != null) {
                        events.add(eventData);
                    }
                }
            }
            
            logger.info("🎾 Successfully parsed {} events from API response", events.size());
            
        } catch (Exception e) {
            logger.error("❌ Error parsing events JSON response: {}", e.getMessage(), e);
        }
        
        return events;
    }
    
    /**
     * Parse individual event node
     */
    private Map<String, Object> parseEventNode(JsonNode event) {
        try {
            Map<String, Object> eventData = new HashMap<>();
            
            // Extract event ID
            String eventId = "unknown";
            if (event.has("eventId")) {
                eventId = event.get("eventId").asText();
            } else if (event.has("id")) {
                eventId = event.get("id").asText();
            }
            
            // Extract participants
            List<String> participants = new ArrayList<>();
            
            // Check for participant1 and participant2 fields (RapidAPI format)
            if (event.has("participant1") && event.has("participant2")) {
                String participant1 = formatParticipantName(event.get("participant1").asText());
                String participant2 = formatParticipantName(event.get("participant2").asText());
                participants.add(participant1);
                participants.add(participant2);
            } else if (event.has("participants") && event.get("participants").isArray()) {
                // Handle participants array format
                for (JsonNode participant : event.get("participants")) {
                    String name = participant.has("name") ? participant.get("name").asText() : "Unknown Player";
                    name = formatParticipantName(name);
                    participants.add(name);
                }
            }
            
            // Extract and convert timestamp
            LocalDateTime eventTime = LocalDateTime.now();
            if (event.has("startTime")) {
                try {
                    long timestamp = event.get("startTime").asLong();
                    eventTime = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochSecond(timestamp),
                        ZoneId.systemDefault()
                    );
                } catch (Exception e) {
                    logger.warn("⚠️ Error parsing startTime for event {}", eventId);
                }
            } else if (event.has("commence_time")) {
                try {
                    long timestamp = event.get("commence_time").asLong();
                    eventTime = LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochSecond(timestamp),
                        ZoneId.systemDefault()
                    );
                } catch (Exception e) {
                    logger.warn("⚠️ Error parsing commence_time for event {}", eventId);
                }
            }
            
            // Extract status
            String status = "scheduled";
            if (event.has("eventStatus")) {
                String eventStatus = event.get("eventStatus").asText();
                status = eventStatus.equals("pre-game") ? "scheduled" : eventStatus;
            }
            
            // Only add if we have participants
            if (!participants.isEmpty()) {
                eventData.put("eventId", eventId);
                eventData.put("participants", participants);
                eventData.put("eventTime", eventTime.toString());
                eventData.put("status", status);
                return eventData;
            }
            
        } catch (Exception e) {
            logger.warn("⚠️ Error parsing individual event node: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * Format participant name from "Last, First" to "First Last"
     */
    private String formatParticipantName(String name) {
        if (name.contains(",")) {
            String[] parts = name.split(",", 2);
            if (parts.length == 2) {
                return parts[1].trim() + " " + parts[0].trim();
            }
        }
        return name;
    }

    /**
     * Get static tournaments from provided API data
     */
    private List<Map<String, Object>> getStaticTournaments() {
        List<Map<String, Object>> tournaments = new ArrayList<>();
        
        // Static tournament data from the provided API response
        String[][] tournamentData = {
            {"2891", "ATP Bastad, Sweden Men Doubles"},
            {"2889", "ATP Bastad, Sweden Men Singles"},
            {"2961", "ATP Gstaad, Switzerland Men Doubles"},
            {"2959", "ATP Gstaad, Switzerland Men Singles"},
            {"13397", "ATP Los Cabos, Mexico Men Doubles"},
            {"13395", "ATP Los Cabos, Mexico Men Singles"},
            {"2557", "Wimbledon Men Doubles"},
            {"2555", "Wimbledon Men Singles"},
            {"3573", "ATP Challenger Braunschweig, Germany Men Doubles"},
            {"3571", "ATP Challenger Braunschweig, Germany Men Singles"},
            {"46499", "ATP Challenger Bunschoten, Netherlands Men Doubles"},
            {"46497", "ATP Challenger Bunschoten, Netherlands Men Singles"},
            {"3589", "ATP Challenger Granby, Canada Men Doubles"},
            {"3587", "ATP Challenger Granby, Canada Men Singles"},
            {"33087", "ATP Challenger Iasi, Romania Men Doubles"},
            {"33089", "ATP Challenger Iasi, Romania Men Singles"},
            {"46491", "ATP Challenger Newport, USA Men Doubles"},
            {"46489", "ATP Challenger Newport, USA Men Singles"},
            {"46691", "ATP Challenger Nottingham 3, Great Britain Men Doubles"},
            {"46689", "ATP Challenger Nottingham 3, Great Britain Men Singles"},
            {"3651", "ATP Challenger Pozoblanco, Spain Men Doubles"},
            {"3649", "ATP Challenger Pozoblanco, Spain Men Singles"},
            {"3711", "ATP Challenger San Marino, San Marino Men Doubles"},
            {"3709", "ATP Challenger San Marino, San Marino Men Singles"},
            {"33051", "ATP Challenger Trieste, Italy Men Singles"},
            {"13653", "ATP Challenger Winnipeg, Canada Men Doubles"},
            {"13651", "ATP Challenger Winnipeg, Canada Men Singles"},
            {"15680", "Juniors Wimbledon, London, GB Men Doubles"},
            {"15678", "Juniors Wimbledon, London, GB Men Singles"},
            {"15682", "Juniors Wimbledon, London, GB Women Doubles"},
            {"15660", "Juniors Wimbledon, London, GB Women Singles"},
            {"15664", "Legends Wimbledon, London, GB Men Doubles"},
            {"15666", "Legends Wimbledon, London, GB Women Doubles"},
            {"37319", "Legends Wimbledon, London, Great Britain Mixed Doubles"},
            {"47538", "SRL Summer Invitational Castlemaine, AUS"},
            {"47540", "SRL Summer Invitational Castlemaine, AUS, Women"},
            {"47441", "UTR PTT Boondall Men 01"},
            {"47552", "UTR PTT Brisbane Men 02"},
            {"47554", "UTR PTT Dusseldorf Men 01"},
            {"47556", "UTR PTT Krakow Men 01"},
            {"47445", "UTR PTT Monterol Men 01"},
            {"47449", "UTR PTT Newport Beach Men 09"},
            {"47443", "UTR PTT Pazardzhik Men 02"},
            {"47447", "UTR PTT Walpole Men 01"},
            {"47451", "UTR PTT Boondall Women 01"},
            {"47564", "UTR PTT Boston Women 01"},
            {"47558", "UTR PTT Brisbane Women 02"},
            {"47560", "UTR PTT Dusseldorf Women 01"},
            {"47562", "UTR PTT Krakow Women 01"},
            {"47453", "UTR PTT Pazardzhik Women 02"},
            {"47455", "UTR PTT Stillwater Women 01"},
            {"34274", "WTA Hamburg, Germany Women Doubles"},
            {"34272", "WTA Hamburg, Germany Women Singles"},
            {"43369", "WTA Iasi, Romania Women Doubles"},
            {"43367", "WTA Iasi, Romania Women Singles"},
            {"2561", "Wimbledon Women Doubles"},
            {"2559", "Wimbledon Women Singles"},
            {"28229", "WTA 125K Bastad, Sweden Women Doubles"},
            {"28227", "WTA 125K Bastad, Sweden Women Singles"},
            {"36399", "WTA 125K Contrexeville, France Women Doubles"},
            {"36401", "WTA 125K Contrexeville, France Women Singles"},
            {"46617", "WTA 125K Newport, USA Women Doubles"},
            {"46619", "WTA 125K Newport, USA Women Singles"},
            {"46371", "WTA 125K Rome, Italy Women Doubles"},
            {"46369", "WTA 125K Rome, Italy Women Singles"},
            {"15672", "Wheelchairs Wimbledon, London, GB Men Doubles"},
            {"15670", "Wheelchairs Wimbledon, London, GB Men Singles"},
            {"15676", "Wheelchairs Wimbledon, London, GB Women Doubles"},
            {"15674", "Wheelchairs Wimbledon, London, GB Women Singles"}
        };
        
        for (String[] data : tournamentData) {
            Map<String, Object> tournament = new HashMap<>();
            tournament.put("tournamentId", data[0]);
            tournament.put("name", data[1]);
            tournaments.add(tournament);
        }
        
        logger.info("✅ Loaded {} static tournaments", tournaments.size());
        return tournaments;
    }

    /**
     * Generate mock tournaments for fallback
     */
    private List<Map<String, Object>> getMockTournaments() {
        List<Map<String, Object>> tournaments = new ArrayList<>();
        
        String[] tournamentNames = {
            "Wimbledon Championships",
            "US Open",
            "French Open",
            "Australian Open",
            "ATP Masters 1000 - Indian Wells",
            "ATP Masters 1000 - Miami"
        };
        
        for (int i = 0; i < tournamentNames.length; i++) {
            Map<String, Object> tournament = new HashMap<>();
            tournament.put("tournamentId", "mock_" + (i + 1));
            tournament.put("name", tournamentNames[i]);
            tournaments.add(tournament);
        }
        
        logger.info("✅ Generated {} mock tournaments", tournaments.size());
        return tournaments;
    }

    /**
     * Generate mock events for a tournament
     */
    private List<Map<String, Object>> getMockEvents(String tournamentId) {
        List<Map<String, Object>> events = new ArrayList<>();
        
        String[] players = {
            "Jannik Sinner", "Carlos Alcaraz", "Daniil Medvedev", "Alexander Zverev",
            "Casper Ruud", "Holger Rune", "Felix Auger-Aliassime", "Taylor Fritz",
            "Tommy Paul", "Grigor Dimitrov", "Ben Shelton", "Frances Tiafoe"
        };
        
        for (int i = 0; i < 6 && i < players.length - 1; i += 2) {
            Map<String, Object> event = new HashMap<>();
            
            List<String> participants = new ArrayList<>();
            participants.add(players[i]);
            participants.add(players[i + 1]);
            
            LocalDateTime eventTime = LocalDateTime.now().plusDays(i / 2 + 1).plusHours(i * 2);
            
            event.put("eventId", "mock_event_" + (i + 1));
            event.put("participants", participants);
            event.put("eventTime", eventTime.toString());
            event.put("status", "scheduled");
            
            events.add(event);
        }
        
        logger.info("✅ Generated {} mock events for tournament {}", events.size(), tournamentId);
        return events;
    }
}