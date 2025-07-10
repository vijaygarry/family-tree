import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosInstance";
import ERROR_MESSAGES from "../constants/messages";

const FamilySearch = () => {
  const [search, setSearch] = useState({ searchString: "" });
  const [results, setResults] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleRowClick = (id) => {
    navigate(`/family/${id}`);
  };

  const handleSearch = async (e) => {
    e?.preventDefault(); // Prevent page reload if used inside a form
    try {
      const res = await api.post("/family/searchfamily", search);
      setResults(res.data);
      setError(null);
    } catch (err) {
      console.error("Search failed to load family details", err);
        if (err.response?.data?.operationMessage) {
          // API returned an error in payload
          setError(err.response?.data?.operationMessage);
        } else {
          setError(ERROR_MESSAGES.DEFAULT);
        }
    }
  };

  return (
    <div className="container py-4">
      <h2 className="mb-4">Search Family</h2>
      <form onSubmit={handleSearch} className="mb-3">
        <div className="row g-3">
          <div className="col-md-4">
            <input
              type="text"
              className="form-control"
              placeholder="Search Family"
              value={search.searchString}
              onChange={(e) => setSearch({ searchString: e.target.value })}
            />
          </div>
          {/* <div className="col-md-4">
            <input
              type="text"
              className="form-control"
              placeholder="City"
              value={search.city}
              onChange={(e) => setSearch({ ...search, city: e.target.value })}
            />
          </div> */}
          <div className="col-md-4">
            <button className="btn btn-primary w-100" type="submit">Search</button>
          </div>
        </div>
      </form>

      {error && <div className="alert alert-danger">{error}</div>}

      {results.families?.length > 0 && (
        <table className="table table-bordered mt-4">
          <thead>
            <tr>
              <th>Family Name</th>
              <th>Region</th>
              <th>Last Name</th>
              <th>Gotra</th>
              <th>Phone</th>
            </tr>
          </thead>
          <tbody>
            {results.families.map((family) => (
              <tr
                key={family.familyId}
                style={{ cursor: "pointer" }}
                onClick={() => handleRowClick(family.familyId)}
              >
                <td>{family.familyDisplayName}</td>
                <td>{family.region}</td>
                <td>{family.familyName}</td>
                <td>{family.gotra}</td>
                <td>{family.phone}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default FamilySearch;
