package com.neasaa.familytree.operation.family.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import com.neasaa.familytree.entity.FamilyMemberRegistrationEntity;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.Month;
import com.neasaa.familytree.utils.DataFormatter;
import java.io.Serial;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.neasaa.familytree.utils.Constants.CHHIPA_SAMAJ_ID;
import static com.neasaa.familytree.utils.Constants.MISSING_BIRTH_DATE_VALUE;
import static com.neasaa.familytree.utils.DataFormatter.parseISODateToLocalDate;

@Getter
@Setter
@ToString
public class FamilyRegistrationRequest extends OperationRequest {

  @Serial private static final long serialVersionUID = 1L;

  private FamilyDetails familyDetails;
  private List<Member> members;

  public void trimFields() {
    if (familyDetails != null) {
      familyDetails.trimFields();
    }
    if (members != null) {
      members.forEach(Member::trimFields);
    }
  }

  @Getter
  @Setter
  @ToString
  public static class FamilyDetails {
    private String surname;
    private String surnameInHindi;
    private String gotra;
    private String email;
    private String phone;
    private AddressDto familyAddress;

    public void trimFields() {
      if (surname != null) {
        surname = DataFormatter.capitalizeFirstLetter(surname);
      }
      if (surnameInHindi != null) {
        surnameInHindi = DataFormatter.capitalizeFirstLetter(surnameInHindi);
      }
      if (gotra != null) {
        gotra = DataFormatter.capitalizeFirstLetter(gotra);
      }
      if (email != null) {
        email = email.trim();
      }
      if (phone != null) {
        phone = DataFormatter.formatPhoneNumberForDBStorage(phone);
      }
      if (familyAddress != null) {
        familyAddress.trimFields();
        // Capitalize address fields
        if (familyAddress.getAddressLine1() != null) {
          familyAddress.setAddressLine1(DataFormatter.capitalizeFirstLetter(familyAddress.getAddressLine1()));
        }
        if (familyAddress.getAddressLine2() != null) {
          familyAddress.setAddressLine2(DataFormatter.capitalizeFirstLetter(familyAddress.getAddressLine2()));
        }
        if (familyAddress.getAddressLine3() != null) {
          familyAddress.setAddressLine3(DataFormatter.capitalizeFirstLetter(familyAddress.getAddressLine3()));
        }
        if (familyAddress.getCity() != null) {
          familyAddress.setCity(DataFormatter.capitalizeFirstLetter(familyAddress.getCity()));
        }
        if (familyAddress.getDistrict() != null) {
          familyAddress.setDistrict(DataFormatter.capitalizeFirstLetter(familyAddress.getDistrict()));
        }
        if (familyAddress.getState() != null) {
          familyAddress.setState(DataFormatter.capitalizeFirstLetter(familyAddress.getState()));
        }
        if (familyAddress.getCountry() != null) {
          familyAddress.setCountry(DataFormatter.capitalizeFirstLetter(familyAddress.getCountry()));
        }
      }
    }
  }

  @Getter
  @Setter
  @ToString
  public static class Member {
    private String firstName;
    private String firstNameInHindi;
    private String phoneNumber;
    private String gender;
    private String maritalStatus;
    private String weddingDate;
    private Short birthDay;
    private String birthMonth;
    private Short birthYear;
    private String email;
    private String educationDetails;
    private String occupation;
    private Boolean headOfFamily;
    private Relationship relationship;

    public void trimFields() {
      if (firstName != null) {
        firstName = DataFormatter.capitalizeFirstLetter(firstName);
      }
      if (firstNameInHindi != null) {
        firstNameInHindi = DataFormatter.capitalizeFirstLetter(firstNameInHindi);
      }
      if (phoneNumber != null) {
        phoneNumber = DataFormatter.formatPhoneNumberForDBStorage(phoneNumber);
      }
      if (gender != null) {
        gender = gender.trim();
      }
      if (maritalStatus != null) {
        maritalStatus = maritalStatus.trim();
      }
      if (weddingDate != null) {
        weddingDate = weddingDate.trim();
      }
      if(birthDay == null) {
        birthDay = MISSING_BIRTH_DATE_VALUE;
      }
      if (birthMonth != null) {
        birthMonth = birthMonth.trim();
      }
      if (email != null) {
        email = email.trim().toLowerCase();
      }
      if (educationDetails != null) {
        educationDetails = DataFormatter.capitalizeFirstLetter(educationDetails);
      }
      if (occupation != null) {
        occupation = DataFormatter.capitalizeFirstLetter(occupation);
      }
      if (relationship != null) {
        relationship.trimFields();
      }
    }

    public FamilyMemberRegistrationEntity getFamilyMemberRegistrationEntityFromRequest(int familyRegistrationRequestId) {
      FamilyMemberRegistrationEntity entity = new FamilyMemberRegistrationEntity();
      entity.setFamilyRequestId(familyRegistrationRequestId);
      entity.setSamajId(CHHIPA_SAMAJ_ID);
      if(getHeadOfFamily() != null && getHeadOfFamily()) {
        entity.setHeadOfFamily(true);
        entity.setRelationshipType("Self");
      } else {
        entity.setHeadOfFamily(false);
        entity.setRelationshipType(relationship.getRelationshipType());
      }

      entity.setFirstName(getFirstName());
      entity.setFirstNameInHindi(getFirstNameInHindi());
      entity.setGender(Gender.getGenderByString(getGender()));
      if (getBirthDay() != null) {
          entity.setBirthDay(getBirthDay());
      }
      if (getBirthMonth() != null) {
        entity.setBirthMonth(Month.fromName(birthMonth));
      }
      if (getBirthYear() != null) {
        entity.setBirthYear(getBirthYear());
      }
      entity.setMaritalStatus(MaritalStatus.getMaritalStatus(getMaritalStatus()));
      if (getWeddingDate() != null) {
        entity.setWeddingDate(parseISODateToLocalDate(getWeddingDate()));
      }
      entity.setPhone(getPhoneNumber());
      entity.setEmail(getEmail());
      entity.setAddressSameAsFamily(true);
      entity.setEducationDetails(getEducationDetails());
      entity.setOccupation(getOccupation());
      entity.setCreatedDate(new Date());
      return entity;
    }

  }

  @Getter
  @Setter
  @ToString
  public static class Relationship {
    private String relationshipType;
    private String memberName;

    public void trimFields() {
      if (relationshipType != null) {
        relationshipType = relationshipType.trim();
      }
      if (memberName != null) {
        memberName = DataFormatter.capitalizeFirstLetter(memberName);
      }
    }
  }
}