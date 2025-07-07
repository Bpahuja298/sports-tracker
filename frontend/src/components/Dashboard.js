import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert } from 'react-bootstrap';
import { matchesApi, sportsApi } from '../services/api';
import MatchCard from './MatchCard';

const Dashboard = () => {
  const [liveMatches, setLiveMatches] = useState([]);
  const [upcomingMatches, setUpcomingMatches] = useState([]);
  const [sports, setSports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [liveResponse, upcomingResponse, sportsResponse] = await Promise.all([
        matchesApi.getLiveMatches(),
        matchesApi.getUpcomingMatches(),
        sportsApi.getAllSports()
      ]);

      setLiveMatches(liveResponse.data);
      setUpcomingMatches(upcomingResponse.data.slice(0, 5)); // Show only first 5 upcoming matches
      setSports(sportsResponse.data);
    } catch (err) {
      setError('Failed to fetch dashboard data');
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
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

  if (error) {
    return (
      <Alert variant="danger" className="error-message">
        {error}
      </Alert>
    );
  }

  return (
    <Container fluid>
      <Row>
        <Col>
          <h1 className="section-title">🎾 Tennis Dashboard</h1>
          <p className="text-muted">Your one-stop place for all tennis action</p>
        </Col>
      </Row>

      {/* Tennis Overview */}
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Header>
              <h5 className="mb-0">🏅 Tennis Tournaments</h5>
            </Card.Header>
            <Card.Body>
              <Row>
                {sports.filter(sport => sport.name.toLowerCase().includes('tennis')).map((sport) => (
                  <Col key={sport.id} md={3} className="mb-3">
                    <Card className="text-center h-100">
                      <Card.Body>
                        <div className="sport-icon">🎾</div>
                        <Card.Title>{sport.name}</Card.Title>
                        <Card.Text className="text-muted small">
                          {sport.description}
                        </Card.Text>
                      </Card.Body>
                    </Card>
                  </Col>
                ))}
                {sports.filter(sport => sport.name.toLowerCase().includes('tennis')).length === 0 && (
                  <Col md={12}>
                    <div className="text-center py-4">
                      <div className="sport-icon mb-3">🎾</div>
                      <h5>Tennis Tournaments</h5>
                      <p className="text-muted">Wimbledon, French Open, US Open, Australian Open</p>
                    </div>
                  </Col>
                )}
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Live Tennis Matches */}
      <Row className="mb-4">
        <Col>
          <Card>
            <Card.Header className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">🔴 Live Tennis Matches</h5>
              {liveMatches.length > 0 && (
                <span className="live-indicator">
                  {liveMatches.length} LIVE
                </span>
              )}
              <small className="text-muted">Auto-refreshing every 30 seconds</small>
            </Card.Header>
            <Card.Body>
              {liveMatches.length > 0 ? (
                liveMatches.slice(0, 6).map((match, index) => (
                  <MatchCard key={`live-${match.id || index}`} match={match} />
                ))
              ) : (
                <div className="text-center text-muted py-4">
                  <div className="mb-3">🎾</div>
                  <h6>No live tennis matches at the moment</h6>
                  <p>Check back later for live tennis action from Wimbledon and other tournaments!</p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Upcoming Tennis Matches */}
      <Row>
        <Col>
          <Card>
            <Card.Header className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">📅 Upcoming Tennis Matches</h5>
              <small className="text-muted">Next 5 matches</small>
            </Card.Header>
            <Card.Body>
              {upcomingMatches.length > 0 ? (
                upcomingMatches.map((match, index) => (
                  <MatchCard key={`upcoming-${match.id || index}`} match={match} />
                ))
              ) : (
                <div className="text-center text-muted py-4">
                  <div className="mb-3">📅</div>
                  <h6>No upcoming tennis matches scheduled</h6>
                  <p>Stay tuned for exciting tennis matches from top tournaments!</p>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Quick Stats */}
      <Row className="mt-4">
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <h3 className="text-primary">{liveMatches.length}</h3>
              <p className="mb-0">Live Matches</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <h3 className="text-info">{upcomingMatches.length}</h3>
              <p className="mb-0">Upcoming Matches</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center">
            <Card.Body>
              <h3 className="text-success">{sports.length}</h3>
              <p className="mb-0">Tennis Tournaments</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Dashboard;