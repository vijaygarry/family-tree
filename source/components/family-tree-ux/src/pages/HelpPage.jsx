import React, { useState } from "react";
import { Accordion, Card, Button, Form } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";

const HelpPage = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [expandedItems, setExpandedItems] = useState([]);
  
  // Sample FAQ data
  const faqs = [
    { id: 1, question: "How do I reset my password?", answer: "Go to the login page and click 'Forgot Password' to reset it." },
    { id: 2, question: "How can I add a new family member?", answer: "Navigate to the Family Tree page and click on 'Add Member'." },
    { id: 3, question: "How do I upload a profile photo?", answer: "Open the member profile page and use the 'Upload Image' option." },
    { id: 4, question: "Can I edit my email address?", answer: "Yes, open your profile settings and update the email field." },
  ];

  // Filter FAQs based on search
  const filteredFaqs = faqs.filter(faq =>
    faq.question.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const toggleAll = (expand) => {
    if (expand) {
      setExpandedItems(filteredFaqs.map(faq => faq.id));
    } else {
      setExpandedItems([]);
    }
  };

  const toggleItem = (id) => {
    setExpandedItems(prev =>
      prev.includes(id) ? prev.filter(item => item !== id) : [...prev, id]
    );
  };

  return (
    <div className="container py-4">
      {/* Image Banner */}
      <div className="text-center mb-4">
        <img
          src="/static/help-banner.png" // Replace with your help image path
          alt="Help Banner"
          className="img-fluid rounded shadow"
          style={{ maxHeight: "200px", objectFit: "cover" }}
        />
      </div>

      {/* Search Bar */}
      <div className="mb-3 d-flex">
        <Form.Control
          type="text"
          placeholder="Search questions..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="me-2"
        />
        <Button variant="success" onClick={() => toggleAll(true)}>Expand All</Button>
        <Button variant="secondary" className="ms-2" onClick={() => toggleAll(false)}>Collapse All</Button>
      </div>

      {/* FAQ Section */}
      <Accordion activeKey={expandedItems}>
        {filteredFaqs.length > 0 ? (
          filteredFaqs.map(faq => (
            <Card key={faq.id} className="mb-2 shadow-sm">
              <Accordion.Item eventKey={faq.id.toString()}>
                <Accordion.Header onClick={() => toggleItem(faq.id)}>
                  {faq.question}
                </Accordion.Header>
                <Accordion.Body>{faq.answer}</Accordion.Body>
              </Accordion.Item>
            </Card>
          ))
        ) : (
          <p className="text-muted">No questions found.</p>
        )}
      </Accordion>
    </div>
  );
};

export default HelpPage;
