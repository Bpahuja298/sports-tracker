// Mobile API Service - Adapted from your web app
import { useState, useEffect } from 'react';

const API_BASE_URL = 'http://localhost:8080/api'; // Your existing backend

// Types based on your existing data structure
export interface Tournament {
  tournamentId: string;
  name: string;
  location?: string;
  startDate?: string;
  endDate?: string;
}

export interface TournamentEvent {
  eventId: string;
  eventTime: string;
  status: string;
  participants: string[];
  score?: string;
  round?: string;
}

// Tournament API - Exact same logic as your web app
export const fetchTournaments = async (): Promise<Tournament[]> => {
  try {
    const response = await fetch(`${API_BASE_URL}/tournaments`);
    if (!response.ok) {
      throw new Error('Failed to fetch tournaments');
    }
    return await response.json();
  } catch (error) {
    console.error('Tournaments error:', error);
    throw error;
  }
};

export const fetchTournamentEvents = async (tournamentId: string): Promise<TournamentEvent[]> => {
  try {
    const response = await fetch(`${API_BASE_URL}/tournaments/${tournamentId}/events`);
    if (!response.ok) {
      throw new Error('Failed to fetch events');
    }
    return await response.json();
  } catch (error) {
    console.error('Events error:', error);
    throw error;
  }
};

// Custom hook - Same pattern as your web component
export const useTournaments = () => {
  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTournaments();
  }, []);

  const loadTournaments = async () => {
    try {
      setLoading(true);
      const data = await fetchTournaments();
      setTournaments(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch tournaments');
      console.error('Tournaments error:', err);
    } finally {
      setLoading(false);
    }
  };

  return { tournaments, loading, error, refetch: loadTournaments };
};

export const useTournamentEvents = (tournamentId: string | null) => {
  const [events, setEvents] = useState<TournamentEvent[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (tournamentId) {
      loadEvents();
    } else {
      setEvents([]);
    }
  }, [tournamentId]);

  const loadEvents = async () => {
    if (!tournamentId) return;
    
    try {
      setLoading(true);
      const data = await fetchTournamentEvents(tournamentId);
      setEvents(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch tournament events');
      console.error('Events error:', err);
    } finally {
      setLoading(false);
    }
  };

  return { events, loading, error, refetch: loadEvents };
};

// Utility functions - Same as your web app
export const formatDateTime = (dateTimeString: string): string => {
  try {
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    return dateTimeString;
  }
};