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

const getImagePath = (url) => {
  if (!url) return "/sample-male2.jpeg";
  return `/${url}`;
};

const MemberCard = ({ member }) => (
  <Link to={`/member/${member.memberId}`} className="text-decoration-none">
    <div
      className={`member-card d-flex align-items-center p-2 ${member.selectedNode ? "root-node" : ""}`}
      style={{ minWidth: "180px", borderRadius: "8px" }}
    >
      {member.profileImage && (
        <img
          src={getImagePath(member.profileImage)}
          alt={`${member.firstName}`}
          className="mb-2"
          style={{
            width: "60px",
            height: "60px",
            borderRadius: "10%",
            objectFit: "cover",
            marginRight: "10px",
          }}
        />
      )}
      <div className="card-body p-2">
        <h6 className="card-title mb-1">
          {member.firstName} {member.lastName}
        </h6>
        {/* <p className="card-text small text-muted">{member.occupation}</p>
      <p className="card-text small">📞 {member.phone}</p> */}
      </div>
    </div>
  </Link>
);

const CoupleNode = ({ member }) => (
  <div className="d-flex justify-content-center gap-2">
    <MemberCard member={member} />
    {member.spouse && <MemberCard member={member.spouse} />}
  </div>
);

const MemberNode = ({ member }) => (
  <TreeNode label={<CoupleNode member={member} />}>
    {member.children &&
      member.children.map((child) => (
        <MemberNode key={child.memberId} member={child} />
      ))}
  </TreeNode>
);

function flattenFamilyTree(root) {
  const members = [];

  function traverse(member) {
    members.push({
      memberId: member.memberId,
      firstName: member.firstName,
      firstNameInHindi: member.firstNameInHindi,
      lastName: member.lastName,
      phone: member.phone || "",
      email: member.email || "",
      occupation: member.occupation || "",
      relationship: member.familyRelationship,
      phoneWhatsappRegistered: member.phoneWhatsappRegistered,
      maritalStatus: member.maritalStatus,
      birthDate: member.birthDate,
      educationDetails: member.educationDetails,
      workingAt: member.workingAt,
    });

    if (member.spouse) {
      members.push({
        memberId: member.spouse.memberId,
        firstName: member.spouse.firstName,
        firstNameInHindi: member.spouse.firstNameInHindi,
        lastName: member.spouse.lastName,
        phone: member.spouse.phone || "",
        email: member.spouse.email || "",
        occupation: member.spouse.occupation || "",
        relationship: member.spouse.familyRelationship,
        phoneWhatsappRegistered: member.spouse.phoneWhatsappRegistered,
        maritalStatus: member.spouse.maritalStatus,
        birthDate: member.spouse.birthDate,
        educationDetails: member.spouse.educationDetails,
        workingAt: member.spouse.workingAt,
      });
    }

    if (member.children) {
      member.children.forEach((child) => {
        traverse(child);
      });
    }
  }

  traverse(root);
  return members;
}

const FamilyDetails = () => {
  const [family, setFamily] = useState(null);
  const [error, setError] = useState(null);
  const [editMode, setEditMode] = useState(false);
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
          familyName: response.data.familyName || '',
          familyNameInHindi: response.data.familyNameInHindi || '',
          headOfFamilyName: response.data.headOfFamilyName || '',
          gotra: response.data.gotra || '',
          email: response.data.email || '',
          phone: response.data.phone || '',
          phoneWhatsappRegistered: response.data.phoneWhatsappRegistered || false,
          familyAddress: { ...response.data.familyAddress } || {},
        });
      } catch (err) {
        console.error("Failed to load family data", err);
        if (err.response?.data?.operationMessage) {
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
  const validateForm = () => {
    if (!form.familyName.trim()) return "Family Name is required.";
    if (!form.headOfFamilyName.trim()) return "Head Of Family is required.";
    if (!form.gotra.trim()) return "Gotra is required.";
    if (!form.email.trim()) return "Email is required.";
    if (!form.phone.trim()) return "Phone is required.";
    if (!form.familyAddress?.addressLine1?.trim()) return "Address Line 1 is required.";
    if (!form.familyAddress?.city?.trim()) return "City is required.";
    if (!form.familyAddress?.state?.trim()) return "State is required.";
    if (!form.familyAddress?.postalCode?.trim()) return "Postal Code is required.";
    if (!form.familyAddress?.country?.trim()) return "Country is required.";
    return "";
  };

  const handleSave = async () => {
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
        <span className="fw-semibold me-2">Head Of Family:</span>
        <input name="headOfFamilyName" value={form.headOfFamilyName} onChange={handleFormChange} className="form-control d-inline w-auto" placeholder="Head Of Family" readOnly/>
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
      <div className="mt-2">
        <button className="btn btn-success btn-sm me-2" onClick={handleSave}>Save</button>
        <button className="btn btn-secondary btn-sm" onClick={handleCancelEdit}>Cancel</button>
      </div>
    </form>
  );

  if (error) return <div style={{ color: "red" }}>{error}</div>;
  if (!family) return <div>Loading family tree...</div>;
  const membersList = flattenFamilyTree(family.familyTreeRoot);


  return (
    <div className="container py-4">
      {/* Family Info Section */}
      <h5 className="mb-3">Family Information</h5>
      <div className="card mb-4" style={{ backgroundColor: "#e7f3ff" }}>
        <div
          className="card-body d-flex flex-wrap align-items-start"
          style={{ gap: "1rem" }}
        >
        {editMode
          ? familyInformationEditForm
          : (
            <div
          className="card-body d-flex flex-wrap align-items-start"
          style={{ gap: "1rem" }}
        >
          <div style={{ flex: "1 1 10%" }}>
            <div className="d-flex justify-content-between align-items-center">
            <h4 className="card-title mb-3">
              <span className="fw-semibold me-2">Family Name:</span>
              <span className="text-secondary">{family.familyName} {family.familyNameInHindi && ` (${family.familyNameInHindi})`} </span>
            </h4>
            <button
                  className="btn btn-outline-primary btn-sm ms-2"
                  onClick={handleEditClick}
                  title="Edit Family Details"
                >
                  <i className="bi bi-pencil-square"></i> Edit
                </button>
          </div>
            <div className="mb-2">
              <span className="fw-semibold me-2">Head Of Family:</span>
              <span className="text-secondary">{family.headOfFamilyName}</span>
            </div>
            <div className="mb-2">
              <span className="fw-semibold me-2">Gotra:</span>
              <span className="text-secondary">{family.gotra}</span>
            </div>
            <div className="mb-2">
              <span className="fw-semibold me-2">Email:</span>
              <span className="text-secondary">{family.email}</span>
            </div>
            <div className="mb-2">
              <span className="fw-semibold me-2">Phone:</span>
              <span className="text-secondary">
                {getFormattedPhoneDisplay(
                  family.phone,
                  family.phoneWhatsappRegistered,
                )}
              </span>
            </div>
            <div className="d-flex">
              <span className="fw-semibold me-2">Address:</span>
              <address className="mb-0">
                {family.familyAddress?.addressLine1}
                <br />
                {family.familyAddress?.addressLine2 && (
                  <>
                    {family.familyAddress.addressLine2}
                    <br />
                  </>
                )}
                {family.familyAddress?.addressLine3 && (
                  <>
                    {family.familyAddress.addressLine3}
                    <br />
                  </>
                )}
                {family.familyAddress?.district && (
                  <>
                    {" "}
                    District: {family.familyAddress.district}
                    <br />
                  </>
                )}
                {family.familyAddress?.city}, {family.familyAddress?.state} -{" "}
                {family.familyAddress?.postalCode.trim()}
                <br />
                {family.familyAddress?.country}
              </address>
            </div>
          </div>
          {/* Family Image (Right) */}
          {family.familyImage && (
            <div style={{ flex: "0 0 auto" }}>
              <img
                src={`/${family.familyImage}`}
                alt="Family"
                style={{
                  width: "450px",
                  height: "350px",
                  objectFit: "cover",
                  borderRadius: "8px",
                }}
              />
            </div>
          )}
          </div>
          )
        }
        </div>
      </div>

      {/* Tree View */}
      <div className="mb-4">
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
        <Tree
          lineWidth={"2px"}
          lineColor={"#ccc"}
          lineBorderRadius={"10px"}
          label={<CoupleNode member={family.familyTreeRoot} />}
        >
          {family.familyTreeRoot.children &&
            family.familyTreeRoot.children.map((child) => (
              <MemberNode key={child.memberId} member={child} />
            ))}
        </Tree>
      </div>
      </div>
      {/* Members List */}
      
      <div className="mb-4">
        <h5 className="mb-3">Family Members</h5>
        <div className="table-responsive">
          <table className="table table-bordered table-striped">
            <thead className="table-light">
              <tr>
                <th>Name</th>
                <th>Relationship</th>
                <th>Birth Date</th>
                <th>Marital Status</th>
                <th>Education</th>
                <th>Occupation</th>
                <th>Phone</th>
              </tr>
            </thead>
            <tbody>
              {membersList.map((member) => (
                <tr key={member.memberId}>
                  <td>
                    <Link
                      to={`/member/${member.memberId}`}
                      className="text-decoration-none"
                    >
                      {member.firstName} {member.lastName} <br />
                      {member.firstNameInHindi &&
                        `${member.firstNameInHindi} ${family.familyNameInHindi}`}
                    </Link>
                  </td>
                  <td>{member.relationship}</td>
                  <td>{member.birthDate}</td>
                  <td>{member.maritalStatus}</td>
                  <td>{member.educationDetails}</td>
                  <td>
                    {member.occupation}{" "}
                    {member.workingAt && `at ${member.workingAt}`}
                  </td>
                  <td>
                    {getFormattedPhoneDisplay(
                      member.phone,
                      member.phoneWhatsappRegistered,
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

    </div>
  );
};

export default FamilyDetails;
