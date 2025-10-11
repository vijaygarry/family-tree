package com.neasaa.familytree.operation.family.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.neasaa.familytree.utils.DataFormatter.formatBirthDate;
import static com.neasaa.familytree.utils.DataFormatter.getFormattedMemberAge;
import static com.neasaa.familytree.utils.DataFormatter.getISOFormatDate;


@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberSummaryDto {

    private int memberId;
    private int familyId;
    private boolean headOfFamily;
    private String firstName;
    private String firstNameInHindi;
    private String lastName;
    private String maidenLastName;
    private String nickName;
    private String nickNameInHindi;
    private String familyRelationship; // Son of abc or Wife of abc or Father of abc
    private Gender gender;
    private String birthDate; // Format: dd-MMM-yyyy or MMM-yyyy
    private boolean isAlive;
    private String age; // Format (xx years), if dead then (xxxx - yyyy)
    private String phone;
    private boolean isPhoneWhatsappRegistered;
    private MaritalStatus maritalStatus;
    private String weddingDate; // Date is in ISO format i.e. yyyy-MM-dd
    private String educationDetails;
    private String occupation;
    private String profileImageThumbnail;
    private boolean selectedNode;

    public static MemberSummaryDto getMemberSummaryDto(FamilyMemberEntity familyMemberEntity, String familyRelationship) {
        return MemberSummaryDto.builder()
                .memberId(familyMemberEntity.getMemberId())
                .familyId(familyMemberEntity.getFamilyId())
                .headOfFamily(familyMemberEntity.isHeadOfFamily())
                .firstName(familyMemberEntity.getFirstName())
                .firstNameInHindi(familyMemberEntity.getFirstNameInHindi())
                .lastName(familyMemberEntity.getLastName())
                .maidenLastName(familyMemberEntity.getMaidenLastName())
                .nickName(familyMemberEntity.getNickName())
                .nickNameInHindi(familyMemberEntity.getNickNameInHindi())
                .familyRelationship(familyRelationship)
                .gender(familyMemberEntity.getGender())
                .birthDate(formatBirthDate(familyMemberEntity.getBirthDay(), familyMemberEntity.getBirthMonth(), familyMemberEntity.getBirthYear()))
                .isAlive(familyMemberEntity.isAlive())
                .age(getFormattedMemberAge(familyMemberEntity))
                .phone(familyMemberEntity.getPhone())
                .isPhoneWhatsappRegistered(familyMemberEntity.isPhoneWhatsappRegistered())
                .maritalStatus(familyMemberEntity.getMaritalStatus())
                .weddingDate(getISOFormatDate(familyMemberEntity.getWeddingDate()))
                .educationDetails(familyMemberEntity.getEducationDetails())
                .occupation(familyMemberEntity.getOccupation())
                .profileImageThumbnail(familyMemberEntity.getProfileImageThumbnail())
                .build();
    }

}
