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
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.MemberSummaryDto;
import com.neasaa.familytree.utils.SessionUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.neasaa.familytree.operation.OperationNames.UPDATE_MY_FAMILY_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.BROTHER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.DAUGHTER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.FATHER_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.HUSBAND_OF_MEMBER;
import static com.neasaa.familytree.operation.family.GetFamilyDetailsOperation.MOTHER_OF_MEMBER;
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

        List<MemberSummaryDto> parents = getParents(memberEntity);
        MemberSummaryDto spouse = getSpouse(memberEntity);
        List<MemberSummaryDto> children = getChildrenForMember(memberEntity, spouse);
        List<MemberSummaryDto> siblings = getSiblings(memberEntity, parents);
        boolean familyMemberUpdateAllowedForUser = isFamilyMemberUpdateAllowedForUser(memberEntity.getFamilyId());
        GetMemberProfileResponse.MemberProfile memberProfile = GetMemberProfileResponse.MemberProfile.fromFamilyMemberDBEntity(memberEntity, getAddress(memberEntity), familyMemberUpdateAllowedForUser);

        return GetMemberProfileResponse.builder()
                .memberProfile(memberProfile)
                .parents(parents)
                .spouse(spouse)
                .children(children)
                .siblings(siblings)
                .build();
    }

    private boolean isFamilyMemberUpdateAllowedForUser(int familyId) {
        if(getContext() == null || getContext().getAppSessionUser() == null) {
            log.info("Operation context or AppSessionUser is null, cannot check if family member update allowed for user");
            return false;
        }
        FamilyMemberEntity memberEntity = SessionUtils.getFamilyMemberFromSession( getContext().getAppSessionUser());
        if(memberEntity == null) {
            log.info("Family member not found in session, cannot check if family member update allowed for user");
            return false;
        }
        if(memberEntity.getFamilyId() != familyId) {
            // Only allowed to edit own family members.
            return false;
        }
        return isOperationAllowedForUser(UPDATE_MY_FAMILY_MEMBER);
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

        List<MemberSummaryDto> children = new ArrayList<>();
        List<MemberRelationshipEntity> childrenForMember = memberRelationshipDao.getChildrenForMemberById(selectedMemberEntity.getMemberId(), selectedMemberSpouseDto.getMemberId());
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
