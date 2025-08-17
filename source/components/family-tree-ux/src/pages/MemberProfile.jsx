import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Tree, TreeNode } from "react-organizational-chart";
import api from "../api/axiosInstance";
import { getFormattedPhoneDisplay } from "../utils/phoneUtils";
import "./TreeNode.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { Link } from "react-router-dom";
import ERROR_MESSAGES from "../constants/messages";

const MemberCard = ({ member }) => (
  <Link to={`/member/${member.memberId}`} className="text-decoration-none">
    <div
      className={`member-card text-center p-2 ${member.selectedNode ? "root-node" : ""}`}
      style={{ minWidth: "160px" }}
    >
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

  function traverse(member, relationship) {
    const memberName = `${member.firstName} ${member.lastName}`;
    members.push({
      memberId: member.memberId,
      firstName: member.firstName,
      lastName: member.lastName,
      phone: member.phone || "",
      email: member.email || "",
      occupation: member.occupation || "",
      relationship: relationship === "Head" ? "Head of Family" : relationship,
      phoneWhatsappRegistered: member.phoneWhatsappRegistered,
    });

    if (member.spouse) {
      const spouseName = `${member.spouse.firstName} ${member.spouse.lastName}`;
      let spouseRel = "";
      if (member.gender === "Male") {
        spouseRel = `Wife of ${memberName}`;
      } else if (member.gender === "Female") {
        spouseRel = `Husband of ${memberName}`;
      } else {
        spouseRel = `Spouse of ${memberName}`;
      }
      members.push({
        memberId: member.spouse.memberId,
        firstName: member.spouse.firstName,
        lastName: member.spouse.lastName,
        phone: member.spouse.phone || "",
        email: member.spouse.email || "",
        occupation: member.spouse.occupation || "",
        relationship: spouseRel,
        phoneWhatsappRegistered: member.spouse.phoneWhatsappRegistered,
      });
    }

    if (member.children) {
      member.children.forEach((child) => {
        let childRel = "Child of " + memberName;
        if (child.gender === "Male") childRel = `Son of ${memberName}`;
        else if (child.gender === "Female")
          childRel = `Daughter of ${memberName}`;

        traverse(child, childRel);
      });
    }
  }

  traverse(root, "Head");
  return members;
}

const MemberProfile = () => {
  const { id } = useParams(); // from route: /member/:id
  const [memberData, setMemberData] = useState(null);
  const [error, setError] = useState("");
  const [editMode, setEditMode] = useState(false);
  const [form, setForm] = useState({});
  
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
  
  if (error) return <div className="text-danger p-4">{error}</div>;
  if (!memberData) return <div className="p-4">Loading member profile...</div>;
  const { memberProfile } = memberData;
  const membersList = flattenFamilyTree(memberData.familyTreeRoot);


  const memberInformationEditForm = (
    <form >
      <div className="mb-2">
        <span className="fw-semibold me-2">Name:</span>
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
                  <span className="text-secondary"> {memberProfile.occupation}
                    {memberProfile.workingAt && ` at ${memberProfile.workingAt}`}
                  </span>
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

      <div className="mb-4">
        <h4>Family Members</h4>
        <div className="table-responsive">
          <table className="table table-bordered table-striped">
            <thead className="table-light">
              <tr>
                <th>Name</th>
                <th>Relationship</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Occupation</th>
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
                      {member.firstName} {member.lastName}
                    </Link>
                  </td>
                  <td>{member.relationship}</td>
                  <td>
                    {getFormattedPhoneDisplay(
                      member.phone,
                      member.phoneWhatsappRegistered,
                    )}
                  </td>
                  <td>{member.email}</td>
                  <td>{member.occupation}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Tree View */}
      <h5 className="mb-3">Family Tree</h5>
      <div className="overflow-auto">
        <Tree
          lineWidth={"2px"}
          lineColor={"#ccc"}
          lineBorderRadius={"10px"}
          label={<CoupleNode member={memberData.familyTreeRoot} />}
        >
          {memberData.familyTreeRoot.children?.map((child) => (
            <MemberNode key={child.memberId} member={child} />
          ))}
        </Tree>
      </div>
    </div>
  );
};

export default MemberProfile;
