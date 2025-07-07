import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Form, Button } from 'react-bootstrap';
import { matchesApi, sportsApi } from '../services/api';
import MatchCard from './MatchCard';

const UpcomingMatches = () => {
  const [matches, setMatches] = useState([]);
  const [sports, setSports] = useState([]);
  const [selectedSport, setSelectedSport] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchSports();
    fetchUpcomingMatches();
  }, []);

  useEffect(() => {
    fetchUpcomingMatches();
  }, [selectedSport]);

  const fetchSports = async () => {
    try {
      const response = await sportsApi.getAllSports();
      setSports(response.data);
    } catch (err) {
      console.error('Error fetching sports:', err);
    }
  };

  const fetchUpcomingMatches = async () => {
    try {
      setLoading(true);
      let response;
      if (selectedSport) {
        response = await matchesApi.getUpcomingMatchesBySport(selectedSport);
      } else {
        response = await matchesApi.getUpcomingMatches();
      }
      setMatches(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch upcoming matches');
      console.error('Upcoming matches error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSportChange = (e) => {
    setSelectedSport(e.target.value);
  };

  const handleRefresh = () => {
    fetchUpcomingMatches();
  };

  if (loading && matches.length === 0) {
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
            <h1 className="section-title">📅 Upcoming Matches</h1>
            <Button 
              variant="outline-primary" 
              onClick={handleRefresh}
              disabled={loading}
            >
              {loading ? (
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

      {/* Sport Filter */}
      <Row className="mb-4">
        <Col md={4}>
          <Card>
            <Card.Body>
              <Form.Group>
                <Form.Label>Filter by Sport</Form.Label>
                <Form.Select 
                  value={selectedSport} 
                  onChange={handleSportChange}
                  disabled={loading}
                >
                  <option value="">All Sports</option>
                  {sports.map((sport) => (
                    <option key={sport.id} value={sport.id}>
                      {sport.iconUrl || '🏆'} {sport.name}
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

      <Row>
        <Col>
          <Card>
            <Card.Header>
              <h5 className="mb-0">
                Scheduled Matches
                {selectedSport && sports.find(s => s.id == selectedSport) && (
                  <span className="ms-2 text-muted">
                    - {sports.find(s => s.id == selectedSport).name}
                  </span>
                )}
              </h5>
            </Card.Header>
            <Card.Body>
              {matches.length > 0 ? (
                matches.map((match) => (
                  <MatchCard key={match.id} match={match} />
                ))
              ) : (
                <div className="text-center text-muted py-5">
                  <div className="mb-3" style={{ fontSize: '4rem' }}>📅</div>
                  <h4>No Upcoming Matches</h4>
                  {selectedSport ? (
                    <p>No upcoming matches found for the selected sport.</p>
                  ) : (
                    <p>No upcoming matches scheduled at the moment.</p>
                  )}
                  <p>Check back later for exciting matches!</p>
                  <Button variant="primary" onClick={handleRefresh} className="mt-3">
                    Check Again
                  </Button>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default UpcomingMatches;