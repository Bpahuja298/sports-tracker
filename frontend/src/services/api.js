import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Sports API
export const sportsApi = {
  getAllSports: () => api.get('/sports'),
  getSportById: (id) => api.get(`/sports/${id}`),
  getSportByName: (name) => api.get(`/sports/name/${name}`),
  createSport: (sport) => api.post('/sports', sport),
  updateSport: (id, sport) => api.put(`/sports/${id}`, sport),
  deleteSport: (id) => api.delete(`/sports/${id}`),
};

// Matches API
export const matchesApi = {
  getAllMatches: () => api.get('/matches'),
  getMatchById: (id) => api.get(`/matches/${id}`),
  getLiveMatches: () => api.get('/matches/live'),
  getUpcomingMatches: () => api.get('/matches/upcoming'),
  getUpcomingMatchesBySport: (sportId) => api.get(`/matches/upcoming/sport/${sportId}`),
  getMatchesBySport: (sportId) => api.get(`/matches/sport/${sportId}`),
  createMatch: (match) => api.post('/matches', match),
  updateMatch: (id, match) => api.put(`/matches/${id}`, match),
  updateMatchScore: (id, scoreUpdate) => api.patch(`/matches/${id}/score`, scoreUpdate),
  updateMatchStatus: (id, statusUpdate) => api.patch(`/matches/${id}/status`, statusUpdate),
  deleteMatch: (id) => api.delete(`/matches/${id}`),
};

// Teams API (you'll need to create TeamController in backend)
export const teamsApi = {
  getAllTeams: () => api.get('/teams'),
  getTeamById: (id) => api.get(`/teams/${id}`),
  getTeamsBySport: (sportId) => api.get(`/teams/sport/${sportId}`),
  createTeam: (team) => api.post('/teams', team),
  updateTeam: (id, team) => api.put(`/teams/${id}`, team),
  deleteTeam: (id) => api.delete(`/teams/${id}`),
};

// Players API (you'll need to create PlayerController in backend)
export const playersApi = {
  getAllPlayers: () => api.get('/players'),
  getPlayerById: (id) => api.get(`/players/${id}`),
  getPlayersByTeam: (teamId) => api.get(`/players/team/${teamId}`),
  getPlayersBySport: (sportId) => api.get(`/players/sport/${sportId}`),
  createPlayer: (player) => api.post('/players', player),
  updatePlayer: (id, player) => api.put(`/players/${id}`, player),
  deletePlayer: (id) => api.delete(`/players/${id}`),
};

// Users API (you'll need to create UserController in backend)
export const usersApi = {
  getAllUsers: () => api.get('/users'),
  getUserById: (id) => api.get(`/users/${id}`),
  getUserByUsername: (username) => api.get(`/users/username/${username}`),
  createUser: (user) => api.post('/users', user),
  updateUser: (id, user) => api.put(`/users/${id}`, user),
  addFavoriteTeam: (userId, teamId) => api.post(`/users/${userId}/favorites/teams/${teamId}`),
  removeFavoriteTeam: (userId, teamId) => api.delete(`/users/${userId}/favorites/teams/${teamId}`),
  addFavoritePlayer: (userId, playerId) => api.post(`/users/${userId}/favorites/players/${playerId}`),
  removeFavoritePlayer: (userId, playerId) => api.delete(`/users/${userId}/favorites/players/${playerId}`),
  addFavoriteSport: (userId, sportId) => api.post(`/users/${userId}/favorites/sports/${sportId}`),
  removeFavoriteSport: (userId, sportId) => api.delete(`/users/${userId}/favorites/sports/${sportId}`),
  deleteUser: (id) => api.delete(`/users/${id}`),
};

export default api;