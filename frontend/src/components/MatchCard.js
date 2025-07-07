import React from 'react';
import { Card, Row, Col, Badge } from 'react-bootstrap';

const MatchCard = ({ match }) => {
  const getStatusBadge = (status) => {
    switch (status) {
      case 'LIVE':
        return <Badge bg="danger" className="live-indicator">LIVE</Badge>;
      case 'COMPLETED':
        return <Badge bg="success">COMPLETED</Badge>;
      case 'SCHEDULED':
        return <Badge bg="secondary">SCHEDULED</Badge>;
      case 'CANCELLED':
        return <Badge bg="warning">CANCELLED</Badge>;
      case 'POSTPONED':
        return <Badge bg="info">POSTPONED</Badge>;
      default:
        return <Badge bg="secondary">{status}</Badge>;
    }
  };

  const formatDateTime = (dateTime) => {
    const date = new Date(dateTime);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getSportIcon = (sportName) => {
    switch (sportName?.toLowerCase()) {
      case 'cricket':
        return '🏏';
      case 'football':
        return '⚽';
      case 'tennis':
        return '🎾';
      case 'badminton':
        return '🏸';
      default:
        return '🏆';
    }
  };

  return (
    <Card className="match-card mb-3">
      <Card.Body>
        <Row className="align-items-center">
          <Col xs={1}>
            <div className="sport-icon">
              {getSportIcon(match.sport?.name)}
            </div>
          </Col>
          <Col xs={4}>
            <div className="team-name">{match.homeTeam?.name}</div>
            <div className="team-name">{match.awayTeam?.name}</div>
          </Col>
          <Col xs={2} className="text-center">
            {match.status === 'LIVE' || match.status === 'COMPLETED' ? (
              <div className="score">
                {match.homeScore || 0} - {match.awayScore || 0}
              </div>
            ) : (
              <div className="text-muted">vs</div>
            )}
          </Col>
          <Col xs={3}>
            <div className="match-status">
              {getStatusBadge(match.status)}
            </div>
            <div className="text-muted small">
              {formatDateTime(match.matchDateTime)}
            </div>
            {match.venue && (
              <div className="text-muted small">📍 {match.venue}</div>
            )}
          </Col>
          <Col xs={2}>
            {match.tournament && (
              <Badge bg="outline-primary" className="mb-1">
                {match.tournament}
              </Badge>
            )}
            <div className="text-muted small">{match.sport?.name}</div>
          </Col>
        </Row>
        {match.matchSummary && (
          <Row className="mt-2">
            <Col>
              <div className="text-muted small">{match.matchSummary}</div>
            </Col>
          </Row>
        )}
      </Card.Body>
    </Card>
  );
};

export default MatchCard;