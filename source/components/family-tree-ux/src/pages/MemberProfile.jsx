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
import { genderOptions, maritalStatusOptions, monthOptions, dayOptions } from "../constants/DropdownConstants";
import ImageUploadCropModal from "../components/ImageUploadCropModal";

const MemberProfile = () => {
  const { id } = useParams(); // from route: /member/:id
  const [memberData, setMemberData] = useState(null);
  const [error, setError] = useState("");
  const [editMode, setEditMode] = useState(false);
  const [form, setForm] = useState({});
  const [editError, setEditError] = useState("");
  const [editSuccess, setEditSuccess] = useState("");
  const [showImageEdit, setShowImageEdit] = useState(false);

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
    if (!form.gender?.trim()) return "Gender is required.";
    if (!form.maritalStatus?.trim()) return "Marital Status is required.";
    if (!form.birthMonth?.trim() || !form.birthYear?.trim()) return "Complete Birth Date is required.";
    if (!form.phone?.trim()) return "Phone is required.";
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

  const handleImageSave = async (croppedImageBlob) => {
    setEditError("");
    setEditSuccess("");
    try {
      const formData = new FormData();
      formData.append("memberId", 1);
      formData.append("image", croppedImageBlob, "family-image.jpg");
      const response = await api.post(
        "/family/uploadMemberImage",
        formData, {
          headers: { "Content-Type": "multipart/form-data" },
      });

      if (response.data && response.data.imagePath) {
        setMemberData((prev) => ({
          ...prev,
          memberProfile: {
            ...prev.memberProfile,
            memberProfileImage: response.data.imagePath,
          },
        }));
        setEditSuccess("Member image updated successfully.");
      } else {
        setEditError("Image upload failed. Please try again.");
      }
    } catch (err) {
      setEditError("Image upload failed. Please try again.");
    }
    setShowImageEdit(false);
  };

  if (error) return <div className="text-danger p-4">{error}</div>;
  if (!memberData) return <div className="p-4">Loading member profile...</div>;
  const { memberProfile } = memberData;



  const memberInformationEditForm = (
    <form onSubmit={handleSave} className="row g-3">
      <div className="mb-2">
        <span className="fw-semibold me-2">First Name:</span>
        <input name="firstName" value={form.firstName || ''} onChange={handleFormChange} className="form-control" required />
      </div>
      <div className="col-md-4">
        <label className="form-label fw-semibold">First Name (Hindi)</label>
        <input name="firstNameInHindi" value={form.firstNameInHindi || ''} onChange={handleFormChange} className="form-control" />
      </div>
      <div className="col-md-4">
        <label className="form-label fw-semibold">Maiden Last Name</label>
        <input name="maidenLastName" value={form.maidenLastName || ''} onChange={handleFormChange} className="form-control" />
      </div>
      <div className="col-md-4">
        <label className="form-label fw-semibold">Nick Name</label>
        <input name="nickName" value={form.nickName || ''} onChange={handleFormChange} className="form-control" />
      </div>
      <div className="col-md-4">
        <label className="form-label fw-semibold">Nick Name (Hindi)</label>
        <input name="nickNameInHindi" value={form.nickNameInHindi || ''} onChange={handleFormChange} className="form-control" />
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
        <input name="weddingDate" type="date" value={form.weddingDate || ''} onChange={handleFormChange} className="form-control" />
      </div>
      <div className="col-md-4">
        <label className="form-label fw-semibold">Birth Day</label>
        <select name="birthDay" value={form.birthDay || ''} onChange={handleFormChange} className="form-select">
          {dayOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
        </select>
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
        <input name="email" type="email" value={form.email || ''} onChange={handleFormChange} className="form-control" disabled={!!memberProfile.email} />
      </div>
      <div className="col-md-4">
        <label className="form-label fw-semibold">Education Details</label>
        <input name="educationDetails" value={form.educationDetails || ''} onChange={handleFormChange} className="form-control" />
      </div>
      <div className="col-md-4">
        <label className="form-label fw-semibold">Occupation</label>
        <input name="occupation" value={form.occupation || ''} onChange={handleFormChange} className="form-control" />
      </div>

      <span className="fw-semibold me-2">Address:</span>
      <div className="col-md-4" style={{ width: '100%' }}>
        Address Line 1: <input name="familyAddress.addressLine1" value={form.familyAddress?.addressLine1 || ''} onChange={handleFormChange} className="form-control" placeholder="Address Line 1" />
        Address Line 2: <input name="familyAddress.addressLine2" value={form.familyAddress?.addressLine2 || ''} onChange={handleFormChange} className="form-control" placeholder="Address Line 2" />
        Address Line 3: <input name="familyAddress.addressLine3" value={form.familyAddress?.addressLine3 || ''} onChange={handleFormChange} className="form-control" placeholder="Address Line 3" />
        District: <input name="familyAddress.district" value={form.familyAddress?.district || ''} onChange={handleFormChange} className="form-control" placeholder="District" />
        City: <input name="familyAddress.city" value={form.familyAddress?.city || ''} onChange={handleFormChange} className="form-control" placeholder="City" />
        State: <input name="familyAddress.state" value={form.familyAddress?.state || ''} onChange={handleFormChange} className="form-control" placeholder="State" />
        Postal Code: <input name="familyAddress.postalCode" value={form.familyAddress?.postalCode || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Postal Code" />
        Country: <input name="familyAddress.country" value={form.familyAddress?.country || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Country" />
      </div>

      {editError && <div className="alert alert-danger py-1 my-2 col-12">{editError}</div>}
      {editSuccess && <div className="alert alert-success py-1 my-2 col-12">{editSuccess}</div>}
      <div className="col-12 mt-2">
        <button className="btn btn-primary fw-bold me-2" type="submit">Save</button>
        <button className="btn btn-outline-primary btn-sm" type="button" onClick={handleCancelEdit}>Cancel</button>
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
        {memberProfile.memberAddress && (
          <div className="d-flex  mt-5">
            <span className="fw-semibold me-2">Member Address:</span>
            <address className="mb-0">
              {memberProfile.memberAddress?.addressLine1}
              <br />
              {memberProfile.memberAddress?.addressLine2 && (
                <>
                  {memberProfile.memberAddress.addressLine2}
                  <br />
                </>
              )}
              {memberProfile.memberAddress?.addressLine3 && (
                <>
                  {memberProfile.memberAddress.addressLine3}
                  <br />
                </>
              )}
              {memberProfile.memberAddress?.district && (
                <>
                  {" "}
                  District: {memberProfile.memberAddress.district}
                  <br />
                </>
              )}
              {memberProfile.memberAddress?.city}, {memberProfile.memberAddress?.state} -{" "}
              {memberProfile.memberAddress?.postalCode.trim()}
              <br />
              {memberProfile.memberAddress?.country}
            </address>
          </div>
        )}
        {memberProfile.memberAddress && (
          <div className="d-flex  mt-5">
            <span className="fw-semibold me-2">Family Address:</span>
            <address className="mb-0">
              {memberProfile.memberAddress?.addressLine1}
              <br />
              {memberProfile.memberAddress?.addressLine2 && (
                <>
                  {memberProfile.memberAddress.addressLine2}
                  <br />
                </>
              )}
              {memberProfile.memberAddress?.addressLine3 && (
                <>
                  {memberProfile.memberAddress.addressLine3}
                  <br />
                </>
              )}
              {memberProfile.memberAddress?.district && (
                <>
                  {" "}
                  District: {memberProfile.memberAddress.district}
                  <br />
                </>
              )}
              {memberProfile.memberAddress?.city}, {memberProfile.memberAddress?.state} -{" "}
              {memberProfile.memberAddress?.postalCode.trim()}
              <br />
              {memberProfile.memberAddress?.country}
            </address>
          </div>
        )}

        <div className="mt-auto d-flex align-items-end" style={{ minHeight: '60px' }}>
          <div className="text-end w-100">
            <button
              className="btn btn-primary me-2"
              onClick={() => window.location.href = `/family/${memberProfile.familyId}`}
              disabled={!memberProfile.familyId}
            >
              View {memberProfile.firstName}'s Family
            </button>
            {memberProfile.canUpdateMember && (
            <button
              className="btn btn-outline-primary btn-sm ms-2"
              onClick={handleEditClick}
              title="Edit Member Details" style={{ background: "transparent", borderColor: "#A42502", color: "#A42502" }}
            >
              <i className="bi bi-pencil-square"></i> Edit
            </button>
            )}
          </div>
        </div>
      </div>
      <div className="col-md-4 d-flex flex-column  align-items-end">
        <img
          src={`/${memberProfile.profileImage}`}
          alt={memberProfile.firstName}
          className="mb-3"
          style={{ width: "100%", height: "360px", objectFit: "cover", borderRadius: "8px" }}
        />
        {memberProfile.canUpdateMember && (
        <button
          className="btn btn-outline-primary btn-sm mt-2"
          onClick={() => setShowImageEdit(true)}
          style={{  }}
        >
          <i className="bi bi-pencil-square"></i> Edit Image
        </button>
        )}
        {showImageEdit && (
          <ImageUploadCropModal
            onClose={() => setShowImageEdit(false)}
            onSave={handleImageSave}
          />
        )}
      </div>
    </div>
  );

  return (
    <div className="container p-4 bg-white rounded mt-4">
      <div className="card mb-4" style={{ borderColor: "#FFDCB0" }}>
        <div className="card-header text-black" style={{ background: "#FFDCB0" }}>
          <h5 className="mb-0">Member Profile</h5>
        </div>
        <div className="card-body" style={{ borderColor: "#FFDCB0" }}>
          {editMode ? memberInformationEditForm : memberReadOnlyView}
        </div>
      </div>

      {/* Tree View */}
      <h5 className="mb-3">Family Tree</h5>
        <div
          className="tree-container"
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
