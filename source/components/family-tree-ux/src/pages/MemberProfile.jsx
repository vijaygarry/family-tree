import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Tree, TreeNode } from "react-organizational-chart";
import api from "../api/axiosInstance";
import { getFormattedPhoneDisplay } from '../utils/phoneUtils';
import './TreeNode.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Link } from "react-router-dom";
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
    <MemberCard member={member} />
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

const MemberProfile = () => {
  const { id } = useParams(); // from route: /member/:id
  const [memberData, setMemberData] = useState(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();

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

  if (error) return <div className="text-danger p-4">{error}</div>;
  if (!memberData) return <div className="p-4">Loading member profile...</div>;
  const { memberProfile } = memberData ;
  const membersList = flattenFamilyTree(memberData.familyTreeRoot);

  return (
    <div className="container py-4">
      <div className="card mb-4">
        <div className="card-header bg-info text-white">
          <h4 className="mb-0">Member Profile</h4>
        </div>
        <div className="card-body">
          <div className="d-flex align-items-center justify-content-between mb-3">
            {/* <img
              src={`/${member.profileImageThumbnail}`}
              alt={member.firstName}
              className="rounded-circle me-3"
              style={{ width: "80px", height: "80px" }}
            /> */}
              <div>
                <h5 className="mb-0">{memberProfile.firstName} {memberProfile.lastName}</h5>
              </div>
              {memberProfile.familyId && (
                <div>
                  <Link
                    to={`/family/${memberProfile.familyId}`}
                    className="btn btn-sm btn-primary"
                  >
                    <img src="/family.png" alt="Family" style={{ width: '40px', height: '40px' }} /> View {memberProfile.firstName}'s Family
                  </Link>
                </div>
              )}
            </div>
              <p className="mb-1 text-muted">{memberProfile.occupation} at {memberProfile.workingAt}</p>
              <p className="mb-1">{getFormattedPhoneDisplay(memberProfile.phone, memberProfile.phoneWhatsappRegistered)}</p>
              <p className="mb-1">✉️ {memberProfile.email}</p>
              <p className="mb-0">🎂 {memberProfile.birthDay}/{memberProfile.birthMonth}/{memberProfile.birthYear}</p>
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
          label={<CoupleNode member={memberData.familyTreeRoot} />}
        >
          {memberData.familyTreeRoot.children?.map(child => (
            <MemberNode key={child.memberId} member={child} />
          ))}
        </Tree>
      </div>
    </div>
  );
};

export default MemberProfile;
