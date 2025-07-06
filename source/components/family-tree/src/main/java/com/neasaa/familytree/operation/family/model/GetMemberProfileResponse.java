package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
public class GetMemberProfileResponse extends OperationResponse {
    private static final long serialVersionUID = -3478291045823901842L;

    private MemberProfile memberProfile;
    private List<RelationshipDto> relationships;

    @Getter
    @Builder
    public static class MemberProfile {
        private int memberId;
        private int familyId;
        private boolean headOfFamily;
        private String firstName;
        private String firstNameInHindi;
        private String lastName;
        private String maidenLastName;
        private String nickName;
        private String nickNameInHindi;
        private boolean addressSameAsFamily;
        private AddressDto memberAddress;
        private String phone;
        private boolean isPhoneWhatsappRegistered;
        private String email;
        private String linkedinUrl;
        private Gender gender;
        private short birthDay;
        private String birthMonth;
        private short birthYear;
        private Date dateOfDeath;
        private MaritalStatus maritalStatus;
        private String educationDetails;
        private String occupation;
        private String workingAt;
        private String hobby;
        private String profileImage;
        private String profileImageThumbnail;
        private Date lastUpdatedDate;
    }
}
