import React, { useEffect, useState } from "react";
import { Modal, Button, Row, Col, Image, Spinner } from "react-bootstrap";
import api from "../api/axiosInstance";
import ImageModal from "./ImageModal";

function EventDetailModal({ show, eventId, onClose }) {
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [modalIndex, setModalIndex] = useState(null);

  useEffect(() => {
    if (!eventId) return;
    const fetchEvent = async () => {
      try {
        const res = await api.post("/family/getEvents", {});
        const found = res.data.events.find((e) => e.eventId.toString() === eventId.toString());
        setEvent(found);
        setLoading(false);
      } catch (err) {
        console.error("Failed to fetch events", err);
        setLoading(false);
      }
    };
    fetchEvent();
  }, [eventId]);

  if (!show) return null;

  const photos = event?.eventPhotos || [];
  const allImages = [event?.eventImage, ...photos].filter(Boolean);

  return (
    <Modal show={show} onHide={onClose} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>{event?.title || "Event Details"}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {loading ? (
          <Spinner animation="border" />
        ) : (
          <>
            <p>{event?.description}</p>
            <p><strong>Date:</strong> {event?.eventDate}</p>
            <p><strong>Time:</strong> {event?.eventTime}</p>
            <p><strong>Organizer:</strong> {event?.eventOrganizer}</p>
            <p><strong>Notes:</strong> {event?.notes}</p>
            <p>
              <strong>Address:</strong>{" "}
              {`${event?.eventPlace?.addressLine1 || ""}, ${event?.eventPlace?.city || ""}, ${event?.eventPlace?.state || ""} ${event?.eventPlace?.postalCode || ""}`}
            </p>
            <Row className="mt-4">
              {allImages.map((img, idx) => (
                <Col key={idx} md={3} className="mb-3">
                  <Image
                    src={img}
                    thumbnail
                    onClick={() => setModalIndex(idx)}
                    style={{ cursor: "pointer" }}
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
          </>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>Close</Button>
      </Modal.Footer>
    </Modal>
  );
}

export default EventDetailModal;
