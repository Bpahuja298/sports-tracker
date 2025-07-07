import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Spinner, Alert, Button, Tabs, Tab } from 'react-bootstrap';
import { sportsApi, matchesApi } from '../services/api';

const Favorites = () => {
  const [sports, setSports] = useState([]);
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('sports');

  useEffect(() => {
    fetchFavorites();
  }, []);

  const fetchFavorites = async () => {
    try {
      setLoading(true);
      // For now, we'll show all sports and matches as "favorites"
      // In a real app, this would fetch user-specific favorites
      const [sportsResponse, matchesResponse] = await Promise.all([
        sportsApi.getAllSports(),
        matchesApi.getAllMatches()
      ]);
      
      setSports(sportsResponse.data);
      setMatches(matchesResponse.data.slice(0, 10)); // Show first 10 matches
      setError(null);
    } catch (err) {
      setError('Failed to fetch favorites');
      console.error('Favorites error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = () => {
    fetchFavorites();
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
            <h1 className="section-title">⭐ My Favorites</h1>
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
            <Card.Body>
              <Tabs
                activeKey={activeTab}
                onSelect={(k) => setActiveTab(k)}
                className="mb-3"
              >
                <Tab eventKey="sports" title="🏆 Favorite Sports">
                  <Row>
                    {sports.length > 0 ? (
                      sports.map((sport) => (
                        <Col key={sport.id} lg={4} md={6} className="mb-3">
                          <Card className="h-100 match-card">
                            <Card.Body className="text-center">
                              <div className="sport-icon mb-2">
                                {sport.iconUrl || '🏆'}
                              </div>
                              <Card.Title className="h6">{sport.name}</Card.Title>
                              <Card.Text className="text-muted small">
                                {sport.description}
                              </Card.Text>
                              <Button 
                                variant="outline-danger" 
                                size="sm"
                                className="favorite-btn active"
                              >
                                💖 Following
                              </Button>
                            </Card.Body>
                          </Card>
                        </Col>
                      ))
                    ) : (
                      <Col>
                        <div className="text-center text-muted py-4">
                          <h5>No Favorite Sports</h5>
                          <p>Start following your favorite sports to see them here!</p>
                        </div>
                      </Col>
                    )}
                  </Row>
                </Tab>

                <Tab eventKey="teams" title="👥 Favorite Teams">
                  <div className="text-center text-muted py-5">
                    <div className="mb-3" style={{ fontSize: '3rem' }}>👥</div>
                    <h4>Favorite Teams</h4>
                    <p>Team favorites feature coming soon!</p>
                    <p>You'll be able to follow your favorite teams and get personalized updates.</p>
                  </div>
                </Tab>

                <Tab eventKey="players" title="🌟 Favorite Players">
                  <div className="text-center text-muted py-5">
                    <div className="mb-3" style={{ fontSize: '3rem' }}>🌟</div>
                    <h4>Favorite Players</h4>
                    <p>Player favorites feature coming soon!</p>
                    <p>Follow your favorite athletes and never miss their matches.</p>
                  </div>
                </Tab>

                <Tab eventKey="matches" title="🎯 Favorite Matches">
                  <div className="text-center text-muted py-5">
                    <div className="mb-3" style={{ fontSize: '3rem' }}>🎯</div>
                    <h4>Favorite Matches</h4>
                    <p>Match bookmarking feature coming soon!</p>
                    <p>Save interesting matches to watch later or review results.</p>
                  </div>
                </Tab>
              </Tabs>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Quick Stats */}
      <Row className="mt-4">
        <Col>
          <Card>
            <Card.Header>
              <h5 className="mb-0">📊 Your Sports Activity</h5>
            </Card.Header>
            <Card.Body>
              <Row>
                <Col md={3} className="text-center">
                  <h3 className="text-primary">{sports.length}</h3>
                  <p className="text-muted">Sports Following</p>
                </Col>
                <Col md={3} className="text-center">
                  <h3 className="text-success">0</h3>
                  <p className="text-muted">Teams Following</p>
                </Col>
                <Col md={3} className="text-center">
                  <h3 className="text-info">0</h3>
                  <p className="text-muted">Players Following</p>
                </Col>
                <Col md={3} className="text-center">
                  <h3 className="text-warning">0</h3>
                  <p className="text-muted">Saved Matches</p>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Favorites;