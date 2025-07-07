package com.bha.sportsTracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String profilePictureUrl;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime lastLoginAt;
    
    private boolean active = true;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_teams",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<Team> favoriteTeams = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_players",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> favoritePlayers = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_sports",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "sport_id")
    )
    private Set<Sport> favoriteSports = new HashSet<>();

    // Constructors
    public User() {}

    public User(Long id, String username, String email, String password, String firstName, String lastName, String profilePictureUrl, LocalDateTime createdAt, LocalDateTime lastLoginAt, boolean active, Set<Team> favoriteTeams, Set<Player> favoritePlayers, Set<Sport> favoriteSports) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
        this.active = active;
        this.favoriteTeams = favoriteTeams != null ? favoriteTeams : new HashSet<>();
        this.favoritePlayers = favoritePlayers != null ? favoritePlayers : new HashSet<>();
        this.favoriteSports = favoriteSports != null ? favoriteSports : new HashSet<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Team> getFavoriteTeams() {
        return favoriteTeams;
    }

    public void setFavoriteTeams(Set<Team> favoriteTeams) {
        this.favoriteTeams = favoriteTeams;
    }

    public Set<Player> getFavoritePlayers() {
        return favoritePlayers;
    }

    public void setFavoritePlayers(Set<Player> favoritePlayers) {
        this.favoritePlayers = favoritePlayers;
    }

    public Set<Sport> getFavoriteSports() {
        return favoriteSports;
    }

    public void setFavoriteSports(Set<Sport> favoriteSports) {
        this.favoriteSports = favoriteSports;
    }
}