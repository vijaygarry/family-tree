package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.entity.Address;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("GetMemberProfileOperation")
@Scope("prototype")
public class GetMemberProfileOperation extends AbstractOperation <GetMemberProfileRequest, GetMemberProfileResponse> {

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Autowired
    private AddressDao addressDao;

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
        getRelationships(member);

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

        GetMemberProfileResponse response = GetMemberProfileResponse.builder()
                .memberProfile(profile)
                .build();

        return response;
    }

    private List<RelationshipDto> getRelationships(FamilyMember member) {
        return null;
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
