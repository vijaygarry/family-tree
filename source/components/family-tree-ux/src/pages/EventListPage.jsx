import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Tabs, Tab, Spinner } from 'react-bootstrap';
import api from "../api/axiosInstance";
import EventCard from '../components/EventCard';
import PaginationComponent from '../components/Pagination';
import dayjs from 'dayjs';
import ERROR_MESSAGES from "../constants/messages";

function EventListPage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const requestBody = {};
        const res = await api.post("/family/getEvents", requestBody);
        setEvents(res.data.events);
        setTotalPages(res.data.totalPages || 1);
        setError("");
        setLoading(false);
      } catch (err) {
        console.error("Failed to fetch events", err);
        if (err.response?.data?.operationMessage) {
          // API returned an error in payload
          setError(err.response?.data?.operationMessage);
        } else {
          setError(ERROR_MESSAGES.DEFAULT);
        }
      }
    };
    fetchEvents();
  }, [currentPage]);

  const now = dayjs();
  const upcomingEvents = events; //.filter((e) => dayjs(e.eventDate).isAfter(now));
  const pastEvents = events; //.filter((e) => dayjs(e.eventDate).isBefore(now));

  return (
    <Container className="py-4">
      <h2 className="text-center mb-4">Family Events</h2>
      <Tabs defaultActiveKey="upcoming" className="mb-3">
        <Tab eventKey="upcoming" title="Upcoming Events">
          {loading ? (
            <Spinner animation="border" />
          ) : (
            <Row>
              {upcomingEvents.map((event) => (
                <Col key={event.eventId} md={6} lg={3} className="d-flex">
                  <EventCard event={event} />
                </Col>
              ))}
            </Row>
          )}
        </Tab>
        <Tab eventKey="past" title="Past Events">
          {loading ? (
            <Spinner animation="border" />
          ) : (
            <Row>
              {pastEvents.map((event) => (
                <Col key={event.eventId} md={6} lg={3} className="d-flex">
                  <EventCard event={event} />
                </Col>
              ))}
            </Row>
          )}
        </Tab>
      </Tabs>
      <PaginationComponent currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />
    </Container>
  );
}

export default EventListPage;