// Mobile API Service - Adapted from your web app
// This will be placed in mobile/TennisTournamentApp/src/services/api.js

const API_BASE_URL = 'http://localhost:8080/api'; // Your existing backend

// Tournament API - Exact same logic as your web app
export const fetchTournaments = async () => {
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

export const fetchTournamentEvents = async (tournamentId) => {
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
import { useState, useEffect } from 'react';

export const useTournaments = () => {
  const [tournaments, setTournaments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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

export const useTournamentEvents = (tournamentId) => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (tournamentId) {
      loadEvents();
    } else {
      setEvents([]);
    }
  }, [tournamentId]);

  const loadEvents = async () => {
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
export const formatDateTime = (dateTimeString) => {
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