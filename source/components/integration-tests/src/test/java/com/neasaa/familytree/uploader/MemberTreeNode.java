package com.neasaa.familytree.uploader;

import com.neasaa.familytree.operation.model.ExcelFamilyMemberDetails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MemberTreeNode {
    // Make this variable  immutable
    @Setter(AccessLevel.NONE)
    private ExcelFamilyMemberDetails member;
//    @Setter(AccessLevel.NONE)
//    private String firstName;
    private MemberTreeNode father; //This will be used only for head of family
    private MemberTreeNode mother; //This will be used only for head of family
    private MemberTreeNode spouse;
    private List<MemberTreeNode> children;
    @Setter(AccessLevel.NONE)
    private boolean memberAddedToTree;
    private boolean memberAddedToApplication;

    public MemberTreeNode (ExcelFamilyMemberDetails member) {
        this.member = member;
//        this.firstName = member.getFirstName();
    }

    public void addChild(MemberTreeNode child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

    public void setMemberAddedToTree() {
        this.memberAddedToTree = true;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("member=").append(member.getFirstName()).append(" ").append(member.getLastName());
//        if(father != null) {
//            sb.append(" (Father: ").append(father.member.getFirstName()).append(" ").append(father.member.getLastName()).append(")");
//        }
//        if(mother != null) {
//            sb.append(" (Mother: ").append(mother.member.getFirstName()).append(" ").append(mother.member.getLastName()).append(")");
//        }
        if(spouse != null) {
            sb.append(" (Spouse: ").append(spouse.member.getFirstName()).append(" ").append(spouse.member.getLastName()).append(")");
        }
        if(children != null && !children.isEmpty()) {
            for (MemberTreeNode child : children) {
                sb.append(" (Child: ").append(child.member.getFirstName()).append(" ").append(child.member.getLastName()).append(")");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
