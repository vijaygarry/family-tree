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
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Log4j2
@Component("GetMemberProfileOperation")
@Scope("prototype")
public class GetMemberProfileOperation extends FamilyAbstractOperation <GetMemberProfileRequest, GetMemberProfileResponse> {

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

        List<MemberSummaryDto> parents = getParents(memberEntity);
        MemberSummaryDto spouse = getSpouse(memberEntity);
        List<MemberSummaryDto> children = getChildrenForMember(memberEntity, spouse);
        List<MemberSummaryDto> siblings = getSiblings(memberEntity, parents);
        boolean canLoggedInUserUpdateMember = canLoggedInUserUpdateMember(memberEntity.getFamilyId());
        AddressDto familyAddress = getFamilyAddress(memberEntity);
        AddressDto memberAddress = null;
        if(!memberEntity.isAddressSameAsFamily()) {
            memberAddress = getMemberAddress(memberEntity);
        }
        MemberProfileDto memberProfile = MemberProfileDto.fromFamilyMemberDBEntity(memberEntity, memberAddress, familyAddress, canLoggedInUserUpdateMember);

        return GetMemberProfileResponse.builder()
                .memberProfile(memberProfile)
                .parents(parents)
                .spouse(spouse)
                .children(children)
                .siblings(siblings)
                .build();
    }


    public List<MemberSummaryDto> getParents(FamilyMemberEntity selectedMemberEntity) {

        log.info("Adding parents for member: {}", selectedMemberEntity.getFirstName());
        List<MemberRelationshipEntity> parents = memberRelationshipDao.getParentsForMemberById(selectedMemberEntity.getMemberId());
        if (parents == null || parents.isEmpty()) {
            log.info("No parents found for member: {}", selectedMemberEntity.getFirstName());
            return null;
        }
        MemberSummaryDto father = null;
        MemberSummaryDto mother = null;
        for (MemberRelationshipEntity parentRelationship : parents) {
            MemberSummaryDto parent = getMemberSummaryDtoFromDB(parentRelationship.getMemberId());
            if (parent != null) {
                if (parent.getGender() == Gender.Male) {
                    father = parent;
                    father.setFamilyRelationship(FATHER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
                } else {
                    mother = parent;
                    mother.setFamilyRelationship(MOTHER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
                }
            }
        }
        List<MemberSummaryDto> parentList = new ArrayList<>();
        if(father != null) {
            log.info("Father found for member {}: {}", selectedMemberEntity.getFirstName(), father.getFirstName());
            parentList.add(father);
        }
        if (mother != null) {
            log.info("Mother found for member {}: {}", selectedMemberEntity.getFirstName(), mother.getFirstName());
            parentList.add(mother);
        }
        return parentList;
    }

    public MemberSummaryDto getSpouse(FamilyMemberEntity selectedMemberEntity) {
        if(selectedMemberEntity.getMaritalStatus() == null || selectedMemberEntity.getMaritalStatus() == MaritalStatus.Single) {
            log.info("Member {} is not married, so no spouse.", selectedMemberEntity.getFirstName());
            return null;
        }
        MemberRelationshipEntity spouseForMember = memberRelationshipDao.getSpouseForMemberById(selectedMemberEntity.getMemberId());
        if(spouseForMember != null) {
            MemberSummaryDto spouse = getMemberSummaryDtoFromDB(spouseForMember.getRelatedMemberId());
            if(spouse != null) {
                if(selectedMemberEntity.getGender() == Gender.Male) {
                    spouse.setFamilyRelationship(WIFE_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
                } else {
                    spouse.setFamilyRelationship(HUSBAND_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
                }
                return spouse;
            } else {
                log.warn("Spouse member with id {} not found in DB.", spouseForMember.getRelatedMemberId());
            }
        }
        return null;
    }

    /**
        Get children for the member and set the relationship as Son of abc or Daughter of abc
     */
    private List<MemberSummaryDto> getChildrenForMember(FamilyMemberEntity selectedMemberEntity, MemberSummaryDto selectedMemberSpouseDto) {
        if(selectedMemberEntity.getMaritalStatus() == null || selectedMemberEntity.getMaritalStatus() == MaritalStatus.Single) {
            log.info("Member {} is not married, so no children.", selectedMemberEntity.getFirstName());
            return null;
        }
        int spouseMemberId = selectedMemberSpouseDto == null ? -1 : selectedMemberSpouseDto.getMemberId();
        List<MemberSummaryDto> children = new ArrayList<>();
        List<MemberRelationshipEntity> childrenForMember = memberRelationshipDao.getChildrenForMemberById(selectedMemberEntity.getMemberId(), spouseMemberId);
        if (childrenForMember != null) {
            for (MemberRelationshipEntity childRelation : childrenForMember) {
                MemberSummaryDto child = getMemberSummaryDtoFromDB(childRelation.getRelatedMemberId());
                if (child != null) {
                    if(child.getGender() == Gender.Male) {
                        child.setFamilyRelationship(SON_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
                    } else {
                        child.setFamilyRelationship(DAUGHTER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
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

    public List<MemberSummaryDto> getSiblings (FamilyMemberEntity selectedMemberEntity, List<MemberSummaryDto> parents) {
        if(parents == null || parents.isEmpty()) {
            log.info("No parents found for member: {}, so cannot find siblings.", selectedMemberEntity.getFirstName());
            return null;
        }
        int parentId1 = -1;
        int parentId2 = -1;
        if(parents.size() == 1) {
            parentId1 = parents.get(0).getMemberId();
        } else  {
            parentId1 = parents.get(0).getMemberId();
            parentId2 = parents.get(1).getMemberId();
        }

        List<MemberSummaryDto> siblingsFromDb = getChildrenForMember(parentId1, parentId2);
        List<MemberSummaryDto> siblings = null;
        if (siblingsFromDb != null) {
            for (MemberSummaryDto child : siblingsFromDb) {
                if (child.getMemberId() != selectedMemberEntity.getMemberId()) {
                    if(siblings == null) {
                        siblings = new ArrayList<>();
                    }
                    log.info("Adding sibling: {} for member: {}", child.getFirstName(), selectedMemberEntity.getFirstName());
                    if(child.getGender() == Gender.Male) {
                        child.setFamilyRelationship(BROTHER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
                    } else {
                        child.setFamilyRelationship(SISTER_OF_MEMBER.formatted(selectedMemberEntity.getFirstName()));
                    }
                    siblings.add(child);
                }
            }
        }
        return siblings;
    }

    /**
     * Get children for the member and set the relationship as Son of abc or Daughter of abc
     * This is mainly to get siblings of a member by passing the parent ids.
     */
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
            log.info("No children found for member with parent1: {} and parent2: {}", memberId, spouseMemberId);
            return null;
        }
        return children;
    }

    private MemberSummaryDto getMemberSummaryDtoFromDB (int memberId) {
        FamilyMemberEntity memberFromDb = familyMemberDao.getMemberById(memberId);
        if(memberFromDb == null) {
            return null;
        }
        return MemberSummaryDto.getMemberSummaryDto(memberFromDb, UNKNOWN_RELATIONSHIP);
    }

    private AddressDto getMemberAddress(FamilyMemberEntity member){
        AddressEntity address = addressDao.getAddressById(member.getMemberAddressId());
        return AddressDto.getAddressDtoFromEntity(address);
    }

    private AddressDto getFamilyAddress(FamilyMemberEntity member){
        AddressEntity address = addressDao.getAddressByFamilyId(member.getFamilyId());
        return AddressDto.getAddressDtoFromEntity(address);
    }


}
