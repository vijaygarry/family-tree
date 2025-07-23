import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Image, Button } from 'react-bootstrap';
import api from "../api/axiosInstance";
import ImageModal from '../components/ImageModal';
//import ERROR_MESSAGES from "../constants/messages";

function EventDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [modalIndex, setModalIndex] = useState(null);

  useEffect(() => {
      const fetchEvents = async () => {
        try {
            const requestBody = {};
          const res = await api.post("/family/getEvents", requestBody);
          const found = res.data.events.find((e) => e.eventId.toString() === id);
          setEvent(found);
          //setError("");
          setLoading(false);
        } catch (err) {
          console.error("Failed to fetch events", err);
          if (err.response?.data?.operationMessage) {
            // API returned an error in payload
            //setError(err.response?.data?.operationMessage);
          } else {
            //setError(ERROR_MESSAGES.DEFAULT);
          }
        }
      };
      fetchEvents();
      
    }, [id]);

  if (!event) return <div>Loading...</div>;

  const photos = event.eventPhotos || [];
  const allImages = [event.eventImage, ...photos];

  return (
    <Container className="py-4">
      <Button variant="outline-secondary" onClick={() => navigate(-1)} className="mb-3">
        ← Back to Events
      </Button>
      <h2>{event.title}</h2>
      <p>{event.description}</p>
      <p><strong>Date:</strong> {event.eventDate}</p>
      <p><strong>Time:</strong> {event.eventTime}</p>
      <p><strong>Organizer:</strong> {event.eventOrganizer}</p>
      <p><strong>Notes:</strong> {event.notes}</p>
      <p><strong>Address:</strong> {`${event.eventPlace.addressLine1}, ${event.eventPlace.city}, ${event.eventPlace.state} ${event.eventPlace.postalCode}`}</p>

      <Row className="mt-4">
        {allImages.map((img, idx) => (
          <Col key={idx} md={3} className="mb-3">
            <Image
              src={img}
              thumbnail
              onClick={() => setModalIndex(idx)}
              style={{ cursor: 'pointer' }}
            />
          </Col>
        ))}
      </Row>

      <ImageModal
        show={modalIndex !== null}
        images={allImages}
        currentIndex={modalIndex || 0}
        onClose={() => setModalIndex(null)}
        onNext={() => setModalIndex((prev) => Math.min(prev + 1, allImages.length - 1))}
        onPrev={() => setModalIndex((prev) => Math.max(prev - 1, 0))}
      />
    </Container>
  );
}

export default EventDetailPage;