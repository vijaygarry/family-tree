package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.Address;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.entity.MemberRelationship;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.FamilyMemberDto;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        FamilyMember member = null;
        if (opRequest != null && opRequest.getMemberId() != null) {
            int memberId = opRequest.getMemberId();
            member = familyMemberDao.getMemberById(memberId);
        } else {
            String logonName = getContext().getAppSessionUser().getLogonName();
            member = familyMemberDao.getMemberByLogonName(logonName);
        }
        if (member == null) {
            throw new ValidationException("Member not found.");
        }

        FamilyMemberDto familyTreeRoot = getRelationships(member);

        GetMemberProfileResponse.MemberProfile profile = GetMemberProfileResponse.MemberProfile.builder()
                .memberId(member.getMemberId())
                .familyId(member.getFamilyId())
                .headOfFamily(member.isHeadOfFamily())
                .firstName(member.getFirstName())
                .firstNameInHindi(member.getFirstNameInHindi())
                .lastName(member.getLastName())
                .maidenLastName(member.getMaidenLastName())
                .nickName(member.getNickName())
                .nickNameInHindi(member.getNickNameInHindi())
                .addressSameAsFamily(member.isAddressSameAsFamily())
                .memberAddress(getAddress(member))
                .phone(member.getPhone())
                .isPhoneWhatsappRegistered(member.isPhoneWhatsappRegistered())
                .email(member.getEmail())
                .linkedinUrl(member.getLinkedinUrl())
                .gender(member.getGender())
                .birthDay(member.getBirthDay())
                .birthMonth(member.getBirthMonth().getShortMonthName())
                .birthYear(member.getBirthYear())
                .dateOfDeath(member.getDateOfDeath())
                .maritalStatus(member.getMaritalStatus())
                .educationDetails(member.getEducationDetails())
                .occupation(member.getOccupation())
                .workingAt(member.getWorkingAt())
                .hobby(member.getHobby())
                .profileImage(member.getProfileImage())
                .profileImageThumbnail(member.getProfileImageThumbnail())
                .lastUpdatedDate(member.getLastUpdatedDate())
                .build();

        return GetMemberProfileResponse.builder()
                .memberProfile(profile)
                .familyTreeRoot(familyTreeRoot)
                .build();
    }

    private FamilyMemberDto getRelationships(FamilyMember memberEntity) {
        FamilyMemberDto memberDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(memberEntity);
        FamilyMemberDto familyTreeRoot = memberDto;

        //Get all the relationships of the member.
        List<MemberRelationship> memberDirectRelationships = memberRelationshipDao.getRelationshipByMemberIdOrRelatedMemberId(memberEntity.getMemberId());
        memberDto.setSpouse(getSpouseDtoForMember(memberEntity, memberDirectRelationships));
        memberDto.setChildren(getChildrenDtoForMember(memberDto, memberDirectRelationships));

        return memberDto;
    }

    private FamilyMemberDto getSpouseDtoForMember(FamilyMember memberEntity, List<MemberRelationship> memberDirectRelationships) {

        if (memberDirectRelationships == null || memberDirectRelationships.isEmpty()) {
            return null;
        }
        int spouseMemberId = -1;
        for (MemberRelationship relationship : memberDirectRelationships) {
            if (relationship.getRelationshipType() == RelationshipType.Wife || relationship.getRelationshipType() == RelationshipType.Husband) {
                if (relationship.getMemberId() == memberEntity.getMemberId()) {
                    spouseMemberId = relationship.getRelatedMemberId();
                } else {
                    spouseMemberId = relationship.getMemberId();
                }
                FamilyMember spouseMemberEntity = familyMemberDao.getMemberById(spouseMemberId);
                return FamilyMemberDto.getFamilyMemberDtoFromDBEntity(spouseMemberEntity);
            }
        }
        return null;
    }

    private List<FamilyMemberDto> getChildrenDtoForMember(FamilyMemberDto memberDto, List<MemberRelationship> memberDirectRelationships) {
        List<FamilyMemberDto> children = null;
        List<Integer> memberIds = List.of(memberDto.getMemberId());
        if(memberDto.getSpouse() != null) {
            memberIds = List.of(memberDto.getMemberId(), memberDto.getSpouse().getMemberId());
        }
        List<MemberRelationship> childrenRelationship = memberRelationshipDao.getRelatedMembersByIdAndRelationType(memberIds, List.of(RelationshipType.Son, RelationshipType.Daughter));
        if(childrenRelationship == null || childrenRelationship.isEmpty()) {
            return null;
        }
        for (MemberRelationship relationship : childrenRelationship) {
            FamilyMember childMemberEntity = familyMemberDao.getMemberById(relationship.getRelatedMemberId());
            if (childMemberEntity != null) {
                if (children == null) {
                    children = new ArrayList<>();
                } else if (children.stream().anyMatch(child -> child.getMemberId() == childMemberEntity.getMemberId())) {
                    continue; // Skip if already added
                }
                children.add(FamilyMemberDto.getFamilyMemberDtoFromDBEntity(childMemberEntity));
            }
        }
        return children;
    }

    private AddressDto getAddress(FamilyMember member){
        Address address = null;
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
