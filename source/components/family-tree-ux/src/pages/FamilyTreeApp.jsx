import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Link } from "react-router-dom";
import { Tree, TreeNode } from "react-organizational-chart";
import { getFormattedPhoneDisplay } from '../utils/phoneUtils';
import './TreeNode.css';
import 'bootstrap/dist/css/bootstrap.min.css';
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
            style={{ width: "60px",
            height: "60px",
            borderRadius: "10%",
            objectFit: "cover",
            marginRight: "10px"
            }}
          />
        )}
    <div className="card-body p-2">
      <h6 className="card-title mb-1">{member.firstName} {member.lastName}</h6>
      {/* <p className="card-text small text-muted">{member.occupation}</p>
      <p className="card-text small">📞 {member.phone}</p> */}
    </div>
  </div>
  </Link>
);


const CoupleNode = ({ member }) => (
  <div className="d-flex justify-content-center gap-2">
    <MemberCard member={member}/>
    {member.spouse && <MemberCard member={member.spouse} />}
  </div>
);


const MemberNode = ({ member }) => (
  <TreeNode label={<CoupleNode member={member} />}>
    {member.children && member.children.map((child) => (
      <MemberNode key={child.memberId} member={child} />
    ))}
  </TreeNode>
);

function flattenFamilyTree(root) {
  const members = [];

  function traverse(member) {
    const memberName = `${member.firstName} ${member.lastName}`;
    members.push({
      memberId: member.memberId,
      firstName: member.firstName,
      firstNameInHindi: member.firstNameInHindi,
      lastName: member.lastName,
      phone: member.phone || '',
      email: member.email || '',
      occupation: member.occupation || '',
      relationship: member.familyRelationship,
      phoneWhatsappRegistered: member.phoneWhatsappRegistered,
      maritalStatus: member.maritalStatus,
      birthDate: member.birthDate,
      educationDetails: member.educationDetails,
      occupation: member.occupation,
      workingAt: member.workingAt,
    });

    if (member.spouse) {
      const spouseName = `${member.spouse.firstName} ${member.spouse.lastName}`;
      members.push({
        memberId: member.spouse.memberId,
        firstName: member.spouse.firstName,
        firstNameInHindi: member.spouse.firstNameInHindi,
        lastName: member.spouse.lastName,
        phone: member.spouse.phone || '',
        email: member.spouse.email || '',
        occupation: member.spouse.occupation || '',
        relationship: member.spouse.familyRelationship,
        phoneWhatsappRegistered: member.spouse.phoneWhatsappRegistered,
        maritalStatus: member.spouse.maritalStatus,
        birthDate: member.spouse.birthDate,
        educationDetails: member.spouse.educationDetails,
        occupation: member.spouse.occupation,
        workingAt: member.spouse.workingAt,
      });
    }

    if (member.children) {
      member.children.forEach(child => {
        traverse(child);
      });
    }
  }

  traverse(root);
  return members;
}

const FamilyTreeApp = () => {
  const [family, setFamily] = useState(null);
  const [error, setError] = useState(null);
  const { familyId } = useParams();  // ← Get it from URL
  useEffect(() => {
    const fetchFamilyData = async () => {
      try {
        const requestBody = familyId ? { familyId: parseInt(familyId) } : {};
        const response = await api.post("/family/getfamilydetails", requestBody);
        setFamily(response.data);
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

  if (error) return <div style={{ color: "red" }}>{error}</div>;
  if (!family) return <div>Loading family tree...</div>;
  const membersList = flattenFamilyTree(family.familyTreeRoot);

  return (
    <div className="container py-4">
      {/* Family Info Section */}
      <div className="card mb-4" style={{ backgroundColor: '#e7f3ff' }}>
        <div className="card-body d-flex flex-wrap align-items-start" style={{ gap: '1rem' }}>
          <div style={{ flex: '1 1 10%' }}>
            <h2 className="card-title mb-3">{family.familyName} 
              {family.familyNameInHindi && ` (${family.familyNameInHindi})`}
            </h2>         
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
              <span className="text-secondary">{getFormattedPhoneDisplay(family.phone, family.phoneWhatsappRegistered)}
              </span>
            </div>
            <div className="d-flex">
              <span className="fw-semibold me-2">Address:</span>
              <address className="mb-0">
                {family.familyAddress?.addressLine1}<br />
                {family.familyAddress?.addressLine2 && (<>{family.familyAddress.addressLine2}<br /></>)}
                {family.familyAddress?.addressLine3 && (<>{family.familyAddress.addressLine3}<br /></>)}
                {family.familyAddress?.district && (<> District: {family.familyAddress.district}<br /></>)}
                {family.familyAddress?.city}, {family.familyAddress?.state} - {family.familyAddress?.postalCode.trim()}<br />
                {family.familyAddress?.country}
              </address>
            </div>
        </div>
        {/* Family Image (Right) */}
        {family.familyImage && (
          <div style={{ flex: '0 0 auto' }}>
            <img
              src={`/${family.familyImage}`}
              alt="Family"
              style={{ width: '450px', height: '350px', objectFit: 'cover', borderRadius: '8px' }}
            />
          </div>
        )}
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
                <th>Birth Date</th>
                <th>Marital Status</th>
                <th>Education</th>
                <th>Occupation</th>
                <th>Phone</th>
              </tr>
            </thead>
            <tbody>
              {membersList.map(member => (
                <tr key={member.memberId}>
                  <td>
                    <Link to={`/member/${member.memberId}`} className="text-decoration-none">
                      {member.firstName} {member.lastName} <br />
                      {member.firstNameInHindi && `${member.firstNameInHindi} ${family.familyNameInHindi}`}
                    </Link>
                  </td>
                  <td>{member.relationship}</td>
                  <td>{member.birthDate}</td>
                  <td>{member.maritalStatus}</td>
                  <td>{member.educationDetails}</td>
                  <td>{member.occupation} {member.workingAt && `at ${member.workingAt}`}</td>
                  <td>{getFormattedPhoneDisplay(member.phone, member.phoneWhatsappRegistered)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Tree View */}
      <h5 className="mb-3">Family Tree</h5>
      <div 
        className="tree-container border" 
        style={{ 
          width: '100%', 
          minHeight: '600px', 
          overflow: 'auto', 
          padding: '10px'
        }}
      >
        <Tree
          lineWidth={"2px"}
          lineColor={"#ccc"}
          lineBorderRadius={"10px"}
          label={<CoupleNode member={family.familyTreeRoot}/>}
        >
          {family.familyTreeRoot.children && family.familyTreeRoot.children.map((child) => (
            <MemberNode key={child.memberId} member={child} />
          ))}
        </Tree>
      </div>
    </div>
  );
};

export default FamilyTreeApp;
