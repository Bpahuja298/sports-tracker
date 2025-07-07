import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Button } from 'react-bootstrap';
import { sportsApi } from '../services/api';

const Sports = () => {
  const [sports, setSports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchSports();
  }, []);

  const fetchSports = async () => {
    try {
      setLoading(true);
      const response = await sportsApi.getAllSports();
      setSports(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch sports');
      console.error('Sports error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    fetchSports();
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
            <h1 className="section-title">🏆 Sports</h1>
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
        {sports.length > 0 ? (
          sports.map((sport) => (
            <Col key={sport.id} lg={4} md={6} className="mb-4">
              <Card className="h-100 match-card">
                <Card.Body className="text-center">
                  <div className="sport-icon mb-3">
                    {sport.iconUrl || '🏆'}
                  </div>
                  <Card.Title className="h4">{sport.name}</Card.Title>
                  <Card.Text className="text-muted">
                    {sport.description || 'No description available'}
                  </Card.Text>
                  <div className="mt-auto">
                    <Button 
                      variant="outline-primary" 
                      size="sm"
                      className="me-2"
                    >
                      📊 View Stats
                    </Button>
                    <Button 
                      variant="outline-success" 
                      size="sm"
                      className="favorite-btn"
                    >
                      ⭐ Follow
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))
        ) : (
          <Col>
            <Card>
              <Card.Body>
                <div className="text-center text-muted py-5">
                  <div className="mb-3" style={{ fontSize: '4rem' }}>🏆</div>
                  <h4>No Sports Available</h4>
                  <p>No sports data found.</p>
                  <Button variant="primary" onClick={handleRefresh} className="mt-3">
                    Try Again
                  </Button>
                </div>
              </Card.Body>
            </Card>
          </Col>
        )}
      </Row>

      {/* Sports Statistics */}
      {sports.length > 0 && (
        <Row className="mt-5">
          <Col>
            <Card>
              <Card.Header>
                <h5 className="mb-0">📊 Sports Overview</h5>
              </Card.Header>
              <Card.Body>
                <Row>
                  <Col md={3} className="text-center">
                    <h3 className="text-primary">{sports.length}</h3>
                    <p className="text-muted">Total Sports</p>
                  </Col>
                  <Col md={3} className="text-center">
                    <h3 className="text-success">
                      {sports.filter(s => s.active).length}
                    </h3>
                    <p className="text-muted">Active Sports</p>
                  </Col>
                  <Col md={3} className="text-center">
                    <h3 className="text-info">∞</h3>
                    <p className="text-muted">Matches Available</p>
                  </Col>
                  <Col md={3} className="text-center">
                    <h3 className="text-warning">24/7</h3>
                    <p className="text-muted">Live Updates</p>
                  </Col>
                </Row>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
    </Container>
  );
};

export default Sports;