package com.bha.sportsTracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;
    
    private LocalDateTime matchDateTime;
    
    private String venue;
    
    private Integer homeScore;
    
    private Integer awayScore;
    
    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.SCHEDULED;
    
    private String tournament;
    
    private String liveStreamUrl;
    
    @Column(length = 1000)
    private String matchSummary;
    
    public enum MatchStatus {
        SCHEDULED, LIVE, COMPLETED, CANCELLED, POSTPONED
    }

    // Constructors
    public Match() {}

    public Match(Long id, Team homeTeam, Team awayTeam, Sport sport, LocalDateTime matchDateTime, String venue, Integer homeScore, Integer awayScore, MatchStatus status, String tournament, String liveStreamUrl, String matchSummary) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.sport = sport;
        this.matchDateTime = matchDateTime;
        this.venue = venue;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
        this.tournament = tournament;
        this.liveStreamUrl = liveStreamUrl;
        this.matchSummary = matchSummary;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public LocalDateTime getMatchDateTime() {
        return matchDateTime;
    }

    public void setMatchDateTime(LocalDateTime matchDateTime) {
        this.matchDateTime = matchDateTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    public String getLiveStreamUrl() {
        return liveStreamUrl;
    }

    public void setLiveStreamUrl(String liveStreamUrl) {
        this.liveStreamUrl = liveStreamUrl;
    }

    public String getMatchSummary() {
        return matchSummary;
    }

    public void setMatchSummary(String matchSummary) {
        this.matchSummary = matchSummary;
    }
}