import React from 'react';
import { Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import './EventCard.css';

const eventColors = {
  Birthday: '#cce5ff',
  Wedding: '#d1ecf1',
  Death: '#e2e3e5',
  'Community Party': '#d4edda',
};

function EventCard({ event }) {
  const navigate = useNavigate();
  const bgColor = eventColors[event.eventType] || 'light';

  return (
    <Card
      className="mb-4 shadow-sm event-card"
      onClick={() => navigate(`/event/${event.eventId}`)}
      style={{ cursor: 'pointer', height: '100%', backgroundColor: bgColor }}
    >
      <Card.Img variant="top" src={event.eventImage} style={{ height: '250px', objectFit: 'cover' }} />
      <Card.Body>
        <Card.Title>{event.title}</Card.Title>
        <Card.Subtitle className="mb-2 text-muted">{event.eventType}</Card.Subtitle>
        <Card.Text>
          {event.description.slice(0, 100)}...
        </Card.Text>
      </Card.Body>
    </Card>
  );
}

export default EventCard;
