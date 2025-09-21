import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axiosInstance";
import { getFormattedPhoneDisplay } from "../utils/phoneUtils";
import "./TreeNode.css";
import "bootstrap/dist/css/bootstrap.min.css";
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
        setForm({
          ...res.data?.memberProfile,
          memberAddress: {...res.data?.memberProfile?.memberAddress} || {},
        });
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
    <form onSubmit={handleSave}>
      {/* Member Profile Section */}
      <div className="row">
      <h5 className="mb-3 float-start">Edit {memberProfile.firstName} {memberProfile.lastName}'s Profile</h5>
      </div>
      <div className="card mb-5 p-4 bg-body-secondary border-0"> 
      <div className="row">
        <div className="col-sm-4">
          <span className="fw-semibold me-2">First Name:</span>
          <input name="firstName" value={form.firstName || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="First Name" required />
        </div>
        <div className="col-sm-4">
          <span className="fw-semibold me-2">First Name (Hindi)</span>
          <input name="firstNameInHindi" value={form.firstNameInHindi || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="First Name (Hindi)" />
        </div>
        <div className="col-sm-4">
          <span className="fw-semibold me-2">Maiden Last Name</span>
          <input name="maidenLastName" value={form.maidenLastName || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Maiden Last Name" />
        </div>
      </div>
      <div className="row">
        <div className="col-sm-4">
          <span className="fw-semibold me-2">Nick Name</span>
          <input name="nickName" value={form.nickName || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="" />
        </div>
        <div className="col-sm-4">
          <span className="fw-semibold me-2">Nick Name (Hindi)</span>
          <input name="nickNameInHindi" value={form.nickNameInHindi || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="" />
        </div>
        <div className="col-md-4">
          <span className="fw-semibold me-2">Phone</span>
          <input name="phone" value={form.phone || ''} onChange={handleFormChange} className="form-control d-inline w-auto" required placeholder="Phone" />
        </div>
      </div>

      <div className="row">
        <div className="col-md-4">
          <span className="fw-semibold me-2">Gender</span>
          <select name="gender" value={form.gender || ''} onChange={handleFormChange} className="form-select" required placeholder="Gender">
            {genderOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
          </select>
        </div>
        <div className="col-md-4">
          <span className="fw-semibold me-2">Marital Status</span>
          <select name="maritalStatus" value={form.maritalStatus || ''} onChange={handleFormChange} className="form-select" required placeholder="Marital Status">
            {maritalStatusOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
          </select>
        </div>
        <div className="col-md-4">
          <span className="fw-semibold me-2">Wedding Date</span>
          <input name="weddingDate" type="date" value={form.weddingDate || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Wedding Date" />
        </div>
      </div>

      <div className="row">
        <div className="col-md-4">
          <span className="fw-semibold me-2">Birth Day</span>
          <select name="birthDay" value={form.birthDay || ''} onChange={handleFormChange} className="form-select" placeholder="Birth Day">
            {dayOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
          </select>
        </div>
        <div className="col-md-4">
          <span className="fw-semibold me-2">Birth Month</span>
          <select name="birthMonth" value={form.birthMonth || ''} onChange={handleFormChange} className="form-select" required placeholder="Birth Month">
            {monthOptions.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
          </select>
        </div>
        <div className="col-md-4">
          <span className="fw-semibold me-2">Birth Year</span>
          <input name="birthYear" type="number" min="1900" max={new Date().getFullYear()} value={form.birthYear || ''} onChange={handleFormChange} className="form-control d-inline w-auto" required placeholder="Birth Year" />
        </div>
      </div>

      <div className="row">
        <div className="col-md-4">
          <span className="fw-semibold me-2">Email</span>
          <input name="email" type="email" value={form.email || ''} onChange={handleFormChange} className="form-control d-inline w-auto" disabled={!!memberProfile.email} placeholder="Email" />
        </div>
      </div>
      <div className="row">
        <div className="col-md-4">
          <span className="fw-semibold me-2">Education Details</span>
          <input name="educationDetails" value={form.educationDetails || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Education Details" />
        </div>
        <div className="col-md-4">
          <span className="fw-semibold me-2">Occupation</span>
          <input name="occupation" value={form.occupation || ''} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Occupation" />
        </div>
      </div>
      <div className="d-flex">
        <span className="fw-semibold me-2">Member Address:</span>
        <div className="row">
          <div className="col-sm-4">
            <span className="fw-semibold me-2">Address Line 1</span>
            <input name="memberAddress.addressLine1" value={form.memberAddress?.addressLine1 || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Address Line 1" />
          </div>
          <div className="col-sm-4">
            <span className="fw-semibold me-2">Address Line 2</span>
            <input name="memberAddress.addressLine2" value={form.memberAddress?.addressLine2 || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Address Line 2" />
          </div>
          <div className="col-sm-4">
            <span className="fw-semibold me-2">Address Line 3</span>
            <input name="memberAddress.addressLine3" value={form.memberAddress?.addressLine3 || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Address Line 3" />
          </div>
        </div>
        <div className="row">
          <div className="col-sm-4">
            <span className="fw-semibold me-2">District</span>
            <input name="memberAddress.district" value={form.memberAddress?.district || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="District" />
          </div>
          <div className="col-sm-4">
            <span className="fw-semibold me-2">City</span>
            <input name="memberAddress.city" value={form.memberAddress?.city || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="City" />
          </div>
          <div className="col-sm-4">
            <span className="fw-semibold me-2">State</span>
            <input name="memberAddress.state" value={form.memberAddress?.state || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="State" />
          </div>
          <div className="col-sm-4">
            <span className="fw-semibold me-2">Postal Code</span>
            <input name="memberAddress.postalCode" value={form.memberAddress?.postalCode || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Postal Code" />
          </div>
          <div className="col-sm-4">
            <span className="fw-semibold me-2">Country</span>
            <input name="memberAddress.country" value={form.memberAddress?.country || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Country" />
          </div>
        </div>
      </div>

      {editError && <div className="alert alert-danger py-1 my-2 col-12">{editError}</div>}
      {editSuccess && <div className="alert alert-success py-1 my-2 col-12">{editSuccess}</div>}
      <div className="mt-3 text-end">
        <button className="btn btn-primary fw-bold me-2" type="submit">Save</button>
        <button className="btn btn-outline-primary btn-sm" type="button" onClick={handleCancelEdit}>Cancel</button>
      </div>
      </div>
    </form>
  );

  const memberReadOnlyView = (
    <div className="container p-4 bg-white rounded mt-4">
      {/* Member Profile Section */}
      <h5 className="mb-3 float-start">{memberProfile.firstName} {memberProfile.firstNameInHindi && ` (${memberProfile.firstNameInHindi}) `} {memberProfile.lastName} Profile</h5>
      {memberProfile.canUpdateMember && (
        <button
          className="btn btn-primary fw-bold float-end"
          onClick={handleEditClick}
          title="Edit Member Details"
        >
          <i className="bi bi-pencil-square"></i> Edit Profile
        </button>
      )}
      <div className="clearfix"></div>
      <div className="card mb-5 p-4 bg-body-secondary border-0">
        <div className="row">
          <div className="col-sm-4">
            {/* Member Image (Right) */}
            {memberProfile.profileImage && (
              <div className="text-end" style={{ flex: "0 0 auto" }}>
                <img
                  src={`/${memberProfile.profileImage}`}
                  alt={memberProfile.firstName}
                  style={{ width: "100%", height: "360px", objectFit: "cover", borderRadius: "8px" }}
                />
                {memberProfile.canUpdateMember && (
                  <button
                    className="btn btn-outline-primary btn-sm mt-2"
                    onClick={() => setShowImageEdit(true)}
                    style={{}}
                  >
                    <i className="bi bi-pencil-square"></i> Edit Image
                  </button>
                )}
              </div>
            )}
            {showImageEdit && (
              <ImageUploadCropModal
                onClose={() => setShowImageEdit(false)}
                onSave={handleImageSave}
              />
            )}
          </div>
          <div className="col-sm-4">
            {/* <div className="d-flex justify-content-between align-items-center">
            <h4 className="card-title mb-3 pb-2 fw-bold">
              <span className="fw-bold me-2">Member Name:</span>
              <span className="text-black">{memberProfile.firstName} {memberProfile.firstNameInHindi && ` (${memberProfile.firstNameInHindi}) `} {memberProfile.lastName}</span>
            </h4>
          </div> */}
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
          </div>
          <div className="col-sm-4">

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
            )} { /*End of Member Address */}
            {/* TODO: Currently backend system does not return family addrress, so member is used as filler*/}
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
            )} { /*End of Member Family Address */}

          </div>
          <div className="mt-auto d-flex align-items-end" style={{ minHeight: '60px' }}>
            <div className="text-end w-100">
              <button
                className="btn btn-primary me-2"
                onClick={() => window.location.href = `/family/${memberProfile.familyId}`}
                disabled={!memberProfile.familyId}
              >
                View {memberProfile.firstName}'s Family
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="container p-4 bg-white rounded mt-4">
      <div
        className="card-body d-flex flex-wrap align-items-start p-0"
        style={{ gap: "1rem" }}
      >
      {editMode ? memberInformationEditForm : memberReadOnlyView}
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
