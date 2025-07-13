package com.bha.sportsTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SportsTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportsTrackerApplication.class, args);
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🎾 TENNIS TOURNAMENT & EVENT VIEWER STARTED");
        System.out.println("=".repeat(80));
        System.out.println("🏆 View tennis tournaments and events from around the world");
        System.out.println("📊 Browse 70+ tournaments across ATP, WTA, Challenger, and more");
        System.out.println("🌐 API endpoints available at: http://localhost:8080/api/tournaments/");
        System.out.println("=".repeat(80) + "\n");
    }
}
