import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Form, Button, Badge } from 'react-bootstrap';

const TournamentEvents = () => {
  const [tournaments, setTournaments] = useState([]);
  const [selectedTournament, setSelectedTournament] = useState('');
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [eventsLoading, setEventsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTournaments();
  }, []);

  useEffect(() => {
    if (selectedTournament) {
      fetchTournamentEvents(selectedTournament);
    } else {
      setEvents([]);
    }
  }, [selectedTournament]);

  const fetchTournaments = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/tournaments');
      if (!response.ok) {
        throw new Error('Failed to fetch tournaments');
      }
      const data = await response.json();
      setTournaments(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch tournaments');
      console.error('Tournaments error:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchTournamentEvents = async (tournamentId) => {
    try {
      setEventsLoading(true);
      const response = await fetch(`http://localhost:8080/api/tournaments/${tournamentId}/events`);
      if (!response.ok) {
        throw new Error('Failed to fetch events');
      }
      const data = await response.json();
      setEvents(data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch tournament events');
      console.error('Events error:', err);
    } finally {
      setEventsLoading(false);
    }
  };

  const handleTournamentChange = (e) => {
    setSelectedTournament(e.target.value);
  };

  const handleRefresh = () => {
    if (selectedTournament) {
      fetchTournamentEvents(selectedTournament);
    } else {
      fetchTournaments();
    }
  };

  const formatDateTime = (dateTimeString) => {
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

  if (loading) {
    return (
      <div className="loading-spinner">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  return (
    <Container fluid>
      <Row>
        <Col>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <h1 className="section-title">🏆 Tournament Events</h1>
            <Button 
              variant="outline-primary" 
              onClick={handleRefresh}
              disabled={loading || eventsLoading}
            >
              {(loading || eventsLoading) ? (
                <>
                  <Spinner
                    as="span"
                    animation="border"
                    size="sm"
                    role="status"
                    aria-hidden="true"
                    className="me-2"
                  />
                  Refreshing...
                </>
              ) : (
                '🔄 Refresh'
              )}
            </Button>
          </div>
        </Col>
      </Row>

      {/* Tournament Filter */}
      <Row className="mb-4">
        <Col md={6}>
          <Card>
            <Card.Body>
              <Form.Group>
                <Form.Label>Select Tournament</Form.Label>
                <Form.Select 
                  value={selectedTournament} 
                  onChange={handleTournamentChange}
                  disabled={loading}
                >
                  <option value="">Choose a tournament...</option>
                  {tournaments.map((tournament) => (
                    <option key={tournament.tournamentId} value={tournament.tournamentId}>
                      {tournament.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {error && (
        <Row className="mb-3">
          <Col>
            <Alert variant="danger" className="error-message">
              {error}
            </Alert>
          </Col>
        </Row>
      )}

      {/* Events Display */}
      <Row>
        <Col>
          <Card>
            <Card.Header>
              <h5 className="mb-0">
                Tournament Events
                {selectedTournament && tournaments.find(t => t.tournamentId === selectedTournament) && (
                  <span className="ms-2 text-muted">
                    - {tournaments.find(t => t.tournamentId === selectedTournament).name}
                  </span>
                )}
              </h5>
            </Card.Header>
            <Card.Body>
              {eventsLoading ? (
                <div className="text-center py-4">
                  <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading events...</span>
                  </Spinner>
                  <p className="mt-2">Loading tournament events...</p>
                </div>
              ) : !selectedTournament ? (
                <div className="text-center text-muted py-5">
                  <div className="mb-3" style={{ fontSize: '4rem' }}>🏆</div>
                  <h4>Select a Tournament</h4>
                  <p>Choose a tournament from the dropdown above to view its events.</p>
                </div>
              ) : events.length > 0 ? (
                <div className="events-list">
                  {events.map((event, index) => (
                    <Card key={event.eventId || index} className="mb-3 event-card">
                      <Card.Body>
                        <Row className="align-items-center">
                          <Col md={8}>
                            <div className="event-participants">
                              <h6 className="mb-2">
                                {event.participants && event.participants.length >= 2 ? (
                                  <>
                                    <span className="player-name">{event.participants[0]}</span>
                                    <span className="vs-text mx-2">vs</span>
                                    <span className="player-name">{event.participants[1]}</span>
                                  </>
                                ) : (
                                  <span>Event {index + 1}</span>
                                )}
                              </h6>
                              {event.eventTime && (
                                <p className="text-muted mb-1">
                                  <i className="bi bi-calendar-event me-1"></i>
                                  {formatDateTime(event.eventTime)}
                                </p>
                              )}
                            </div>
                          </Col>
                          <Col md={4} className="text-end">
                            <Badge 
                              bg={event.status === 'live' ? 'danger' : 
                                  event.status === 'completed' ? 'success' : 'primary'}
                              className="status-badge"
                            >
                              {event.status === 'live' ? '🔴 Live' :
                               event.status === 'completed' ? '✅ Completed' : '📅 Scheduled'}
                            </Badge>
                          </Col>
                        </Row>
                      </Card.Body>
                    </Card>
                  ))}
                </div>
              ) : (
                <div className="text-center text-muted py-5">
                  <div className="mb-3" style={{ fontSize: '4rem' }}>📅</div>
                  <h4>No Matches Scheduled</h4>
                  <p>No matches are currently scheduled for this tournament.</p>
                  <p>This tournament may be inactive or between seasons.</p>
                  <Button variant="primary" onClick={handleRefresh} className="mt-3">
                    Refresh Events
                  </Button>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <style jsx>{`
        .event-card {
          border-left: 4px solid #007bff;
          transition: all 0.2s ease;
        }
        
        .event-card:hover {
          box-shadow: 0 4px 8px rgba(0,0,0,0.1);
          transform: translateY(-2px);
        }
        
        .player-name {
          font-weight: 600;
          color: #2c3e50;
        }
        
        .vs-text {
          color: #6c757d;
          font-weight: 500;
        }
        
        .status-badge {
          font-size: 0.85rem;
          padding: 0.5rem 0.75rem;
        }
        
        .loading-spinner {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 200px;
        }
        
        .section-title {
          color: #2c3e50;
          font-weight: 700;
        }
        
        .events-list {
          max-height: 600px;
          overflow-y: auto;
        }
      `}</style>
    </Container>
  );
};

export default TournamentEvents;