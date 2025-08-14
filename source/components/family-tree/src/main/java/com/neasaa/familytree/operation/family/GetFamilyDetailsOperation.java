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
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.FamilyMemberDto;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component("GetFamilyDetailsOperation")
@Scope("prototype")
public class GetFamilyDetailsOperation extends AbstractOperation<GetFamilyDetailsRequest, GetFamilyDetailsResponse> {
    public static final String HEAD_OF_FAMILY = "Head of Family";
    public static final String WIFE_OF_MEMBER = "Wife of %s";
    public static final String HUSBAND_OF_MEMBER = "Husband of %s";
    public static final String SON_OF_MEMBER = "Son of %s";
    public static final String DAUGHTER_OF_MEMBER = "Daughter of %s";
    public static final String FATHER_OF_MEMBER = "Father of %s";
    public static final String MOTHER_OF_MEMBER = "Mother of %s";

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
        familyDetailsResponse.setHeadOfFamilyName(headOfFamily.getFirstName() + " " + headOfFamily.getLastName());
        FamilyMemberDto familyTreeRoot = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(headOfFamily, HEAD_OF_FAMILY);
        familyTreeRoot.setSelectedNode(true);
        buildFamilyTreeStructure(familyTreeRoot);
        familyTreeRoot = addParentsAndSiblingsToFamilyTree(familyTreeRoot);

        familyDetailsResponse.setFamilyTreeRoot(familyTreeRoot);
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
                    String familyRelationship = null;
                    if(treeNode.getGender() == Gender.Male) {
                        familyRelationship = WIFE_OF_MEMBER.formatted(treeNode.getFirstName());
                    } else {
                        familyRelationship = HUSBAND_OF_MEMBER.formatted(treeNode.getFirstName());
                    }
                    treeNode.setSpouse(FamilyMemberDto.getFamilyMemberDtoFromDBEntity(spouse, familyRelationship));
                }
            }
        }

        List<MemberRelationship> childrenForMember = memberRelationshipDao.getChildrenForMemberById(treeNode.getMemberId(), spouseMemberId);
        if (childrenForMember != null) {
            for (MemberRelationship childRelation : childrenForMember) {
                FamilyMember child = familyMemberDao.getMemberById(childRelation.getRelatedMemberId());
                if (child != null) {
                    if(child.getFamilyId() != treeNode.getFamilyId()) {
                        log.warn("Child {} does not belong to the same family as parent {}", child.getFirstName(), treeNode.getFirstName());
                        continue; // Skip children that do not belong to the same family
                    }
                    String familyRelationship = null;
                    if(child.getGender() == Gender.Male) {
                        familyRelationship = SON_OF_MEMBER.formatted(treeNode.getFirstName());
                    } else {
                        familyRelationship = DAUGHTER_OF_MEMBER.formatted(treeNode.getFirstName());
                    }
                    FamilyMemberDto childDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(child, familyRelationship);
                    treeNode.addChild(childDto);
                    if(childDto.getFamilyId() == treeNode.getFamilyId()) {
                        buildFamilyTreeStructure(childDto);
                    }
                }
            }
        }
    }

    public FamilyMemberDto addParentsAndSiblingsToFamilyTree(FamilyMemberDto familyMemberDto) {
        log.info("Adding parents for member: {}", familyMemberDto.getFirstName());
        List<MemberRelationship> parents = memberRelationshipDao.getParentsForMemberById(familyMemberDto.getMemberId());
        if (parents == null || parents.isEmpty()) {
            log.info("No parents found for member: {}", familyMemberDto.getFirstName());
            return familyMemberDto;
        }
        FamilyMemberDto father = null;
        FamilyMemberDto mother = null;
        for (MemberRelationship parentRelationship : parents) {
            FamilyMember parentEntity = familyMemberDao.getMemberById(parentRelationship.getMemberId());
            if (parentEntity != null) {
                String familyRelationship = null;
                if (parentEntity.getGender() == Gender.Male) {
                    familyRelationship = FATHER_OF_MEMBER.formatted(familyMemberDto.getFirstName());
                } else {
                    familyRelationship = MOTHER_OF_MEMBER.formatted(familyMemberDto.getFirstName());
                }
                FamilyMemberDto parentDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(parentEntity, familyRelationship);
                if (parentEntity.getGender() == Gender.Male) {
                    father = parentDto;
                } else {
                    mother = parentDto;
                }
            }
        }
        FamilyMemberDto primaryParent = null;
        if(father != null) {
            log.info("Father found for member {}: {}", familyMemberDto.getFirstName(), father.getFirstName());
            primaryParent = father;
            primaryParent.setSpouse(mother);
        }  else if (mother != null) {
            log.info("Mother found for member {}: {}", familyMemberDto.getFirstName(), mother.getFirstName());
            primaryParent = mother;
            primaryParent.setSpouse(father);
        } else {
            log.info("No parents found for member: {}", familyMemberDto.getFirstName());
            return familyMemberDto;
        }

        primaryParent.addChild(familyMemberDto);
        return primaryParent;
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
