import React from 'react';
import { Navbar, Nav, Container } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

const Navigation = () => {
  return (
    <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
      <Container fluid>
        <LinkContainer to="/">
          <Navbar.Brand>🎾 Tennis Tracker</Navbar.Brand>
        </LinkContainer>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="ms-auto">
            <LinkContainer to="/live">
              <Nav.Link>🔴 Live</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/upcoming">
              <Nav.Link>📅 Upcoming</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/notifications">
              <Nav.Link>🔔 Notifications</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/favorites">
              <Nav.Link>⭐ Favorites</Nav.Link>
            </LinkContainer>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Navigation;