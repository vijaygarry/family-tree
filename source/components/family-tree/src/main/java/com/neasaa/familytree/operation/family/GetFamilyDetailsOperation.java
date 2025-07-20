package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.Family;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.entity.MemberRelationship;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.FamilyMemberDto;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("GetFamilyDetailsOperation")
@Scope("prototype")
public class GetFamilyDetailsOperation extends AbstractOperation<GetFamilyDetailsRequest, GetFamilyDetailsResponse> {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private FamilyDao familyDao;

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Autowired
    private MemberRelationshipDao memberRelationshipDao;

    @Override
    public String getOperationName() {
        return OperationNames.GET_FAMILY_DETAILS;
    }

    @Override
    public void doValidate(GetFamilyDetailsRequest opRequest) throws OperationException {
        if (opRequest != null && opRequest.getFamilyId() != null) {
            if(opRequest.getFamilyId() <  1 ){
                throw new ValidationException("Invalid family id provided.");
            }
        }
    }

    @Override
    public GetFamilyDetailsResponse doExecute(GetFamilyDetailsRequest opRequest) throws OperationException {
        int familyId = -1;
        if(opRequest == null || opRequest.getFamilyId() == null) {
            FamilyMember memberByLogonName = familyMemberDao.getMemberByLogonName(getContext().getAppSessionUser().getLogonName());
            familyId = memberByLogonName.getFamilyId();
        } else {
            familyId = opRequest.getFamilyId();
        }
        // Fetch family details using family id.
        Family familyDetailsFromDB = familyDao.getFamilyByFamilyId(familyId);
        // If family not found, throw ValidationException.
        if (familyDetailsFromDB == null) {
            throw new ValidationException("Family not found for the provided family id " + familyId);
        }
        GetFamilyDetailsResponse familyDetailsResponse = GetFamilyDetailsResponse.fromFamilyDBEntity(familyDetailsFromDB, null);

        // Fetch all the members of the family.
        List<FamilyMember> familyMembers = familyMemberDao.allMembersForFamily(familyId);
        FamilyMember headOfFamily = familyMemberDao.getHeadOfFamilyByFamilyId(familyId);
        // If head of family not found, throw ValidationException.
        if (headOfFamily == null) {
            throw new ValidationException("Family members not found for this family " + familyId);
        }

        FamilyMemberDto familyTreeRoot = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(headOfFamily);
        familyDetailsResponse.setFamilyTreeRoot(familyTreeRoot);
//        Map<Integer, FamilyMember> familyMemberMap =  familyMembers.stream().collect(Collectors.toMap(FamilyMember::getMemberId, member -> member));
        buildFamilyTreeStructure(familyTreeRoot);


        // Fetch the relationships of the family members and build the family tree structure.
        return familyDetailsResponse;
    }

    private void buildFamilyTreeStructure(FamilyMemberDto treeNode) {
        int spouseMemberId = -1;
        if(treeNode.getSpouse() == null) {
            MemberRelationship spouseForMember = memberRelationshipDao.getSpouseForMemberById(treeNode.getMemberId());
            if(spouseForMember != null) {
                spouseMemberId = spouseForMember.getRelatedMemberId();
                FamilyMember spouse = familyMemberDao.getMemberById(spouseForMember.getRelatedMemberId());
                if (spouse != null) {
                    treeNode.setSpouse(FamilyMemberDto.getFamilyMemberDtoFromDBEntity(spouse));
                }
            }
        }

        List<MemberRelationship> childrenForMember = memberRelationshipDao.getChildrenForMemberById(treeNode.getMemberId(), spouseMemberId);
        if (childrenForMember != null) {
            for (MemberRelationship childRelation : childrenForMember) {
                FamilyMember child = familyMemberDao.getMemberById(childRelation.getRelatedMemberId());
                if (child != null) {
                    FamilyMemberDto childDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(child);
                    treeNode.addChild(childDto);
                    if(childDto.getFamilyId() == treeNode.getFamilyId()) {
                        buildFamilyTreeStructure(childDto);
                    }
                }
            }
        }
    }

//    private FamilyMember getHeadOfFamily(List<FamilyMember> familyMembers) {
//        for (FamilyMember member : familyMembers) {
//            if (member.isHeadOfFamily()) {
//                return member;
//            }
//        }
//        return null;
//    }

//    private void buildFamilyTreeStructure(FamilyMemberDto treeNode, Map<Integer, FamilyMember> familyMemberMap) {
//        List<MemberRelationship> relationshipsForMember = memberRelationshipDao.getRelationshipsForMember(treeNode.getMemberId());
//        for (MemberRelationship relationship : relationshipsForMember) {
//            FamilyMember relatedMember = familyMemberMap.get(relationship.getRelatedMemberId());
//            if (relatedMember == null) {
//                continue;
//            }
//            FamilyMemberDto relatedMemberDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(relatedMember);
//            switch (relationship.getRelationshipType()) {
//                case Wife:
//                case Husband:
//                    treeNode.setSpouse(relatedMemberDto);
//                    break;
//                case Son:
//                case Daughter:
//                    treeNode.addChild(relatedMemberDto);
//                    break;
//                default:
//                    throw new RuntimeException("Unexpected relationship type: " + relationship.getRelationshipType());
//            }
//        }
//
//        if(treeNode.getChildren() != null && !treeNode.getChildren().isEmpty()) {
//            for (FamilyMemberDto child : treeNode.getChildren()) {
//                buildFamilyTreeStructure(child, familyMemberMap);
//            }
//        }
//    }
}
