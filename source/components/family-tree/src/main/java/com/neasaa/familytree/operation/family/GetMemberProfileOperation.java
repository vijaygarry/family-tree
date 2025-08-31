package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.FamilyTreeNode;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.MemberSummaryDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.BROTHER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.DAUGHTER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.FATHER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.HUSBAND_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.MOTHER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.SELF_RELATIONSHIP;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.SISTER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.SON_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.UNKNOWN_RELATIONSHIP;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.WIFE_OF_MEMBER;

@Log4j2
@Component("GetMemberProfileOperation")
@Scope("prototype")
public class GetMemberProfileOperation extends AbstractOperation <GetMemberProfileRequest, GetMemberProfileResponse> {

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private MemberRelationshipDao memberRelationshipDao;

    @Override
    public String getOperationName() {
        return OperationNames.GET_MEMBER_PROFILE;
    }

    @Override
    public void doValidate(GetMemberProfileRequest opRequest) throws OperationException {
        if (opRequest != null && opRequest.getMemberId() != null) {
            if(opRequest.getMemberId() <  1 ){
                throw new ValidationException("Invalid member id provided.");
            }
        }
    }

    @Override
    public GetMemberProfileResponse doExecute(GetMemberProfileRequest opRequest) throws OperationException {
        FamilyMemberEntity memberEntity = null;
        if (opRequest != null && opRequest.getMemberId() != null) {
            int memberId = opRequest.getMemberId();
            memberEntity = familyMemberDao.getMemberById(memberId);
        } else {
            String logonName = getContext().getAppSessionUser().getLogonName();
            memberEntity = familyMemberDao.getMemberByLogonName(logonName);
        }
        if (memberEntity == null) {
            throw new ValidationException("Member not found.");
        }

        MemberSummaryDto memberSummaryDto = MemberSummaryDto.getMemberSummaryDto(memberEntity, SELF_RELATIONSHIP);
        memberSummaryDto.setSelectedNode(true);
        FamilyTreeNode treeRootNode = new FamilyTreeNode (memberSummaryDto);

        buildFamilyTreeStructure(treeRootNode, 0);
        //Add parents and siblings to the family tree.
        treeRootNode = addParentsAndSiblingsToFamilyTree(treeRootNode, true);
        // Add grandparents to the family tree.
        treeRootNode = addParentsAndSiblingsToFamilyTree(treeRootNode, false);
        List<MemberSummaryDto> memberListToDisplay = new ArrayList<>();
        getMemberListToDisplay(treeRootNode, memberListToDisplay);
        GetMemberProfileResponse.MemberProfile memberProfile = GetMemberProfileResponse.MemberProfile.fromFamilyMemberDBEntity(memberEntity, getAddress(memberEntity));

        return GetMemberProfileResponse.builder()
                .memberProfile(memberProfile)
                .familyRoot(treeRootNode)
                .memberList(memberListToDisplay)
                .build();
    }

    private void buildFamilyTreeStructure(FamilyTreeNode treeNode, int numberOfLevels) {
        if(treeNode == null) {
            return;
        }

        MemberSummaryDto currentMember = treeNode.getMember();
        log.info("Adding details for {} with number of levels in family tree: {}", currentMember.getFirstName(), numberOfLevels);
        if(numberOfLevels > 2) {
            log.info("Not adding family details for member {} as the family tree depth limit is reached.", currentMember.getFirstName());
            return; // Limit the depth of the family tree to 2 levels
        }

        int spouseMemberId = -1;
        //Set the spouse only for selected member. Should not set spouse for children. i.e. if numberOfLevels is 0.
        if(numberOfLevels ==0 && treeNode.getSpouse() == null) {
            MemberRelationshipEntity spouseForMember = memberRelationshipDao.getSpouseForMemberById(currentMember.getMemberId());
            if(spouseForMember != null) {
                MemberSummaryDto spouse = getMemberSummaryDtoFromDB(spouseForMember.getRelatedMemberId());
                if(spouse != null) {
                    if(currentMember.getGender() == Gender.Male) {
                        spouse.setFamilyRelationship(WIFE_OF_MEMBER.formatted(currentMember.getFirstName()));
                    } else {
                        spouse.setFamilyRelationship(HUSBAND_OF_MEMBER.formatted(currentMember.getFirstName()));
                    }
                    spouseMemberId = spouse.getMemberId();
                    treeNode.setSpouse(spouse);
                } else {
                    log.warn("Spouse member with id {} not found in DB.", spouseForMember.getRelatedMemberId());
                }
            }
        }

        ++numberOfLevels;
        List<MemberRelationshipEntity> childrenForMember = memberRelationshipDao.getChildrenForMemberById(currentMember.getMemberId(), spouseMemberId);
        if (childrenForMember != null) {
            for (MemberRelationshipEntity childRelation : childrenForMember) {
                MemberSummaryDto child = getMemberSummaryDtoFromDB(childRelation.getRelatedMemberId());
                if (child != null) {
                    if (child.getGender() == Gender.Male) {
                        child.setFamilyRelationship(SON_OF_MEMBER.formatted(currentMember.getFirstName()));
                    } else {
                        child.setFamilyRelationship(DAUGHTER_OF_MEMBER.formatted(currentMember.getFirstName()));
                    }
                    FamilyTreeNode childNode = new FamilyTreeNode(child);
                    treeNode.addChild(childNode);
                    if(child.getFamilyId() == currentMember.getFamilyId()) {
                        log.info("Member " + child.getFirstName() + " is part of the same family as " + currentMember.getFamilyId());
                        buildFamilyTreeStructure(childNode, numberOfLevels);
                    } else {
                        log.info("Member {} with family id {} is not part of the same family as {}", child.getFirstName(), child.getFamilyId(), currentMember.getFamilyId());
                    }
                }
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

    public FamilyTreeNode addParentsAndSiblingsToFamilyTree(FamilyTreeNode treeNode, boolean includeSiblings) {
        if(treeNode == null || treeNode.getMember() == null) {
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
            MemberSummaryDto parent = getMemberSummaryDtoFromDB(parentRelationship.getMemberId());
            if (parent != null) {
                if (parent.getGender() == Gender.Male) {
                    father = parent;
                    father.setFamilyRelationship(FATHER_OF_MEMBER.formatted(currentMember.getFirstName()));
                } else {
                    mother = parent;
                    mother.setFamilyRelationship(MOTHER_OF_MEMBER.formatted(currentMember.getFirstName()));
                }
            }
        }
        FamilyTreeNode primaryParent = null;
        if(father != null) {
            log.info("Father found for member {}: {}", currentMember.getFirstName(), father.getFirstName());
            primaryParent = new FamilyTreeNode(father);
            primaryParent.setSpouse(mother);
        }  else if (mother != null) {
            log.info("Mother found for member {}: {}", currentMember.getFirstName(), mother.getFirstName());
            primaryParent = new FamilyTreeNode(mother);
            primaryParent.setSpouse(father);
        } else {
            log.info("No parents found for member: {}", currentMember.getFirstName());
            return treeNode;
        }
        primaryParent.addChild(treeNode);

        if(!includeSiblings) {
            log.info("Skipping siblings for member: {}", currentMember.getFirstName());
            return primaryParent;
        }

        List<MemberSummaryDto> childrenForMember = getChildrenForMember(father == null ? -1 : father.getMemberId(), mother == null ? -1 : mother.getMemberId());

        if (childrenForMember != null) {
            for (MemberSummaryDto child : childrenForMember) {
                // Current member node is already added, so do not add current member node again.
                if (child.getMemberId() != currentMember.getMemberId()) {
                    log.info("Adding sibling: {} for member: {}", child.getFirstName(), currentMember.getFirstName());
                    if(child.getGender() == Gender.Male) {
                        child.setFamilyRelationship(BROTHER_OF_MEMBER.formatted(currentMember.getFirstName()));
                    } else {
                        child.setFamilyRelationship(SISTER_OF_MEMBER.formatted(currentMember.getFirstName()));
                    }
                    primaryParent.addChild(new FamilyTreeNode(child));
                }
            }
        }
        return primaryParent;
    }

    private List<MemberSummaryDto> getChildrenForMember(int memberId, int spouseMemberId) {
        List<MemberSummaryDto> children = new ArrayList<>();
        List<MemberRelationshipEntity> childrenForMember = memberRelationshipDao.getChildrenForMemberById(memberId, spouseMemberId);
        if (childrenForMember != null) {
            for (MemberRelationshipEntity childRelation : childrenForMember) {
                MemberSummaryDto child = getMemberSummaryDtoFromDB(childRelation.getRelatedMemberId());
                if (child != null) {
                    children.add(child);
                }
            }
        } else {
            log.info("No children found for member with id: {}", memberId);
            return null;
        }
        return children;
    }

    private void getMemberListToDisplay(FamilyTreeNode treeRootNode, List<MemberSummaryDto> memberListToDisplay) {
        if (treeRootNode == null) {
            return;
        }
        memberListToDisplay.add(treeRootNode.getMember());
        if (treeRootNode.getSpouse() != null) {
            memberListToDisplay.add(treeRootNode.getSpouse());
        }
        if (treeRootNode.getChildren() != null && !treeRootNode.getChildren().isEmpty()) {
            for (FamilyTreeNode childNode : treeRootNode.getChildren()) {
                getMemberListToDisplay(childNode, memberListToDisplay);
            }
        }

    }

    private AddressDto getAddress(FamilyMemberEntity member){
        AddressEntity address = null;
        if (member.isAddressSameAsFamily()) {
            address = addressDao.getAddressByFamilyId(member.getFamilyId());
        } else {
            if (member.getMemberAddressId() > 0) {
                address = addressDao.getAddressById(member.getMemberAddressId());
            }
        }
        return AddressDto.getAddressDtoFromEntity(address);
    }


}
