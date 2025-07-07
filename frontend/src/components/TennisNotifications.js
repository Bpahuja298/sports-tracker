import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Badge, Button, Alert, ListGroup } from 'react-bootstrap';
import axios from 'axios';

const TennisNotifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Using a default user ID for demo purposes
  const userId = 'demo-user';

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`http://localhost:8080/api/tennis/notifications/${userId}`);
      setNotifications(response.data);
      
      const countResponse = await axios.get(`http://localhost:8080/api/tennis/notifications/${userId}/count`);
      setUnreadCount(countResponse.data.unreadCount);
      
      setError(null);
    } catch (err) {
      console.error('Error fetching notifications:', err);
      setError('Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  const markAsRead = async (notificationId) => {
    try {
      await axios.post(`http://localhost:8080/api/tennis/notifications/${userId}/mark-read/${notificationId}`);
      setNotifications(prev => 
        prev.map(notification => 
          notification.id === notificationId 
            ? { ...notification, read: true }
            : notification
        )
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (err) {
      console.error('Error marking notification as read:', err);
    }
  };

  const generateSampleNotifications = async () => {
    try {
      await axios.post(`http://localhost:8080/api/tennis/notifications/${userId}/generate-sample`);
      fetchNotifications();
    } catch (err) {
      console.error('Error generating sample notifications:', err);
    }
  };

  const clearAllNotifications = async () => {
    try {
      await axios.delete(`http://localhost:8080/api/tennis/notifications/${userId}/clear`);
      setNotifications([]);
      setUnreadCount(0);
    } catch (err) {
      console.error('Error clearing notifications:', err);
    }
  };

  useEffect(() => {
    fetchNotifications();
    
    // Set up polling for new notifications every 30 seconds
    const interval = setInterval(fetchNotifications, 30000);
    
    return () => clearInterval(interval);
  }, []);

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'MATCH_STARTING':
        return '🚀';
      case 'SCORE_UPDATE':
        return '📊';
      case 'MATCH_RESULT':
        return '🏆';
      case 'TOURNAMENT_UPDATE':
        return '📢';
      default:
        return '🎾';
    }
  };

  const getNotificationVariant = (type) => {
    switch (type) {
      case 'MATCH_STARTING':
        return 'warning';
      case 'SCORE_UPDATE':
        return 'info';
      case 'MATCH_RESULT':
        return 'success';
      case 'TOURNAMENT_UPDATE':
        return 'primary';
      default:
        return 'secondary';
    }
  };

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffInMinutes = Math.floor((now - date) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return date.toLocaleDateString();
  };

  if (loading) {
    return (
      <Container className="mt-4">
        <div className="text-center">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="mt-2">Loading tennis notifications...</p>
        </div>
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <Row>
        <Col>
          <div className="d-flex justify-content-between align-items-center mb-4">
            <div>
              <h2>🎾 Tennis Notifications</h2>
              <p className="text-muted">Stay updated with the latest tennis match alerts and tournament news</p>
            </div>
            <div>
              {unreadCount > 0 && (
                <Badge bg="danger" className="me-2">
                  {unreadCount} unread
                </Badge>
              )}
              <Button variant="outline-primary" size="sm" onClick={generateSampleNotifications} className="me-2">
                Generate Sample
              </Button>
              <Button variant="outline-secondary" size="sm" onClick={clearAllNotifications}>
                Clear All
              </Button>
            </div>
          </div>

          {error && (
            <Alert variant="danger" className="mb-4">
              {error}
            </Alert>
          )}

          {notifications.length === 0 ? (
            <Card className="text-center py-5">
              <Card.Body>
                <h4>🎾 No notifications yet</h4>
                <p className="text-muted">You'll receive notifications about tennis matches, scores, and tournament updates here.</p>
                <Button variant="primary" onClick={generateSampleNotifications}>
                  Generate Sample Notifications
                </Button>
              </Card.Body>
            </Card>
          ) : (
            <ListGroup>
              {notifications.map((notification) => (
                <ListGroup.Item
                  key={notification.id}
                  className={`d-flex justify-content-between align-items-start ${!notification.read ? 'bg-light border-primary' : ''}`}
                  style={{ cursor: 'pointer' }}
                  onClick={() => !notification.read && markAsRead(notification.id)}
                >
                  <div className="me-auto">
                    <div className="d-flex align-items-center mb-1">
                      <span className="me-2" style={{ fontSize: '1.2em' }}>
                        {getNotificationIcon(notification.type)}
                      </span>
                      <h6 className="mb-0">{notification.title}</h6>
                      {!notification.read && (
                        <Badge bg="primary" className="ms-2">New</Badge>
                      )}
                    </div>
                    <p className="mb-1">{notification.message}</p>
                    <small className="text-muted">
                      {formatTimestamp(notification.timestamp)}
                    </small>
                  </div>
                  <Badge bg={getNotificationVariant(notification.type)}>
                    {notification.type.replace('_', ' ')}
                  </Badge>
                </ListGroup.Item>
              ))}
            </ListGroup>
          )}

          <Card className="mt-4">
            <Card.Header>
              <h5>🔔 Notification Settings</h5>
            </Card.Header>
            <Card.Body>
              <p>Get notified about:</p>
              <ul>
                <li>🚀 Matches starting soon (30 minutes before)</li>
                <li>📊 Live score updates during matches</li>
                <li>🏆 Match results and winners</li>
                <li>📢 Tournament news and updates</li>
                <li>🎾 Wimbledon, US Open, French Open, Australian Open</li>
              </ul>
              <small className="text-muted">
                Notifications are automatically generated based on live tennis data and tournament schedules.
              </small>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default TennisNotifications;