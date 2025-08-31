import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axiosInstance";
import ERROR_MESSAGES from "../constants/messages";

const FamilySearch = () => {
  const [search, setSearch] = useState({ searchString: "" });
  const [results, setResults] = useState([]);
  const [error, setError] = useState("");
  const [hasSearched, setHasSearched] = useState(false);
  const navigate = useNavigate();

  const handleRowClick = (id) => {
    navigate(`/family/${id}`);
  };

  const handleSearch = async (e) => {
    e?.preventDefault(); // Prevent page reload if used inside a form
    try {
      const res = await api.post("/family/searchfamily", search);
      setHasSearched(true);
      setResults(res.data);
      setError(null);
    } catch (err) {
      console.error("Search failed to load family details", err);
      if (err.response?.data?.operationMessage) {
        setError(err.response?.data?.operationMessage);
      } else {
        setError(ERROR_MESSAGES.DEFAULT);
      }
    }
  };

  return (
    <div className="container p-4 bg-white rounded mt-4">
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
            <button className="btn btn-primary w-100" type="submit">
              Search
            </button>
          </div>
        </div>
      </form>

      {error && <div className="alert alert-danger">{error}</div>}
      {hasSearched && results.families == null && <div className="alert alert-danger">No family found for selected search criteria</div>}
      {results.families?.length > 0 && (
        <table className="table table-bordered mt-4">
          <thead>
            <tr>
              <th>Head Of Family</th>
              <th>Family Name</th>
              <th>Gotra</th>
              <th>Phone</th>
              <th>Region</th>
            </tr>
          </thead>
          <tbody>
            {results.families.map((family) => (
              <tr
                key={family.familyId}
                style={{ cursor: "pointer" }}
                onClick={() => handleRowClick(family.familyId)}
              >
                <td>{family.headOfFamilyFirstName} {family.headOfFamilyFirstNameInHindi && ` (${family.headOfFamilyFirstNameInHindi})`}</td>
                <td>{family.familyName} {family.familyNameInHindi && ` (${family.familyNameInHindi})`}</td>
                <td>{family.gotra}</td>
                <td>{family.phone}</td>
                <td>{family.region}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default FamilySearch;
