import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Link } from "react-router-dom";
import { Tree, TreeNode } from "react-organizational-chart";
import { FaWhatsapp } from 'react-icons/fa';
import { getFormattedPhoneDisplay } from '../utils/phoneUtils';
import './TreeNode.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import api from "../api/axiosInstance";
import ERROR_MESSAGES from "../constants/messages";

const MemberCard = ({ member }) => (
  <Link to={`/member/${member.memberId}`} className="text-decoration-none">
  <div 
    className={`member-card text-center p-2 ${member.selectedNode ? "root-node" : ""}`}
    style={{ minWidth: "160px" }}>
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

  function traverse(member, relationship) {
    const memberName = `${member.firstName} ${member.lastName}`;
    members.push({
      memberId: member.memberId,
      firstName: member.firstName,
      lastName: member.lastName,
      phone: member.phone || '',
      email: member.email || '',
      occupation: member.occupation || '',
      relationship: relationship === 'Head' ? 'Head of Family' : relationship,
      phoneWhatsappRegistered: member.phoneWhatsappRegistered,
    });

    if (member.spouse) {
      const spouseName = `${member.spouse.firstName} ${member.spouse.lastName}`;
      let spouseRel = '';
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
        phone: member.spouse.phone || '',
        email: member.spouse.email || '',
        occupation: member.spouse.occupation || '',
        relationship: spouseRel,
        phoneWhatsappRegistered: member.spouse.phoneWhatsappRegistered,
      });
    }

    if (member.children) {
      member.children.forEach(child => {
        let childRel = 'Child of ' + memberName;
        if (child.gender === 'Male') childRel = `Son of ${memberName}`;
        else if (child.gender === 'Female') childRel = `Daughter of ${memberName}`;

        traverse(child, childRel);
      });
    }
  }

  traverse(root, 'Head');
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
        <div className="card-body">
          <div>
            <h2 className="card-title mb-3">{family.familyName} 
              {family.familyNameInHindi && ` (${family.familyNameInHindi})`}
            </h2>
          </div>
          <div className="mb-2">
            <span className="fw-semibold me-2">Head Of Family:</span>
            <span className="text-secondary">{family.gotra}</span>
          </div>
          <div className="mb-2">
            <span className="fw-semibold me-2">Gotra:</span>
            <span className="text-secondary">{family.headOfFamilyName}</span>
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
              {membersList.map(member => (
                <tr key={member.memberId}>
                  <td>
                    <Link to={`/member/${member.memberId}`} className="text-decoration-none">
                      {member.firstName} {member.lastName}
                    </Link>
                  </td>
                  <td>{member.relationship}</td>
                  <td>{getFormattedPhoneDisplay(member.phone, member.phoneWhatsappRegistered)}</td>
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
