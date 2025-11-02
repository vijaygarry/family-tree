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
import com.neasaa.familytree.utils.SessionUtils;
import java.util.ArrayList;
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
    int familyId = -1;
    int samajId = getSamajIdFromSession();
    if (opRequest == null || opRequest.getFamilyId() == null) {
      // If family id not provided in request, fetch family id from session user.
      FamilyMemberEntity familyMemberFromContext = getMemberEntityFromSession();
      if (familyMemberFromContext == null) {
        throw new ValidationException("Member is not linked to any family.");
      }
      familyId = familyMemberFromContext.getFamilyId();
    } else {
      familyId = opRequest.getFamilyId();
    }

    log.info("Fetching family details for familyid {}.", familyId);
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
            .map(member -> MemberSummaryDto.getMemberSummaryDto(member, UNKNOWN_RELATIONSHIP))
            .toList();

    // Find the head of the family from the memberSummaryDto List
    MemberSummaryDto headOfFamily = getHeadOfFamily(memberSummaryDtoList);

    FamilyDetailsDto familyDetails = null;
    boolean canLoggedInUserUpdateFamily = canLoggedInUserUpdateFamily(familyId);
    boolean canLoggedInUserAddNewMember = canLoggedInUserAddNewMember(familyId);
    if (headOfFamily == null) {
      log.error("Head of family not found in family members list.");
      familyDetails =
          FamilyDetailsDto.fromFamilyDBEntity(
              familyDetailsFromDB,
              "Head of Family not defined",
              canLoggedInUserUpdateFamily,
              canLoggedInUserAddNewMember);
    } else {
      log.info("Head of family found: {}", headOfFamily.getFirstName());
      familyDetails =
          FamilyDetailsDto.fromFamilyDBEntity(
              familyDetailsFromDB,
              headOfFamily.getFirstName() + " " + headOfFamily.getLastName(),
              canLoggedInUserUpdateFamily,
              canLoggedInUserAddNewMember);
    }

    // Create a map of memberId to MemberSummaryDto for easy lookup
    Map<Integer, MemberSummaryDto> familyMemberMap =
        memberSummaryDtoList.stream().collect(toMap(MemberSummaryDto::getMemberId, dto -> dto));

    // Build family tree starting from head of family.
    FamilyTreeNode familyTreeRootNode = buildFamilyTree(samajId, headOfFamily, familyMemberMap);

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

  private FamilyTreeNode buildFamilyTree(
          int samajId, MemberSummaryDto headOfFamily, Map<Integer, MemberSummaryDto> familyMemberMap) {
    if (headOfFamily == null) {
      log.error("Head of family is null, cannot build family tree.");
      return null;
    }

    // Build tree structure starting from head of family.
    headOfFamily.setFamilyRelationship(HEAD_OF_FAMILY);
    headOfFamily.setSelectedNode(true);

    FamilyTreeNode rootNode = new FamilyTreeNode(headOfFamily);
    addSpouseAndChildren(samajId, rootNode, familyMemberMap, Set.of());
    FamilyTreeNode parentNode = addParentsToFamilyTree(samajId, rootNode);
    if(rootNode != parentNode) {
        //If node is not same i.e. parents were added to tree.
        rootNode = parentNode;
        addSpouseAndChildren(samajId, rootNode, familyMemberMap, Set.of(headOfFamily.getMemberId()));
    }
    return rootNode;
  }

  private FamilyTreeNode addParentsToFamilyTree(
          int samajId, FamilyTreeNode rootNode) {
    if (rootNode == null || rootNode.getMember() == null) {
      log.error("Tree node or member is null, cannot add parents.");
      return rootNode;
    }
    MemberSummaryDto currentMember = rootNode.getMember();
    List<MemberSummaryDto> parentsForMember = getParentsForMember(samajId, currentMember.getMemberId(), currentMember.getFirstName());
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
          int samajId, FamilyTreeNode treeNode, Map<Integer, MemberSummaryDto> familyMemberMap, Set<Integer> ignoredMemberIds) {

    if (treeNode == null || treeNode.getMember().getMaritalStatus() == MaritalStatus.Single) {
      // If member is single, no spouse or children to add.
      return;
    }
    MemberSummaryDto currentMember = treeNode.getMember();
    if(ignoredMemberIds.contains(currentMember.getMemberId())) {
      return;
    }

    int spouseMemberId = -1;
    // If spouse is already set, no need to add again.
    if (treeNode.getSpouse() == null) {
      MemberRelationshipEntity spouseForMember =
          memberRelationshipDao.getSpouseForMemberById(currentMember.getMemberId());
      if (spouseForMember != null) {
        spouseMemberId = spouseForMember.getRelatedMemberId();
        MemberSummaryDto spouse = familyMemberMap.get(spouseMemberId);
        if (spouse == null) {
          log.info(
              "Spouse not found in family member map, fetching from DB for member id: {}",
              spouseMemberId);
          spouse = getMemberSummaryDtoFromDB(samajId, spouseMemberId);
        }
        if (spouse != null) {
          String familyRelationship = null;
          if (currentMember.getGender() == Gender.Male) {
            familyRelationship = WIFE_OF_MEMBER.formatted(currentMember.getFirstName());
          } else {
            familyRelationship = HUSBAND_OF_MEMBER.formatted(currentMember.getFirstName());
          }
          spouse.setFamilyRelationship(familyRelationship);
          treeNode.setSpouse(spouse);
        }
      }
    }

    List<MemberRelationshipEntity> childrenForMember =
        memberRelationshipDao.getChildrenForMemberById(currentMember.getMemberId(), spouseMemberId);
    if (childrenForMember != null) {
      for (MemberRelationshipEntity childRelation : childrenForMember) {
        MemberSummaryDto child = familyMemberMap.get(childRelation.getRelatedMemberId());
        if (child == null) {
          // Do not fetch child info from DB. Child not found in family member, because child
          // belongs to different family.
          log.info(
              "Child {} not found in family member map, skipping assuming child is part of other family.",
              childRelation.getRelatedMemberId());
          continue;
        }
        if(ignoredMemberIds.contains(child.getMemberId())) {
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
        addSpouseAndChildren(samajId, childNode, familyMemberMap, ignoredMemberIds);
      }
    }
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
