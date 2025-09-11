import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Link } from "react-router-dom";
import { Tree, TreeNode } from "react-organizational-chart";
import { getFormattedPhoneDisplay } from "../utils/phoneUtils";
import "./TreeNode.css";
import "./FamilyDetails.css";
import "bootstrap/dist/css/bootstrap.min.css";
import api from "../api/axiosInstance";
import ERROR_MESSAGES from "../constants/messages";
import FamilyTree from "../components/FamilyTree";
import MemberListTable from "../components/MemberListTable";
import ImageUploadCropModal from "../components/ImageUploadCropModal";


const FamilyDetails = () => {
  const [family, setFamily] = useState(null);
  const [error, setError] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [showImageEdit, setShowImageEdit] = useState(false);
  const [form, setForm] = useState({});
  const [editError, setEditError] = useState("");
  const [editSuccess, setEditSuccess] = useState("");
  const { familyId } = useParams();

  useEffect(() => {
    const fetchFamilyData = async () => {
      try {
        const requestBody = familyId ? { familyId: parseInt(familyId) } : {};
        const response = await api.post(
          "/family/getfamilydetails",
          requestBody,
        );
        setFamily(response.data);
        setForm({
          familyName: response.data.familyDetails.familyName || '',
          familyNameInHindi: response.data.familyDetails.familyNameInHindi || '',
          gotra: response.data.familyDetails.gotra || '',
          email: response.data.familyDetails.email || '',
          phone: response.data.familyDetails.phone || '',
          phoneWhatsappRegistered: response.data.familyDetails.phoneWhatsappRegistered || false,
          familyAddress: { ...response.data.familyDetails.familyAddress } || {},
        });
        setEditMode(false);
      } catch (err) {
        console.error("Failed to load family data", err);
        if (err.response?.data?.operationMessage) {
          // API returned an error in payload
          setError(err.response?.data?.operationMessage);
        } else {
          setError(ERROR_MESSAGES.DEFAULT);
        }
      }
    };
    fetchFamilyData();
  }, [familyId]);

  const handleEditClick = () => setEditMode(true);
  const handleCancelEdit = () => {
    setEditMode(false);
    setEditError("");
    setEditSuccess("");
  };
  const handleFormChange = (e) => {
    const { name, value } = e.target;
    if (name.startsWith('familyAddress.')) {
      setForm((prev) => ({
        ...prev,
        familyAddress: {
          ...prev.familyAddress,
          [name.replace('familyAddress.', '')]: value,
        },
      }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleImageSave = async (croppedImageBlob) => {
    setEditError("");
    setEditSuccess("");
    try {
      const formData = new FormData();
      //formData.append("familyId", family.familyId);
      formData.append("familyId", 1); // TODO: Temporary hardcoded to 1 for testing. Change later.
      formData.append("image", croppedImageBlob, "familyImage.jpeg");
      const response = await api.post(
        "/family/updateFamilyImage",
        formData, {
          headers: { "Content-Type": "multipart/form-data" },
      });
      if (response.data && response.data.uploadedImagePath) {
        setFamily((prev) => ({
          ...prev,
          familyDetails: {
            ...prev.familyDetails,
            familyImage: response.data.uploadedImagePath,
          },
        }));
        setEditSuccess("Family image updated successfully.");
      } else {
        setEditError("Image upload failed. Please try again.");
      }
    } catch (err) {
      setEditError("Image upload failed. Please try again.");
    }
    setShowImageEdit(false);
  };

  const validateForm = () => {
    if (!form.familyName.trim()) return "Family Name is required.";
    if (!form.gotra.trim()) return "Gotra is required.";
    if (!form.familyAddress?.addressLine1?.trim()) return "Address Line 1 is required.";
    if (!form.familyAddress?.city?.trim()) return "City is required.";
    if (!form.familyAddress?.state?.trim()) return "State is required.";
    if (!form.familyAddress?.postalCode?.trim()) return "Postal Code is required.";
    if (!form.familyAddress?.country?.trim()) return "Country is required.";
    return "";
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setEditError("");
    setEditSuccess("");
    const validationMsg = validateForm();
    if (validationMsg) {
      setEditError(validationMsg);
      return;
    }
    try {
      const payload = { ...form, familyId: family.familyId };
      await api.post('/family/updateFamily', payload);
      setEditMode(false);
      setFamily((prev) => ({ ...prev, ...form }));
      setEditSuccess("Family details updated successfully.");
    } catch (err) {
      setEditError('Failed to update family details.');
    }
  };

  const familyInformationEditForm = (
    <form onSubmit={handleSave}>
      <div className="mb-2">
        <span className="fw-semibold me-2">Family Name:</span>
        <input name="familyName" value={form.familyName} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Family Name" />
      </div>
      <div className="mb-2">
        <span className="fw-semibold me-2">Family Name (Hindi):</span>
        <input name="familyNameInHindi" value={form.familyNameInHindi} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Family Name (Hindi)" />
      </div>
      <div className="mb-2">
        <span className="fw-semibold me-2">Gotra:</span>
        <input name="gotra" value={form.gotra} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Gotra" />
      </div>
      <div className="mb-2">
        <span className="fw-semibold me-2">Email:</span>
        <input name="email" value={form.email} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Email" />
      </div>
      <div className="mb-2">
        <span className="fw-semibold me-2">Phone:</span>
        <input name="phone" value={form.phone} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Phone" />
      </div>
      <div className="d-flex">
        <span className="fw-semibold me-2">Address:</span>
        <div style={{ width: '100%' }}>
          Address Line 1: <input name="familyAddress.addressLine1" value={form.familyAddress?.addressLine1 || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Address Line 1" />
          Address Line 2: <input name="familyAddress.addressLine2" value={form.familyAddress?.addressLine2 || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Address Line 2" />
          Address Line 3: <input name="familyAddress.addressLine3" value={form.familyAddress?.addressLine3 || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Address Line 3" />
          District: <input name="familyAddress.district" value={form.familyAddress?.district || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="District" />
          City: <input name="familyAddress.city" value={form.familyAddress?.city || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="City" />
          State: <input name="familyAddress.state" value={form.familyAddress?.state || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="State" />
          Postal Code: <input name="familyAddress.postalCode" value={form.familyAddress?.postalCode || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Postal Code" />
          Country: <input name="familyAddress.country" value={form.familyAddress?.country || ''} onChange={handleFormChange} className="form-control mb-1" placeholder="Country" />
        </div>
      </div>
      {editError && <div className="alert alert-danger py-1 my-2">{editError}</div>}
      {editSuccess && <div className="alert alert-success py-1 my-2">{editSuccess}</div>}
      <div className="mt-3 text-end">
        <button className="btn btn-primary fw-bold me-2" type="submit">Save</button>
        <button className="btn btn-outline-primary btn-sm" onClick={handleCancelEdit}>Cancel</button>
      </div>
    </form>
  );

  if (error) return <div style={{ color: "red" }}>{error}</div>;
  if (!family) return <div>Loading family tree...</div>;
  const familyDetails = family?.familyDetails;


  const showFamilyDetails = (
    <div className="container p-4 bg-white rounded mt-4">
      {/* Family Info Section */}
      <h5 className="mb-3 float-start">Family Information</h5>
      <button
        className="btn btn-primary fw-bold float-end"
        onClick={handleEditClick}
        title="Edit Family Details"
      >
        <i className="bi bi-pencil-square"></i> Edit
      </button>
      <div className="clearfix"></div>
      <div className="card mb-5 p-4 bg-body-secondary border-0">
        <div className="row">
          <div className="col-sm-4">
            <div className="d-flex justify-content-between align-items-center">
              <h4 className="card-title mb-3 pb-2 fw-bold">
                <span className="fw-bold me-2">Family Name:</span>
                <span className="text-black">{familyDetails.familyName} {familyDetails.familyNameInHindi && ` (${familyDetails.familyNameInHindi})`} </span>
              </h4>

            </div>
            <div className="mb-2">
              <span className="fw-bold me-2">Head Of Family:</span>
              <span className="text-black">{familyDetails.headOfFamilyName}</span>
            </div>
            <div className="mb-2">
              <span className="fw-bold me-2">Gotra:</span>
              <span className="text-black">{familyDetails.gotra}</span>
            </div>
            <div className="mb-2">
              <span className="fw-bold me-2">Email:</span>
              <span className="text-black">{familyDetails.email}</span>
            </div>
            <div className="mb-2">
              <span className="fw-bold me-2">Phone:</span>
              <span className="text-black">
                {getFormattedPhoneDisplay(
                  familyDetails.phone,
                  familyDetails.phoneWhatsappRegistered,
                )}
              </span>
            </div>
          </div>
          <div className="col-sm-4">
            <div className="d-flex  mt-5">
              <span className="fw-semibold me-2">Address:</span>
              <address className="mb-0">
                {familyDetails.familyAddress?.addressLine1}
                <br />
                {familyDetails.familyAddress?.addressLine2 && (
                  <>
                    {familyDetails.familyAddress.addressLine2}
                    <br />
                  </>
                )}
                {familyDetails.familyAddress?.addressLine3 && (
                  <>
                    {familyDetails.familyAddress.addressLine3}
                    <br />
                  </>
                )}
                {familyDetails.familyAddress?.district && (
                  <>
                    {" "}
                    District: {familyDetails.familyAddress.district}
                    <br />
                  </>
                )}
                {familyDetails.familyAddress?.city}, {familyDetails.familyAddress?.state} -{" "}
                {familyDetails.familyAddress?.postalCode.trim()}
                <br />
                {familyDetails.familyAddress?.country}
              </address>
            </div>
          </div>
          <div className="col-sm-4">
            {/* Family Image (Right) */}
            {familyDetails.familyImage && (
              <div className="text-end" style={{ flex: "0 0 auto" }}>
                <img
                  src={`/${familyDetails.familyImage}`}
                  alt="Family"
                  style={{
                    width: "100%",
                    height: "205px",
                    objectFit: "cover",
                    borderRadius: "8px",
                  }}
                />
                <button
                  className="btn btn-outline-primary btn-sm mt-2"
                  onClick={() => setShowImageEdit(true)}
                  style={{  }}
                >
                  <i className="bi bi-pencil-square"></i> Edit Image
                </button>
              </div>
            )}
            {showImageEdit && (
              <ImageUploadCropModal
                onClose={() => setShowImageEdit(false)}
                onSave={handleImageSave}
              />
            )}
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
        {editMode ? familyInformationEditForm : showFamilyDetails}
      </div>
      {/* Tree View */}
      <div className="mb-4">
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
          <FamilyTree familyTreeRoot={family.familyRoot} />
        </div>

        <div className="mb-5">
          {/* This div added just to add space before below table.*/}
        </div>
        {/* Members List */}
        <MemberListTable membersList={family?.memberList} familyNameInHindi={family?.familyDetails?.familyNameInHindi} />
      </div>
    </div>
  );
};

export default FamilyDetails;
