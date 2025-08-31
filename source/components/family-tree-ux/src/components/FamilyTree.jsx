import { Tree, TreeNode } from "react-organizational-chart";
import { Link } from "react-router-dom";

const getImagePath = (url) => {
  if (!url) return "/sample-male2.jpeg";
  return `/${url}`;
};

const MemberCard = ({ member }) => {
  let bgColor = "lightblue";
  if (member.isAlive === false) {
    bgColor = "lightgrey";
  } else if (member.gender === "Female") {
    bgColor = "pink";
  }
  return (
    <Link to={`/member/${member.memberId}`} className="text-decoration-none">
      <div
        className={`member-card d-flex align-items-center p-2 ${member.selectedNode ? "root-node" : ""}`}
        style={{ minWidth: "180px", borderRadius: "8px", backgroundColor: bgColor }}
      >
        {member.profileImageThumbnail && (
          <img
            src={getImagePath(member.profileImageThumbnail)}
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
          {member.age}
        </div>
      </div>
    </Link>
  );
};

const CoupleNode = ({ node }) => (
  <div className="d-flex justify-content-center gap-2">
    <MemberCard member={node.member} />
    {node.spouse && <MemberCard member={node.spouse} />}
  </div>
);

const MemberNode = ({ node }) => (
  <TreeNode label={<CoupleNode node={node} />}>
    {node.children &&
      node.children.map((child) => (
        <MemberNode key={child.member.memberId} node={child} />
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
      label={<CoupleNode node={familyTreeRoot} />}
    >
      {familyTreeRoot.children &&
        familyTreeRoot.children.map((child) => (
          <MemberNode key={child.member.memberId} node={child} />
        ))}
    </Tree>
  );
};

export default FamilyTree;
