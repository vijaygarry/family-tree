package com.neasaa.familytree.operation.family;

import static java.util.stream.Collectors.toMap;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("GetFamilyDetailsOperation")
@Scope("prototype")
public class GetFamilyDetailsOperation
    extends FamilyAbstractOperation<GetFamilyDetailsRequest, GetFamilyDetailsResponse> {

  @Override
  public String getOperationName() {
    return OperationNames.GET_FAMILY_DETAILS;
  }

  @Override
  public void doValidate(GetFamilyDetailsRequest opRequest) throws OperationException {
    if (opRequest != null && opRequest.getFamilyId() != null) {
      if (opRequest.getFamilyId() < 1) {
        throw new ValidationException("Invalid family id provided.");
      }
    }
  }

  @Override
  public GetFamilyDetailsResponse doExecute(GetFamilyDetailsRequest opRequest)
      throws OperationException {
    int samajId = getSamajIdFromSession();
    int familyId = getFamilyIdFromRequestOrSession(opRequest);

    log.info("Fetching family details for familyId {}.", familyId);
    // Fetch family details using family id.
    FamilyEntity familyDetailsFromDB = familyDao.getFamilyByFamilyId(samajId, familyId);
    // If family not found, throw ValidationException.
    if (familyDetailsFromDB == null) {
        log.error("Family not found for the provided family id {}", familyId);
      throw new ValidationException("Family not found");
    }

    // Fetch all the members of the family.
    List<FamilyMemberEntity> familyMemberEntities = familyMemberDao.allMembersForFamily(samajId, familyId);

    // Create a list of MemberSummaryDto from familyMemberEntities
    List<MemberSummaryDto> memberSummaryDtoList =
        familyMemberEntities.stream()
            .map(member -> MemberSummaryDto.getMemberSummaryDto(member, UNKNOWN_RELATIONSHIP, familyId))
            .toList();

    // Find the head of the family from the memberSummaryDto List
    MemberSummaryDto headOfFamily = getHeadOfFamily(memberSummaryDtoList);

    FamilyDetailsDto familyDetails = buildFamilyDetailsDto(familyId, familyDetailsFromDB, headOfFamily);

    // Create a map of memberId to MemberSummaryDto for easy lookup
    Map<Integer, MemberSummaryDto> familyMemberMap =
        memberSummaryDtoList.stream().collect(toMap(MemberSummaryDto::getMemberId, dto -> dto));

    // Build family tree starting from head of family.
    FamilyTreeNode familyTreeRootNode = buildFamilyTree(samajId, familyId, headOfFamily, familyMemberMap);

    List<MemberSummaryDto> memberListToDisplay = new ArrayList<>();

    // Get the member list from tree and add other members which does not have any relationship
    // defined with head of family.
    getMemberListToDisplay(familyTreeRootNode, memberSummaryDtoList, memberListToDisplay);

    return GetFamilyDetailsResponse.builder()
        .familyDetails(familyDetails)
        .familyRoot(familyTreeRootNode)
        .memberList(memberListToDisplay)
        .build();
  }

  private int getFamilyIdFromRequestOrSession(GetFamilyDetailsRequest opRequest) throws OperationException {
    if (opRequest == null || opRequest.getFamilyId() == null) {
      // If family id not provided in request, fetch family id from session user.
      FamilyMemberEntity familyMemberFromContext = getMemberEntityFromSession();
      if (familyMemberFromContext == null) {
        throw new ValidationException("Member is not linked to any family.");
      }
      return familyMemberFromContext.getFamilyId();
    } else {
      return opRequest.getFamilyId();
    }
  }

  private FamilyDetailsDto buildFamilyDetailsDto (int familyId, FamilyEntity familyDetailsFromDB, MemberSummaryDto headOfFamily) {
    boolean canLoggedInUserUpdateFamily = canLoggedInUserUpdateFamily(familyId);
    boolean canLoggedInUserAddNewMember = canLoggedInUserAddNewMember(familyId);
    if (headOfFamily == null) {
      log.error("Head of family not found in family members list.");
      return FamilyDetailsDto.fromFamilyDBEntity(
                      familyDetailsFromDB,
                      "Head of Family not defined",
                      canLoggedInUserUpdateFamily,
                      canLoggedInUserAddNewMember);
    } else {
      log.info("Head of family found: {}", headOfFamily.getFirstName());
      return FamilyDetailsDto.fromFamilyDBEntity(
                      familyDetailsFromDB,
                      headOfFamily.getFirstName() + " " + headOfFamily.getLastName(),
                      canLoggedInUserUpdateFamily,
                      canLoggedInUserAddNewMember);
    }
  }

  private FamilyTreeNode buildFamilyTree(
          int samajId, int selectedFamilyId, MemberSummaryDto headOfFamily, Map<Integer, MemberSummaryDto> familyMemberMap) {
    if (headOfFamily == null) {
      log.error("Head of family is null, cannot build family tree.");
      return null;
    }

    // Build tree structure starting from head of family.
    headOfFamily.setFamilyRelationship(HEAD_OF_FAMILY);
    headOfFamily.setSelectedNode(true);

    FamilyTreeNode rootNode = new FamilyTreeNode(headOfFamily);
    Set<Integer> memberIdsAlreadyAddedToTree = new HashSet<>();

    addSpouseAndChildren(samajId, selectedFamilyId, rootNode, familyMemberMap, memberIdsAlreadyAddedToTree);
    FamilyTreeNode parentNode = addParentsToFamilyTree(samajId, selectedFamilyId, rootNode);
    if(rootNode != parentNode) {
        //If node is not same i.e. parents were added to tree.
        rootNode = parentNode;
        // In case, head of family's parents has other kids, that should be added in tree i.e. Sibling of head of family
        addSpouseAndChildren(samajId, selectedFamilyId, rootNode, familyMemberMap, memberIdsAlreadyAddedToTree);
    }
    return rootNode;
  }

  private FamilyTreeNode addParentsToFamilyTree(
          int samajId, int selectedFamilyId, FamilyTreeNode rootNode) {
    if (rootNode == null || rootNode.getMember() == null) {
      log.error("Tree node or member is null, cannot add parents.");
      return rootNode;
    }
    MemberSummaryDto currentMember = rootNode.getMember();
    List<MemberSummaryDto> parentsForMember = getParentsForMember(samajId, currentMember.getMemberId(), currentMember.getFirstName(), selectedFamilyId);
    if (parentsForMember == null || parentsForMember.isEmpty()) {
      return rootNode;
    }

      log.info(
              "Adding parents to tree: {}", currentMember.getFirstName());
      FamilyTreeNode parentNode = null;
      MemberSummaryDto father = null;
      MemberSummaryDto mother = null;
      for (MemberSummaryDto parent : parentsForMember) {
        if (parent.getGender() == Gender.Male) {
          father = parent;
        } else {
          mother = parent;
        }
      }

      if (father != null) {
        parentNode = new FamilyTreeNode(father);
        if(mother != null) {
          parentNode.setSpouse(mother);
        }
      } else {
        parentNode = new FamilyTreeNode(mother);
      }
      parentNode.addChild(rootNode);
      return parentNode;
  }

  /**
   * Recursively add spouse and children to the tree node.
   *
   * @param treeNode
   * @param familyMemberMap
   */
  private void addSpouseAndChildren(
          int samajId, int selectedFamilyId, FamilyTreeNode treeNode,
          Map<Integer, MemberSummaryDto> familyMemberMap,
          Set<Integer> memberIdsAlreadyAddedToTree) {

    if (treeNode == null || treeNode.getMember().getMaritalStatus() == MaritalStatus.Single) {
      // If member is single, no spouse or children to add.
      return;
    }

    MemberSummaryDto currentMember = treeNode.getMember();
    log.info("Adding spouse and children for member: {}:{}", currentMember.getMemberId(), currentMember.getFirstName());
    if(memberIdsAlreadyAddedToTree.contains(currentMember.getMemberId())) {
      return;
    } else {
      memberIdsAlreadyAddedToTree.add(currentMember.getMemberId());
    }

    int spouseMemberId = -1;
    // If spouse is already set, no need to add again.
    if (treeNode.getSpouse() == null) {
      MemberSummaryDto spouse = getSpouseForMember(samajId, selectedFamilyId, currentMember, familyMemberMap);
      if(spouse != null) {
        spouseMemberId = spouse.getMemberId();
        memberIdsAlreadyAddedToTree.add(spouseMemberId);
        treeNode.setSpouse(spouse);
      }
    }

    List<MemberSummaryDto> childrenForMember =
            getChildrenForMember(samajId, selectedFamilyId, currentMember, spouseMemberId, familyMemberMap);
    if (childrenForMember != null && !childrenForMember.isEmpty()) {
      for (MemberSummaryDto child : childrenForMember) {
        if(memberIdsAlreadyAddedToTree.contains(child.getMemberId())) {
            continue;
        }
        log.info("Adding child: {}:{} for {}", child.getMemberId(), child.getFirstName(), currentMember.getFirstName());
        FamilyTreeNode childNode = new FamilyTreeNode(child);
        treeNode.addChild(childNode);
        if(child.isBelongsToSameFamily()) {
          //Recursively add spouse and children for the child node only if the child belongs to same family
          addSpouseAndChildren(samajId, selectedFamilyId, childNode, familyMemberMap, memberIdsAlreadyAddedToTree);
        }
      }
    }
  }

  private List<MemberSummaryDto> getChildrenForMember(int samajId, int selectedFamilyId, MemberSummaryDto member, int spouseMemberId, Map<Integer, MemberSummaryDto> familyMemberMap) {
    List<MemberRelationshipEntity> childrenForMember =
            memberRelationshipDao.getChildrenForMemberById(member.getMemberId(), spouseMemberId);

    if(childrenForMember == null || childrenForMember.isEmpty()) {
      log.info("No children found for member with id: {}", member.getMemberId());
      return null;
    }
    List<MemberSummaryDto> children = new ArrayList<>();

    for (MemberRelationshipEntity childRelation : childrenForMember) {
      MemberSummaryDto child = familyMemberMap.get(childRelation.getRelatedMemberId());
      if (child == null) {
        child = getMemberSummaryDtoFromDB(samajId, childRelation.getRelatedMemberId(), selectedFamilyId);
      }
      if (child == null) {
        log.warn("Child member with id {} not found in DB.", childRelation.getRelatedMemberId());
        continue;
      }
      String familyRelationship = null;
      if (child.getGender() == Gender.Male) {
        familyRelationship = SON_OF_MEMBER.formatted(member.getFirstName());
      } else {
        familyRelationship = DAUGHTER_OF_MEMBER.formatted(member.getFirstName());
      }
      child.setFamilyRelationship(familyRelationship);
      children.add(child);
    }
    return children;
  }

  private MemberSummaryDto getSpouseForMember (int samajId, int selectedFamilyId, MemberSummaryDto member, Map<Integer, MemberSummaryDto> familyMemberMap) {
    MemberRelationshipEntity spouseForMember =
            memberRelationshipDao.getSpouseForMemberById(member.getMemberId());
    if (spouseForMember == null) {
      return null;
    }

    int spouseMemberId = spouseForMember.getRelatedMemberId();
    MemberSummaryDto spouse = familyMemberMap.get(spouseMemberId);
    if (spouse == null) {
      log.info(
              "Spouse not found in family member map, fetching from DB for member id: {}",
              spouseMemberId);
      spouse = getMemberSummaryDtoFromDB(samajId, spouseMemberId, selectedFamilyId);
    }
    //If spouse missing in family map as well as in DB
    if (spouse == null) {
      return null;
    }

    String familyRelationship = null;
    if (member.getGender() == Gender.Male) {
      familyRelationship = WIFE_OF_MEMBER.formatted(member.getFirstName());
    } else {
      familyRelationship = HUSBAND_OF_MEMBER.formatted(member.getFirstName());
    }
    spouse.setFamilyRelationship(familyRelationship);
    return spouse;
  }

  private void getMemberListToDisplay(
      FamilyTreeNode familyTreeRootNode,
      List<MemberSummaryDto> allFamilyMemberList,
      List<MemberSummaryDto> memberListToDisplay) {
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

  private MemberSummaryDto getHeadOfFamily(List<MemberSummaryDto> familyMembers) {
    for (MemberSummaryDto member : familyMembers) {
      if (member.isHeadOfFamily()) {
        return member;
      }
    }
    return null;
  }
}
