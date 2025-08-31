import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Tree, TreeNode } from "react-organizational-chart";
import api from "../api/axiosInstance";
import { getFormattedPhoneDisplay } from "../utils/phoneUtils";
import "./TreeNode.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { Link } from "react-router-dom";
import ERROR_MESSAGES from "../constants/messages";
import FamilyTree from "../components/FamilyTree";
import MemberListTable from "../components/MemberListTable";
import { genderOptions, maritalStatusOptions, monthOptions } from "../constants/DropdownConstants";

const MemberProfile = () => {
  const { id } = useParams(); // from route: /member/:id
  const [memberData, setMemberData] = useState(null);
  const [error, setError] = useState("");
  const [editMode, setEditMode] = useState(false);
  const [form, setForm] = useState({});
  const [editError, setEditError] = useState("");
  const [editSuccess, setEditSuccess] = useState("");
  
  useEffect(() => {
    const fetchMember = async () => {
      try {
        const requestBody = id ? { memberId: parseInt(id) } : {};
        const res = await api.post("/family/getmemberprofile", requestBody);
        setMemberData(res.data);
        setError("");
      } catch (err) {
        console.error("Failed to fetch member data", err);
        if (err.response?.data?.operationMessage) {
          // API returned an error in payload
          setError(err.response?.data?.operationMessage);
        } else {
          setError(ERROR_MESSAGES.DEFAULT);
        }
      }
    };
    fetchMember();
  }, [id]);

  const handleEditClick = () => setEditMode(true);

  const handleCancelEdit = () => {
    setEditMode(false);
    setEditError("");
    setForm({ ...memberProfile });
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const validateForm = () => {
    if (!form.firstName?.trim()) return "First Name is required.";
    if (!form.lastName?.trim()) return "Last Name is required.";
    if (!form.gender?.trim()) return "Gender is required.";
    if (!form.maritalStatus?.trim()) return "Marital Status is required.";
    if (!form.birthDay?.trim() || !form.birthMonth?.trim() || !form.birthYear?.trim()) return "Complete Birth Date is required.";
    if (!form.phone?.trim()) return "Phone is required.";
    if (!form.email?.trim()) return "Email is required.";
    if (!form.educationDetails?.trim()) return "Education Details are required.";
    if (!form.occupation?.trim()) return "Occupation is required.";
    return "";
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setEditError("");
    const validationMsg = validateForm();
    if (validationMsg) {
      setEditError(validationMsg);
      return;
    }
    try {
      await api.post("/family/updatememberprofile", { ...form, memberId: memberProfile.memberId });
      setEditMode(false);
      setMemberData((prev) => ({ ...prev, memberProfile: { ...form } }));
      setEditSuccess("Family member details updated successfully.");
    } catch (err) {
      setEditError("Failed to update member details.");
    }
  };

  if (error) return <div className="text-danger p-4">{error}</div>;
  if (!memberData) return <div className="p-4">Loading member profile...</div>;
  const { memberProfile } = memberData;
  


  const memberInformationEditForm = (
    <form onSubmit={handleSave} className="row g-3">
    <div className="col-md-4">
      <label className="form-label fw-semibold">First Name</label>
      <input name="firstName" value={form.firstName || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">First Name (Hindi)</label>
      <input name="firstNameInHindi" value={form.firstNameInHindi || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Maiden Last Name</label>
      <input name="maidenLastName" value={form.maidenLastName || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Nick Name</label>
      <input name="nickName" value={form.nickName || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Nick Name (Hindi)</label>
      <input name="nickNameInHindi" value={form.nickNameInHindi || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Phone</label>
      <input name="phone" value={form.phone || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Gender</label>
      <select name="gender" value={form.gender || ''} onChange={handleFormChange} className="form-select" required>
        {genderOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
      </select>
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Marital Status</label>
      <select name="maritalStatus" value={form.maritalStatus || ''} onChange={handleFormChange} className="form-select" required>
        {maritalStatusOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
      </select>
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Wedding Date</label>
      <input name="weddingDate" type="date" value={form.weddingDate || ''} onChange={handleFormChange} className="form-control" required={form.maritalStatus === 'Married'} />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Birth Day</label>
      <input name="birthDay" type="number" min="1" max="31" value={form.birthDay || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Birth Month</label> 
      <select name="birthMonth" value={form.birthMonth || ''} onChange={handleFormChange} className="form-select" required>
        {monthOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
      </select>
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Birth Year</label>
      <input name="birthYear" type="number" min="1900" max={new Date().getFullYear()} value={form.birthYear || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Email</label>
      <input name="email" type="email" value={form.email || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Education Details</label>
      <input name="educationDetails" value={form.educationDetails || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    <div className="col-md-4">
      <label className="form-label fw-semibold">Occupation</label>
      <input name="occupation" value={form.occupation || ''} onChange={handleFormChange} className="form-control" required />
    </div>
    {editError && <div className="alert alert-danger py-1 my-2 col-12">{editError}</div>}
    {editSuccess && <div className="alert alert-success py-1 my-2 col-12">{editSuccess}</div>}
    <div className="col-12 mt-2">
      <button className="btn btn-success btn-sm me-2" type="submit">Save</button>
      <button className="btn btn-secondary btn-sm" type="button" onClick={handleCancelEdit}>Cancel</button>
    </div>
  </form>
  );

  const memberReadOnlyView = (
    <div className="row mb-3 align-items-stretch">
      <div className="col-md-8 d-flex flex-column">
        <div className="mb-2">
          <span className="fw-semibold me-2">Name :</span>
          <span className="text-secondary">{memberProfile.firstName} {memberProfile.firstNameInHindi && ` (${memberProfile.firstNameInHindi}) `} {memberProfile.lastName}</span>
        </div>
        {memberProfile.maidenLastName && (
          <div className="mb-2">
            <span className="fw-semibold me-2">Maiden Last Name :</span>
            <span className="text-secondary">{memberProfile.maidenLastName}</span>
          </div>
        )}
        {memberProfile.nickName && (
          <div className="mb-2">
            <span className="fw-semibold me-2">Nick Name :</span>
            <span className="text-secondary">{memberProfile.nickName} {memberProfile.nickNameInHindi && ` (${memberProfile.nickNameInHindi}) `}</span>
          </div>
        )}
        <div className="mb-2">
          <span className="fw-semibold me-2">Gender :</span>
          <span className="text-secondary">{memberProfile.gender}</span>
        </div>
        <div className="mb-2">
          <span className="fw-semibold me-2">Marital Status :</span>
          <span className="text-secondary">{memberProfile.maritalStatus} {memberProfile.weddingDate && ` married on ${memberProfile.weddingDate}`}</span>
        </div>
        <div className="mb-2">
          <span className="fw-semibold me-2">Birth Date :</span>
          <span className="text-secondary"> 🎂 {memberProfile.birthDay} {memberProfile.birthMonth} {memberProfile.birthYear}</span>
        </div>
        {memberProfile.phone && (
          <div className="mb-2">
            <span className="fw-semibold me-2">Phone :</span>
            <span className="text-secondary">{getFormattedPhoneDisplay(
              memberProfile.phone,
              memberProfile.phoneWhatsappRegistered,
            )}</span>
          </div>
        )}
        {memberProfile.email && (
          <div className="mb-2">
            <span className="fw-semibold me-2">Email Id :</span>
            <span className="text-secondary">✉️ {memberProfile.email}</span>
          </div>
        )}
        {memberProfile.educationDetails && (
          <div className="mb-2">
            <span className="fw-semibold me-2">Education details :</span>
            <span className="text-secondary"> {memberProfile.educationDetails}</span>
          </div>
        )}
        {memberProfile.occupation && (
          <div className="mb-2">
            <span className="fw-semibold me-2">Occupation :</span>
            <span className="text-secondary"> {memberProfile.occupation}</span>
          </div>
        )}
        <div className="mt-auto d-flex align-items-end" style={{ minHeight: '60px' }}>
          <div>
            <button
              className="btn btn-primary me-2"
              onClick={() => window.location.href = `/family/${memberProfile.familyId}`}
              disabled={!memberProfile.familyId}
            >
              View {memberProfile.firstName}'s Family
            </button>
            <button
              className="btn btn-outline-primary btn-sm ms-2"
              onClick={handleEditClick}
              title="Edit Member Details"
            >
              <i className="bi bi-pencil-square"></i> Edit
            </button>
          </div>
        </div>
      </div>
      <div className="col-md-4 d-flex flex-column justify-content-end align-items-end">
        <img
          src={`/${memberProfile.profileImage}`}
          alt={memberProfile.firstName}
          className="me-3"
          style={{ width: "360px", height: "360px", objectFit: "cover", borderRadius: "8px" }}
        />
      </div>
    </div>
  );

  return (
    <div className="container py-4">
      <div className="card mb-4">
        <div className="card-header bg-info text-white">
          <h4 className="mb-0">Member Profile</h4>
        </div>
        <div className="card-body">
          {editMode ? memberInformationEditForm : memberReadOnlyView}
        </div>
      </div>

      {/* Tree View */}
      <h5 className="mb-3">Family Tree</h5>
      <div
        className="tree-container border"
        style={{
          width: "100%",
          minHeight: "600px",
          overflow: "auto",
          padding: "10px",
        }}
      >
        <FamilyTree familyTreeRoot={memberData.familyRoot} />
      </div>
      
      <div className="mb-4">
        <MemberListTable membersList={memberData?.memberList} />
      </div>

    </div>
  );
};

export default MemberProfile;
