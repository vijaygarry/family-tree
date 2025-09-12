import React, { useState } from "react";
import {
  Accordion,
  Button,
  Form,
  InputGroup,
  Container,
  Row,
  Col,
} from "react-bootstrap";

const HelpPage = () => {
  const [expandedItems, setExpandedItems] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  const faqs = [
    { id: "1", question: "How do I reset my password?", answer: "Click on 'Forgot Password' on the login page and follow the instructions." },
    { id: "2", question: "How do I update my profile?", answer: "Go to the 'Profile' section from the menu and edit your details." },
    { id: "3", question: "How do I contact support?", answer: "You can contact support by emailing support@example.com." },
    { id: "4", question: "How do I add a new family member?", answer: "Navigate to the Family Tree page and click 'Add Member'." },
  ];

  // Filter based on search
  const filteredFaqs = faqs.filter((faq) =>
    faq.question.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Toggle a single FAQ
  const toggleItem = (id) => {
    setExpandedItems((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  // Expand/collapse all
  const toggleAll = (expand) => {
    if (expand) {
      setExpandedItems(filteredFaqs.map((faq) => faq.id));
    } else {
      setExpandedItems([]);
    }
  };

  return (
    <Container className="py-4">
      <div className="container p-4 bg-white rounded mt-4">
      {/* Help image */}
      <Row className="mb-4">
        <Col className="text-center">
          <img
            src="/faq-banner.jpg"
            alt="Help"
            className="img-fluid rounded shadow"
            style={{ maxHeight: "200px" }}
          />
        </Col>
      </Row>

      {/* Search and buttons */}
      <Row className="mb-3 d-flex">
        <Col md={8}>
          <InputGroup>
            <Form.Control
              type="text"
              placeholder="Search questions..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="me-2"
            />
          </InputGroup>
        </Col>
        <Col md={4} className="text-md-end mt-2 mt-md-0">
          <Button
            variant="primary"
            className="me-2"
            onClick={() => toggleAll(true)}
          >
            Expand All
          </Button>
          <Button variant="secondary" onClick={() => toggleAll(false)}>
            Collapse All
          </Button>
        </Col>
      </Row>

      {/* FAQ Accordion */}
      <Accordion activeKey={expandedItems}>
        {filteredFaqs.map((faq) => (
          <Accordion.Item eventKey={faq.id} key={faq.id}>
            <Accordion.Header onClick={() => toggleItem(faq.id)}>
              {faq.question}
            </Accordion.Header>
            <Accordion.Body>{faq.answer}</Accordion.Body>
          </Accordion.Item>
        ))}
      </Accordion>

      {/* No results */}
      {filteredFaqs.length === 0 && (
        <p className="mt-3 text-muted text-center">No matching questions found.</p>
      )}
      </div>
    </Container>
  );
};

export default HelpPage;
