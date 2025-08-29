import { Tree, TreeNode } from "react-organizational-chart";
import { Link } from "react-router-dom";

const getImagePath = (url) => {
  if (!url) return "/sample-male2.jpeg";
  return `/${url}`;
};

const MemberCard = ({ member }) => {
  let bgColor = "lightblue";
  let age = "(40 years)";
  if (member.dateOfDeath) {
    bgColor = "lightgrey";
    age = "(1916 - 2001)";
  } else if (member.gender === "Female") {
    bgColor = "pink";
  }
  return (
    <Link to={`/member/${member.memberId}`} className="text-decoration-none">
      <div
        className={`member-card d-flex align-items-center p-2 ${member.selectedNode ? "root-node" : ""}`}
        style={{ minWidth: "180px", borderRadius: "8px", backgroundColor: bgColor }}
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
          {age}
        </div>
      </div>
    </Link>
  );
};

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

const FamilyTree = ({ familyTreeRoot }) => {
  if (!familyTreeRoot) return null;
  return (
    <Tree
      lineWidth={"2px"}
      lineColor={"#ccc"}
      lineBorderRadius={"10px"}
      label={<CoupleNode member={familyTreeRoot} />}
    >
      {familyTreeRoot.children &&
        familyTreeRoot.children.map((child) => (
          <MemberNode key={child.memberId} member={child} />
        ))}
    </Tree>
  );
};

export default FamilyTree;
