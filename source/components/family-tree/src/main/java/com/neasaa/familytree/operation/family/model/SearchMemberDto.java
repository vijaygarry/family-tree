package com.neasaa.familytree.operation.family.model;

import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import lombok.Builder;
import lombok.Getter;

import static com.neasaa.familytree.utils.DataFormatter.formatBirthDate;
import static com.neasaa.familytree.utils.DataFormatter.getFormattedMemberAge;

@Builder
@Getter
public class SearchMemberDto {
    private int memberId;
    private String firstName;
    private String firstNameInHindi;
    private String lastName;
    private String region;
    private Gender gender;
    private String birthDate; // Format: dd-MMM-yyyy or MMM-yyyy
    private String age; // Format (xx years), if dead then (xxxx - yyyy)
    private boolean isAlive;
    private MaritalStatus maritalStatus;
    private String educationDetails;
    private String profileImageThumbnail;

    public static SearchMemberDto getSearchMemberDtoFromEntity (FamilyMemberEntity searchMember) {
        return SearchMemberDto.builder()
                .memberId(searchMember.getMemberId())
                .firstName(searchMember.getFirstName())
                .firstNameInHindi(searchMember.getFirstNameInHindi())
                .lastName(searchMember.getLastName())
                .region(searchMember.getMemberSearchText()) // Assuming region is stored in memberSearchText
                .gender(searchMember.getGender())
                .birthDate(
                        formatBirthDate(
                                searchMember.getBirthDay(),
                                searchMember.getBirthMonth(),
                                searchMember.getBirthYear()))
                .isAlive(searchMember.isAlive())
                .age(getFormattedMemberAge(searchMember))
                .maritalStatus(searchMember.getMaritalStatus())
                .educationDetails(searchMember.getEducationDetails())
                .profileImageThumbnail(searchMember.getProfileImageThumbnail())
                .build();
    }
}
