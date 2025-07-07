package com.bha.sportsTracker.service;

import com.bha.sportsTracker.entity.User;
import com.bha.sportsTracker.entity.Team;
import com.bha.sportsTracker.entity.Player;
import com.bha.sportsTracker.entity.Sport;
import com.bha.sportsTracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameAndActiveTrue(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailAndActiveTrue(email);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username '" + user.getUsername() + "' already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email '" + user.getEmail() + "' already exists");
        }
        
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setProfilePictureUrl(userDetails.getProfilePictureUrl());
        
        return userRepository.save(user);
    }
    
    public User addFavoriteTeam(Long userId, Team team) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.getFavoriteTeams().add(team);
        return userRepository.save(user);
    }
    
    public User removeFavoriteTeam(Long userId, Team team) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.getFavoriteTeams().remove(team);
        return userRepository.save(user);
    }
    
    public User addFavoritePlayer(Long userId, Player player) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.getFavoritePlayers().add(player);
        return userRepository.save(user);
    }
    
    public User removeFavoritePlayer(Long userId, Player player) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.getFavoritePlayers().remove(player);
        return userRepository.save(user);
    }
    
    public User addFavoriteSport(Long userId, Sport sport) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.getFavoriteSports().add(sport);
        return userRepository.save(user);
    }
    
    public User removeFavoriteSport(Long userId, Sport sport) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.getFavoriteSports().remove(sport);
        return userRepository.save(user);
    }
    
    public User updateLastLogin(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setActive(false);
        userRepository.save(user);
    }
}