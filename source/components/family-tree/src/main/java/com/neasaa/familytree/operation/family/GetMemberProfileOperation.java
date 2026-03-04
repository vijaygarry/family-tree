package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.MemberProfileDto;
import com.neasaa.familytree.operation.family.model.MemberSummaryDto;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component("GetMemberProfileOperation")
@Scope("prototype")
public class GetMemberProfileOperation
    extends FamilyAbstractOperation<GetMemberProfileRequest, GetMemberProfileResponse> {

  @Override
  public String getOperationName() {
    return OperationNames.GET_MEMBER_PROFILE;
  }

  @Override
  public void doValidate(GetMemberProfileRequest opRequest) throws OperationException {
    if (opRequest != null && opRequest.getMemberId() != null) {
      if (opRequest.getMemberId() < 1) {
        throw new ValidationException("Invalid member id provided.");
      }
    }
  }

  @Override
  public GetMemberProfileResponse doExecute(GetMemberProfileRequest opRequest)
      throws OperationException {
    FamilyMemberEntity memberEntity = null;
    int samajId = getSamajIdFromSession();

    if (opRequest != null && opRequest.getMemberId() != null) {
      int memberId = opRequest.getMemberId();
      memberEntity = familyMemberDao.getMemberById(samajId, memberId);
    } else {
      String logonName = getContext().getAppSessionUser().getLogonName();
      //TODO: Get member id from session instead of looking up member by logon name
      memberEntity = familyMemberDao.getMemberByLogonName(logonName);
    }
    if (memberEntity == null) {
      throw new ValidationException("Member not found.");
    }

    List<MemberSummaryDto> parents = getParents(samajId, memberEntity);
    MemberSummaryDto spouse = getSpouse(samajId, memberEntity);
    List<MemberSummaryDto> children = getChildrenForMember(samajId, memberEntity, spouse);
    List<MemberSummaryDto> siblings = getSiblings(samajId, memberEntity, parents);
    boolean canLoggedInUserUpdateMember = canLoggedInUserUpdateMember(memberEntity.getFamilyId());
    AddressDto familyAddress = getFamilyAddress(memberEntity.getFamilyId());
    AddressDto memberAddress = null;
    if (!memberEntity.isAddressSameAsFamily()) {
      memberAddress = getMemberAddress(memberEntity);
    }
    MemberProfileDto memberProfile =
        MemberProfileDto.fromFamilyMemberDBEntity(
            memberEntity, memberAddress, familyAddress, canLoggedInUserUpdateMember);

    return GetMemberProfileResponse.builder()
        .memberProfile(memberProfile)
        .parents(parents)
        .spouse(spouse)
        .children(children)
        .siblings(siblings)
        .build();
  }

  public List<MemberSummaryDto> getParents(int samajId, FamilyMemberEntity selectedMemberEntity) {

    log.info("Adding parents for member: {}", selectedMemberEntity.getFirstName());
    List<MemberRelationshipEntity> parents =
        memberRelationshipDao.getParentsForMemberById(selectedMemberEntity.getMemberId());
    if (parents == null || parents.isEmpty()) {
      log.info("No parents found for member: {}", selectedMemberEntity.getFirstName());
      return null;
    }
    MemberSummaryDto father = null;
    MemberSummaryDto mother = null;
    for (MemberRelationshipEntity parentRelationship : parents) {
      MemberSummaryDto parent = getMemberSummaryDtoFromDB(samajId, parentRelationship.getMemberId(), selectedMemberEntity.getFamilyId());
      if (parent != null) {
        if (parent.getGender() == Gender.Male) {
          father = parent;
          father.setFamilyRelationship(
              FATHER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
        } else {
          mother = parent;
          mother.setFamilyRelationship(
              MOTHER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
        }
      }
    }
    List<MemberSummaryDto> parentList = new ArrayList<>();
    if (father != null) {
      log.info(
          "Father found for member {}: {}",
          selectedMemberEntity.getFirstName(),
          father.getFirstName());
      parentList.add(father);
    }
    if (mother != null) {
      log.info(
          "Mother found for member {}: {}",
          selectedMemberEntity.getFirstName(),
          mother.getFirstName());
      parentList.add(mother);
    }
    return parentList;
  }

  public MemberSummaryDto getSpouse(int samajId, FamilyMemberEntity selectedMemberEntity) {
    if (selectedMemberEntity.getMaritalStatus() == null
        || selectedMemberEntity.getMaritalStatus() == MaritalStatus.Single) {
      log.info("Member {} is not married, so no spouse.", selectedMemberEntity.getFirstName());
      return null;
    }
    MemberRelationshipEntity spouseForMember =
        memberRelationshipDao.getSpouseForMemberById(selectedMemberEntity.getMemberId());
    if (spouseForMember != null) {
      MemberSummaryDto spouse = getMemberSummaryDtoFromDB(samajId, spouseForMember.getRelatedMemberId(), selectedMemberEntity.getFamilyId());
      if (spouse != null) {
        if (selectedMemberEntity.getGender() == Gender.Male) {
          spouse.setFamilyRelationship(
              WIFE_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
        } else {
          spouse.setFamilyRelationship(
              HUSBAND_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
        }
        return spouse;
      } else {
        log.warn("Spouse member with id {} not found in DB.", spouseForMember.getRelatedMemberId());
      }
    }
    return null;
  }

  /** Get children for the member and set the relationship as Son of abc or Daughter of abc */
  private List<MemberSummaryDto> getChildrenForMember(
      int samajId, FamilyMemberEntity selectedMemberEntity, MemberSummaryDto selectedMemberSpouseDto) {
    if (selectedMemberEntity.getMaritalStatus() == null
        || selectedMemberEntity.getMaritalStatus() == MaritalStatus.Single) {
      log.info("Member {} is not married, so no children.", selectedMemberEntity.getFirstName());
      return null;
    }
    int spouseMemberId =
        selectedMemberSpouseDto == null ? -1 : selectedMemberSpouseDto.getMemberId();
    List<MemberSummaryDto> children = new ArrayList<>();
    List<MemberRelationshipEntity> childrenForMember =
        memberRelationshipDao.getChildrenForMemberById(
            selectedMemberEntity.getMemberId(), spouseMemberId);
    if (childrenForMember != null) {
      for (MemberRelationshipEntity childRelation : childrenForMember) {
        MemberSummaryDto child = getMemberSummaryDtoFromDB(samajId, childRelation.getRelatedMemberId(), selectedMemberEntity.getFamilyId());
        if (child != null) {
          if (child.getGender() == Gender.Male) {
            child.setFamilyRelationship(
                SON_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
          } else {
            child.setFamilyRelationship(
                DAUGHTER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
          }
          children.add(child);
        }
      }
    } else {
      log.info("No children found for member with id: {}", selectedMemberEntity.getMemberId());
      return null;
    }
    return children;
  }

  public List<MemberSummaryDto> getSiblings(
      int samajId, FamilyMemberEntity selectedMemberEntity, List<MemberSummaryDto> parents) {
    if (parents == null || parents.isEmpty()) {
      log.info(
          "No parents found for member: {}, so cannot find siblings.",
          selectedMemberEntity.getFirstName());
      return null;
    }
    int parentId1 = -1;
    int parentId2 = -1;
    if (parents.size() == 1) {
      parentId1 = parents.get(0).getMemberId();
    } else {
      parentId1 = parents.get(0).getMemberId();
      parentId2 = parents.get(1).getMemberId();
    }

    List<MemberSummaryDto> siblingsFromDb = getChildrenForMember(samajId, parentId1, parentId2, parents.get(0).getFamilyId());
    List<MemberSummaryDto> siblings = null;
    if (siblingsFromDb != null) {
      for (MemberSummaryDto child : siblingsFromDb) {
        if (child.getMemberId() != selectedMemberEntity.getMemberId()) {
          if (siblings == null) {
            siblings = new ArrayList<>();
          }
          log.info(
              "Adding sibling: {} for member: {}",
              child.getFirstName(),
              selectedMemberEntity.getFirstName());
          if (child.getGender() == Gender.Male) {
            child.setFamilyRelationship(
                BROTHER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
          } else {
            child.setFamilyRelationship(
                SISTER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
          }
          siblings.add(child);
        }
      }
    }
    return siblings;
  }

  private AddressDto getMemberAddress(FamilyMemberEntity member) {
    AddressEntity address = addressDao.getAddressById(member.getMemberAddressId());
    return AddressDto.getAddressDtoFromEntity(address);
  }
}
