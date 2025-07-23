// import React from 'react';
// import { Pagination } from 'react-bootstrap';

// function PaginationComponent({ currentPage, totalPages, onPageChange }) {
//   return (
//     <Pagination className="justify-content-center">
//       <Pagination.Prev onClick={() => onPageChange(currentPage - 1)} disabled={currentPage === 1} />
//       <Pagination.Item active>{currentPage}</Pagination.Item>
//       <Pagination.Next onClick={() => onPageChange(currentPage + 1)} disabled={currentPage === totalPages} />
//     </Pagination>
//   );
// }

// export default PaginationComponent;

// components/Pagination.js
import React from 'react';
import { Pagination } from 'react-bootstrap';

function PaginationComponent({ currentPage, totalPages, onPageChange }) {
  const renderPageNumbers = () => {
    const pageItems = [];
    const pageLimit = 5;
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, currentPage + 2);

    if (totalPages > pageLimit) {
      if (currentPage <= 3) {
        endPage = pageLimit;
      } else if (currentPage >= totalPages - 2) {
        startPage = totalPages - (pageLimit - 1);
      }
    }

    if (startPage > 1) {
      pageItems.push(<Pagination.Ellipsis key="start-ellipsis" disabled />);
    }

    for (let number = startPage; number <= endPage; number++) {
      pageItems.push(
        <Pagination.Item
          key={number}
          active={number === currentPage}
          onClick={() => onPageChange(number)}
        >
          {number}
        </Pagination.Item>
      );
    }

    if (endPage < totalPages) {
      pageItems.push(<Pagination.Ellipsis key="end-ellipsis" disabled />);
    }

    return pageItems;
  };

  return (
    <div className="text-center mb-3">
      <Pagination className="justify-content-center">
        <Pagination.Prev onClick={() => onPageChange(currentPage - 1)} disabled={currentPage === 1} />
        {renderPageNumbers()}
        <Pagination.Next onClick={() => onPageChange(currentPage + 1)} disabled={currentPage === totalPages} />
      </Pagination>
    </div>
  );
}

export default PaginationComponent;
