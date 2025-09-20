package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.FamilyDetailsDto;
import com.neasaa.familytree.operation.family.model.FamilyTreeNode;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsResponse;
import com.neasaa.familytree.operation.family.model.MemberSummaryDto;
import com.neasaa.familytree.utils.SessionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_DETAILS;
import static java.util.stream.Collectors.toMap;

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
    public static final String UNKNOWN_RELATIONSHIP = "Relationship unknown";
    public static final String SELF_RELATIONSHIP = "Self";
    public static final String BROTHER_OF_MEMBER = "Brother of %s";
    public static final String SISTER_OF_MEMBER = "Sister of %s";

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
            // If family id not provided in request, fetch family id from session user.
            FamilyMemberEntity familyMemberFromContext = SessionUtils.getFamilyMemberFromContext(getContext());
            if(familyMemberFromContext == null) {
                throw new ValidationException("Member is not linked to any family.");
            }
            familyId = familyMemberFromContext.getFamilyId();
        } else {
            familyId = opRequest.getFamilyId();
        }

        // Fetch family details using family id.
        FamilyEntity familyDetailsFromDB = familyDao.getFamilyByFamilyId(familyId);
        // If family not found, throw ValidationException.
        if (familyDetailsFromDB == null) {
            throw new ValidationException("Family not found for the provided family id " + familyId);
        }

        // Fetch all the members of the family.
        List<FamilyMemberEntity> familyMemberEntities = familyMemberDao.allMembersForFamily(familyId);
        //Create a list of MemberSummaryDto from familyMemberEntities
        List<MemberSummaryDto> memberSummaryDtoList = familyMemberEntities.stream()
                .map(member -> MemberSummaryDto.getMemberSummaryDto(member, UNKNOWN_RELATIONSHIP))
                .toList();

        // Find the head of the family from the memberSummaryDto List
        MemberSummaryDto headOfFamily = getHeadOfFamily(memberSummaryDtoList);

        FamilyDetailsDto familyDetails = null;
        boolean isFamilyUpdateAllowed = isFamilyUpdateAllowedForUser(familyId);
        if(headOfFamily == null) {
            log.error("Head of family not found in family members list.");
            familyDetails = FamilyDetailsDto.fromFamilyDBEntity(familyDetailsFromDB, "Head of Family not defined", isFamilyUpdateAllowed);
        } else {
            log.info("Head of family found: {}", headOfFamily.getFirstName());
            familyDetails = FamilyDetailsDto.fromFamilyDBEntity(familyDetailsFromDB, headOfFamily.getFirstName() + " " + headOfFamily.getLastName(), isFamilyUpdateAllowed);
        }

        // Create a map of memberId to MemberSummaryDto for easy lookup
        Map<Integer, MemberSummaryDto> familyMemberMap = memberSummaryDtoList.stream()
                .collect(toMap(MemberSummaryDto::getMemberId, dto -> dto));

        // Build family tree starting from head of family.
        FamilyTreeNode familyTreeRootNode = buildFamilyTree(headOfFamily, familyMemberMap);

        //Add parents and siblings to the family tree root node.
        familyTreeRootNode = addParentsAndSiblingsToFamilyTree(familyTreeRootNode, familyMemberMap);

        List<MemberSummaryDto> memberListToDisplay = new ArrayList<>();

        // Get the member list from tree and add other members which does not have any relationship defined with head of family.
        getMemberListToDisplay(familyTreeRootNode, memberSummaryDtoList, memberListToDisplay);

        return GetFamilyDetailsResponse.builder()
                .familyDetails(familyDetails)
                .familyRoot(familyTreeRootNode)
                .memberList(memberListToDisplay)
                .build();
    }

    private boolean isFamilyUpdateAllowedForUser(int familyId) {
        if(getContext() == null || getContext().getAppSessionUser() == null) {
            log.info("Operation context or AppSessionUser is null, cannot check if family edit allowed for user");
            return false;
        }
        FamilyMemberEntity memberEntity = SessionUtils.getFamilyMemberFromSession( getContext().getAppSessionUser());
        if(memberEntity == null) {
            log.info("Family member not found in session, cannot check if family edit allowed for user");
            return false;
        }
        if(memberEntity.getFamilyId() != familyId) {
            // Only allowed to edit own family details.
            return false;
        }
        return isOperationAllowedForUser(UPDATE_MY_FAMILY_DETAILS);
    }

    private FamilyTreeNode buildFamilyTree(MemberSummaryDto headOfFamily, Map<Integer, MemberSummaryDto> familyMemberMap) {
        if(headOfFamily == null) {
            log.error("Head of family is null, cannot build family tree.");
            return null;
        }

        // Build tree structure starting from head of family.
        headOfFamily.setFamilyRelationship(HEAD_OF_FAMILY);
        headOfFamily.setSelectedNode(true);

        FamilyTreeNode rootNode = new FamilyTreeNode(headOfFamily);
        addSpouseAndChildren (rootNode, familyMemberMap);
        return rootNode;
    }

    /**
     * Recursively add spouse and children to the tree node.
     *
     * @param treeNode
     * @param familyMemberMap
     */
    private void addSpouseAndChildren(FamilyTreeNode treeNode, Map<Integer, MemberSummaryDto> familyMemberMap) {

        if(treeNode == null || treeNode.getMember().getMaritalStatus() == MaritalStatus.Single) {
            //If member is single, no spouse or children to add.
            return;
        }

        MemberSummaryDto currentMember = treeNode.getMember();
        int spouseMemberId = -1;
        //If spouse is already set, no need to add again.
        if(treeNode.getSpouse() == null) {
            MemberRelationshipEntity spouseForMember = memberRelationshipDao.getSpouseForMemberById(currentMember.getMemberId());
            if(spouseForMember != null) {
                spouseMemberId = spouseForMember.getRelatedMemberId();
                MemberSummaryDto spouse = familyMemberMap.get(spouseMemberId);
                if(spouse == null) {
                    log.info("Spouse not found in family member map, fetching from DB for member id: {}", spouseMemberId);
                    spouse = getMemberSummaryDtoFromDB(spouseMemberId);
                }
                if (spouse != null) {
                    String familyRelationship = null;
                    if(currentMember.getGender() == Gender.Male) {
                        familyRelationship = WIFE_OF_MEMBER.formatted(currentMember.getFirstName());
                    } else {
                        familyRelationship = HUSBAND_OF_MEMBER.formatted(currentMember.getFirstName());
                    }
                    spouse.setFamilyRelationship(familyRelationship);
                    treeNode.setSpouse(spouse);
                }
            }
        }

        List<MemberRelationshipEntity> childrenForMember = memberRelationshipDao.getChildrenForMemberById(currentMember.getMemberId(), spouseMemberId);
        if (childrenForMember != null) {
            for (MemberRelationshipEntity childRelation : childrenForMember) {
                MemberSummaryDto child = familyMemberMap.get(childRelation.getRelatedMemberId());
                if(child == null) {
                    // Do not fetch child info from DB. Child not found in family member, because child belongs to different family.
                    log.info("Child {} not found in family member map, skipping assuming child is part of other family.", childRelation.getRelatedMemberId());
                    continue;
                }

                String familyRelationship = null;
                if (child.getGender() == Gender.Male) {
                    familyRelationship = SON_OF_MEMBER.formatted(currentMember.getFirstName());
                } else {
                    familyRelationship = DAUGHTER_OF_MEMBER.formatted(currentMember.getFirstName());
                }
                child.setFamilyRelationship(familyRelationship);
                FamilyTreeNode childNode = new FamilyTreeNode(child);
                treeNode.addChild(childNode);
                addSpouseAndChildren(childNode, familyMemberMap);
            }
        }

    }

    private void getMemberListToDisplay(FamilyTreeNode familyTreeRootNode, List<MemberSummaryDto> allFamilyMemberList, List<MemberSummaryDto> memberListToDisplay) {
        if (familyTreeRootNode == null) {
            return;
        }
        memberListToDisplay.add(familyTreeRootNode.getMember());
        if (familyTreeRootNode.getSpouse() != null) {
            memberListToDisplay.add(familyTreeRootNode.getSpouse());
        }
        if (familyTreeRootNode.getChildren() != null && !familyTreeRootNode.getChildren().isEmpty()) {
            for (FamilyTreeNode childNode : familyTreeRootNode.getChildren()) {
                getMemberListToDisplay(childNode, allFamilyMemberList, memberListToDisplay);
            }
        }

    }


    private MemberSummaryDto getMemberSummaryDtoFromDB (int memberId) {
        FamilyMemberEntity memberFromDb = familyMemberDao.getMemberById(memberId);
        if(memberFromDb == null) {
            return null;
        }
        return MemberSummaryDto.getMemberSummaryDto(memberFromDb, UNKNOWN_RELATIONSHIP);
    }

    public FamilyTreeNode addParentsAndSiblingsToFamilyTree(FamilyTreeNode treeNode, Map<Integer, MemberSummaryDto> familyMemberMap) {
        if(treeNode == null || treeNode.getMember() == null) {
            log.error("Tree node or member is null, cannot add parents.");
            return treeNode;
        }

        MemberSummaryDto currentMember = treeNode.getMember();
        log.info("Adding parents for member: {}", currentMember.getFirstName());
        List<MemberRelationshipEntity> parents = memberRelationshipDao.getParentsForMemberById(currentMember.getMemberId());
        if (parents == null || parents.isEmpty()) {
            log.info("No parents found for member: {}", currentMember.getFirstName());
            return treeNode;
        }
        MemberSummaryDto father = null;
        MemberSummaryDto mother = null;
        for (MemberRelationshipEntity parentRelationship : parents) {
            MemberSummaryDto parent = familyMemberMap.get(parentRelationship.getMemberId());
            if (parent != null) {
                String familyRelationship = null;
                if (parent.getGender() == Gender.Male) {
                    father = parent;
                    familyRelationship = FATHER_OF_MEMBER.formatted(currentMember.getFirstName());
                    father.setFamilyRelationship(familyRelationship);
                } else {
                    mother = parent;
                    familyRelationship = MOTHER_OF_MEMBER.formatted(currentMember.getFirstName());
                    mother.setFamilyRelationship(familyRelationship);
                }
            }
        }

        FamilyTreeNode primaryParentNode = null;
        if(father != null) {
            log.info("Father found for member {}: {}", currentMember.getFirstName(), father.getFirstName());
            primaryParentNode = new FamilyTreeNode(father);
            primaryParentNode.setSpouse(mother);
        }  else if (mother != null) {
            log.info("Mother found for member {}: {}", currentMember.getFirstName(), mother.getFirstName());
            primaryParentNode = new FamilyTreeNode(mother);
            primaryParentNode.setSpouse(father);
        } else {
            log.info("No parents found for member: {}", currentMember.getFirstName());
            return treeNode;
        }

        primaryParentNode.addChild(treeNode);
        return primaryParentNode;
    }

    private MemberSummaryDto getHeadOfFamily(List<MemberSummaryDto> familyMembers) {
        for (MemberSummaryDto member : familyMembers) {
            if (member.isHeadOfFamily()) {
                return member;
            }
        }
        return null;
    }

}
