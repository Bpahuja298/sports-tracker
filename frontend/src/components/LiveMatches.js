import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Button } from 'react-bootstrap';
import { matchesApi } from '../services/api';
import MatchCard from './MatchCard';

const LiveMatches = () => {
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchLiveMatches();
    // Set up auto-refresh for live matches every 30 seconds
    const interval = setInterval(fetchLiveMatches, 30000);
    return () => clearInterval(interval);
  }, []);

  const fetchLiveMatches = async () => {
    try {
      setLoading(true);
      const response = await matchesApi.getLiveMatches();
      setMatches(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch live matches');
      console.error('Live matches error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    fetchLiveMatches();
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
            <h1 className="section-title">🔴 Live Matches</h1>
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
            <Card.Header className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0">Live Sports Action</h5>
              {matches.length > 0 && (
                <span className="live-indicator">
                  {matches.length} LIVE
                </span>
              )}
            </Card.Header>
            <Card.Body>
              {matches.length > 0 ? (
                <>
                  <div className="mb-3 text-muted small">
                    🔄 Auto-refreshing every 30 seconds
                  </div>
                  {matches.map((match) => (
                    <MatchCard key={match.id} match={match} />
                  ))}
                </>
              ) : (
                <div className="text-center text-muted py-5">
                  <div className="mb-3" style={{ fontSize: '4rem' }}>⚽🏏🎾</div>
                  <h4>No Live Matches</h4>
                  <p>There are no live matches at the moment.</p>
                  <p>Check back later for exciting live sports action!</p>
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

export default LiveMatches;