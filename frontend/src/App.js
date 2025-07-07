import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Container, Row, Col } from 'react-bootstrap';
import Navigation from './components/Navigation';
import Sidebar from './components/Sidebar';
import Dashboard from './components/Dashboard';
import LiveMatches from './components/LiveMatches';
import UpcomingMatches from './components/UpcomingMatches';
import Sports from './components/Sports';
import Favorites from './components/Favorites';
import TennisNotifications from './components/TennisNotifications';

function App() {
  return (
    <Router>
      <div className="App">
        <Navigation />
        <Container fluid>
          <Row>
            <Col md={2} className="p-0">
              <Sidebar />
            </Col>
            <Col md={10} className="main-content">
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/live" element={<LiveMatches />} />
                <Route path="/upcoming" element={<UpcomingMatches />} />
                <Route path="/sports" element={<Sports />} />
                <Route path="/favorites" element={<Favorites />} />
                <Route path="/notifications" element={<TennisNotifications />} />
              </Routes>
            </Col>
          </Row>
        </Container>
      </div>
    </Router>
  );
}

export default App;