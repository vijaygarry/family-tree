import React from 'react';
import { Modal, Button } from 'react-bootstrap';

function ImageModal({ show, images, currentIndex, onClose, onNext, onPrev }) {
  if (!images?.length) return null;

  return (
    <Modal show={show} onHide={onClose} centered size="lg">
      <Modal.Body className="text-center">
        <img src={images[currentIndex]} alt="Event" className="img-fluid" />
      </Modal.Body>
      <Modal.Footer className="justify-content-between">
        <Button variant="secondary" onClick={onPrev} disabled={currentIndex === 0}>
          Previous
        </Button>
        <Button variant="secondary" onClick={onNext} disabled={currentIndex === images.length - 1}>
          Next
        </Button>
      </Modal.Footer>
    </Modal>
  );
}

export default ImageModal;