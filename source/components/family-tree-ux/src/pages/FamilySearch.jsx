import React, { useState } from "react";
import api from "../api/axiosInstance";

const FamilySearch = () => {
  const [search, setSearch] = useState({ familyName: "", city: "" });
  const [results, setResults] = useState([]);
  const [error, setError] = useState("");

  const handleSearch = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await api.post("/family/search", search);
      setResults(res.data);
    } catch (err) {
      setError("Search failed. Please try again.");
      console.error("Search Error:", err);
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
              placeholder="Family Name"
              value={search.familyName}
              onChange={(e) => setSearch({ ...search, familyName: e.target.value })}
            />
          </div>
          <div className="col-md-4">
            <input
              type="text"
              className="form-control"
              placeholder="City"
              value={search.city}
              onChange={(e) => setSearch({ ...search, city: e.target.value })}
            />
          </div>
          <div className="col-md-4">
            <button className="btn btn-primary w-100" type="submit">Search</button>
          </div>
        </div>
      </form>

      {error && <div className="alert alert-danger">{error}</div>}

      {results.length > 0 && (
        <table className="table table-bordered mt-4">
          <thead>
            <tr>
              <th>Family Name</th>
              <th>Gotra</th>
              <th>Phone</th>
              <th>Email</th>
              <th>City</th>
            </tr>
          </thead>
          <tbody>
            {results.map((family) => (
              <tr key={family.familyId}>
                <td>{family.familyName}</td>
                <td>{family.gotra}</td>
                <td>{family.phone}</td>
                <td>{family.email}</td>
                <td>{family.familyAddress?.city}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default FamilySearch;
