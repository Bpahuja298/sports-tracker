import React from 'react';
import { Nav } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const Sidebar = () => {
  return (
    <div className="sidebar">
      <Nav className="flex-column p-3">
        <LinkContainer to="/">
          <Nav.Link className="text-light">
            🎾 Tennis Dashboard
          </Nav.Link>
        </LinkContainer>
        <LinkContainer to="/live">
          <Nav.Link className="text-light">
            🔴 Live Tennis
          </Nav.Link>
        </LinkContainer>
        <LinkContainer to="/upcoming">
          <Nav.Link className="text-light">
            📅 Upcoming Matches
          </Nav.Link>
        </LinkContainer>
        <LinkContainer to="/sports">
          <Nav.Link className="text-light">
            🏆 Tournaments
          </Nav.Link>
        </LinkContainer>
        <LinkContainer to="/notifications">
          <Nav.Link className="text-light">
            🔔 Notifications
          </Nav.Link>
        </LinkContainer>
        <LinkContainer to="/favorites">
          <Nav.Link className="text-light">
            ⭐ My Favorites
          </Nav.Link>
        </LinkContainer>
      </Nav>
    </div>
  );
};

export default Sidebar;