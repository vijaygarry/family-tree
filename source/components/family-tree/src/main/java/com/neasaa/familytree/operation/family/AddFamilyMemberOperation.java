package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;
import static com.neasaa.familytree.utils.Constants.MISSING_BIRTH_DATE_VALUE;
import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateBirthDate;

import java.util.List;

import com.neasaa.familytree.constants.ImageConstants;
import com.neasaa.familytree.entity.AddressEntity;
import com.neasaa.familytree.entity.FamilyEntity;
import com.neasaa.familytree.entity.FamilyMemberEntity;
import com.neasaa.familytree.entity.MemberRelationshipEntity;
import com.neasaa.familytree.enums.Month;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberResponse;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.utils.Constants;
import com.neasaa.familytree.utils.DataFormatter;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import com.neasaa.familytree.utils.RelationshipUtils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component ("AddFamilyMemberOperation")
@Scope("prototype")
public class AddFamilyMemberOperation extends AbstractOperation<AddFamilyMemberRequest, AddFamilyMemberResponse> {
	
	@Autowired
	private FamilyDao familyDao;
	
	@Autowired
	private FamilyMemberDao familyMemberDao;
	
	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private MemberRelationshipDao memberRelationshipDao;
	
	@Override
	public String getOperationName() {
		return OperationNames.ADD_MEMBER_TO_MY_FAMILY;
	}

	@Override
	public void doValidate(AddFamilyMemberRequest opRequest) throws OperationException {
		if (opRequest == null) {
			throw new ValidationException("Invalid request provided.");
		}
		checkValueRange(opRequest.getFamilyId(), 1, Integer.MAX_VALUE, "family id");
		checkValuePresent(opRequest.getFirstName(), "first name");
		checkValuePresent(opRequest.getGender(), "gender");
		if(Gender.getGenderByString(opRequest.getGender()) == null) {
			throw new ValidationException ("Invalid value for field gender");
		}

		validateBirthDate(opRequest.getBirthDay(), opRequest.getBirthMonth(), opRequest.getBirthYear());

		checkValuePresent(opRequest.getMaritalStatus(), "marital status");
		if(MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()) == null) {
			throw new ValidationException ("Invalid value for field marital status");
		}
		
		if(opRequest.getMemberAddress() != null) {
			FamilytreeValidationUtils.validateAddress(opRequest.getMemberAddress());
		}
		
		if(opRequest.getMemberAddress() != null && opRequest.isAddressSameAsFamily()) {
			throw new ValidationException ("Either select address same as family address or specify member address");
		}
		
		if(opRequest.getPhone() != null) {
			FamilytreeValidationUtils.validatePhoneNumber(opRequest.getPhone());
		}
		
		if(opRequest.getRelashinship() != null) {
			RelationshipType relationshipType = RelationshipType.getRelationshipType(opRequest.getRelashinship().getRelationshipType());
			if(relationshipType == null) {
				throw new ValidationException ("Invalid relationship type provided");
			}
		}
		
	}

	@Override
	public AddFamilyMemberResponse doExecute(AddFamilyMemberRequest opRequest) throws OperationException {
		log.info("Adding family member");
		FamilyEntity family = familyDao.getFamilyByFamilyId(opRequest.getFamilyId());
		if(family == null) {
			log.info("Family not found for family id {}", opRequest.getFamilyId());
			throw new ValidationException ("Family not found");
		}
		
		FamilyMemberEntity relatedMember = null;
		
		//Fetch list of family members
		List<FamilyMemberEntity> familyMembers = familyMemberDao.allMembersForFamily(opRequest.getFamilyId());
		if(familyMembers == null || familyMembers.isEmpty()) {
			// No member in family. So this member should be the head of family.
			if(!opRequest.isHeadOfFamily()) {
				throw new ValidationException ("First member should be the head of family");
			}
		} else {
			// There are already at least one member in family, so this new member can not be the head of family.
			if(opRequest.isHeadOfFamily()) {
				throw new ValidationException ("Other member is already a head of family");
			}
			// TODO: Uncomment this when relationship is implemented
			checkObjectPresent(opRequest.getRelashinship(), "relationship");

			relatedMember = familyMemberDao.getMemberById(opRequest.getRelashinship().getRelatedMemberId());
			if(relatedMember == null) {
				throw new ValidationException ("Related member does not exists");
			}
			log.info("Input Relationship {}'s {} is {} ({})", opRequest.getFirstName() , opRequest.getRelashinship().getRelationshipType(), opRequest.getRelashinship().getRelatedMemberName(), opRequest.getRelashinship().getRelatedMemberId());
		}

		AddressEntity memberAddress = getAddressFromRequest(opRequest);
		int addressId = Constants.MEMBER_ADDRESS_SAME_AS_FAMILY_ADDRESS;
		if(memberAddress != null) {
			addressId = addressDao.addAddress(memberAddress);
		}

		FamilyMemberEntity newMemberFromDb = familyMemberDao.addFamilyMember(getFamilyMemberFromRequest(opRequest, family, addressId));
		if(newMemberFromDb.isHeadOfFamily()) {
			familyDao.updateFamilyDisplayName(family, newMemberFromDb, getAuditInfo());
		}


		if(opRequest.getRelashinship() != null) {
			//Add relationship
			RelationshipType relationshipType = RelationshipType.getRelationshipType(opRequest.getRelashinship().getRelationshipType());
			if(relationshipType == null) {
				log.info("Invalid relationship type provided: {}", opRequest.getRelashinship().getRelationshipType());
				throw new ValidationException ("Invalid relationship type provided");
			}
			List<MemberRelationshipEntity> relationships = RelationshipUtils.buildRelationships(newMemberFromDb, relationshipType, relatedMember, getAuditInfo());
			MemberRelationshipEntity relationship = relationships.get(0);
			log.info("Member {}'s {} is {}", relationship.getMemberId(), relationship.getRelationshipType(), relationship.getRelatedMemberId());
			updateRelationships(relationships);
		}
		
		AddFamilyMemberResponse response = AddFamilyMemberResponse.builder().firstName(opRequest.getFirstName()).lastName(family.getFamilyName()).memberId(newMemberFromDb.getMemberId()).build();
		response.setOperationMessage(String.format("Member %s %s added successfully !!!", opRequest.getFirstName(), family.getFamilyName()));
		return response;
	}
	
	private AddressEntity getAddressFromRequest (AddFamilyMemberRequest opRequest) {
		AddressDto inputAddress = opRequest.getMemberAddress();
		if(inputAddress ==null) {
			log.info("Member address is not provided, not creating address for member");
			return null;
		}
		
		AuditInfo auditInfo = getAuditInfo();
		return AddressEntity.builder()
				.addressLine1(inputAddress.getAddressLine1())
				.addressLine2(inputAddress.getAddressLine2())
				.addressLine3(inputAddress.getAddressLine3())
				.city(inputAddress.getCity())
				.district(inputAddress.getDistrict())
				.state(inputAddress.getState())
				.postalCode(inputAddress.getPostalCode())
				.country(inputAddress.getCountry())
				.createdBy(auditInfo.getCreatedBy())
				.createdDate(auditInfo.getCreatedDate())
				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
				.build();
	}
	
	private FamilyMemberEntity getFamilyMemberFromRequest (AddFamilyMemberRequest opRequest, FamilyEntity family, int addressId) {
		AuditInfo auditInfo = getAuditInfo();
		String phoneNumber = DataFormatter.formatPhoneNumber(opRequest.getPhone());
		String emailId = opRequest.getEmail() != null ? opRequest.getEmail().toLowerCase().trim() : null;
		short birthDay = MISSING_BIRTH_DATE_VALUE;
		if(opRequest.getBirthDay() != null) {;
			birthDay = opRequest.getBirthDay();
		}

		return FamilyMemberEntity.builder()
				.familyId(family.getFamilyId())
				.headOfFamily(opRequest.isHeadOfFamily())
				.firstName(opRequest.getFirstName())
				.firstNameInHindi(opRequest.getFirstNameInHindi())
				.lastName(family.getFamilyName())
				.maidenLastName(opRequest.getMaidenLastName())
				.nickName(opRequest.getNickName())
				.nickNameInHindi(opRequest.getNickNameInHindi())
				.addressSameAsFamily(opRequest.isAddressSameAsFamily())
				.memberAddressId(addressId)
				.phone(phoneNumber)
				.isPhoneWhatsappRegistered(opRequest.isPhoneWhatsappRegistered())
				.email(emailId)
				.gender(Gender.getGenderByString(opRequest.getGender()))
				.birthDay(birthDay)
				.birthMonth(Month.fromName(opRequest.getBirthMonth()))
				.birthYear(opRequest.getBirthYear())
				.dateOfDeath(opRequest.getDateOfDeath())
				.maritalStatus(MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()))
				.weddingDate(opRequest.getWeddingDate())
				.educationDetails(opRequest.getEducationDetails())
				.occupation(opRequest.getOccupation())
				.hobby(opRequest.getHobby())
				.profileImage(getDefaultImagePath(opRequest))
				.profileImageThumbnail(getDefaultThumbnailImagePath(opRequest))
				.imageLastUpdated(auditInfo.getCreatedDate())
				.createdBy(auditInfo.getCreatedBy())
				.createdDate(auditInfo.getCreatedDate())
				.lastUpdatedBy(auditInfo.getLastUpdatedBy())
				.lastUpdatedDate(auditInfo.getLastUpdatedDate())
				.build();
	}
	
	private void updateRelationships (List<MemberRelationshipEntity> relationships) {
		log.info("Adding relationship");
		for(MemberRelationshipEntity relationship : relationships) {
			MemberRelationshipEntity memberRelationshipFromDb = memberRelationshipDao.getRelationshipBetweenMembers(relationship.getMemberId(), relationship.getRelatedMemberId());
			if(memberRelationshipFromDb == null) {
				log.info("Adding {} is {} of {}", relationship.getMemberId(), relationship.getRelationshipType(), relationship.getRelatedMemberId() );
				memberRelationshipDao.addMemberRelationship(relationship);
			} else {
				log.info("Relationship already exists between {} and {}", relationship.getMemberId(), relationship.getRelatedMemberId());
				if(memberRelationshipFromDb.getRelationshipType() != relationship.getRelationshipType()) {
					log.info("Relationship between member {} and {} is expected as {}, but found {}", relationship.getMemberId(), relationship.getRelatedMemberId(), relationship.getRelationshipType(), memberRelationshipFromDb.getRelationshipType());
				}
			}
		}
	}
	
	private static String getDefaultImagePath (AddFamilyMemberRequest opRequest) {
		Gender gender = Gender.getGenderByString(opRequest.getGender());
		int memberAge = DataFormatter.getMemberAgeInYears(opRequest.getBirthDay(), Month.fromName(opRequest.getBirthMonth()), opRequest.getBirthYear());
		if(gender == Gender.Female) {
			if (memberAge < 20) {
				return ImageConstants.DEFAULT_KID_GIRL_IMAGE;
			} else if (memberAge < 60) {
				MaritalStatus memberMaritalStatus = MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus());
				if(memberMaritalStatus == MaritalStatus.Single || memberMaritalStatus == MaritalStatus.Engaged) {
					return ImageConstants.DEFAULT_UNMARRIED_GIRL_IMAGE;
				}
				return ImageConstants.DEFAULT_MARRIED_WOMAN_IMAGE;
			} else {
				return ImageConstants.DEFAULT_OLD_WOMAN_IMAGE;
			}
		} else {
			if (memberAge < 20) {
				return ImageConstants.DEFAULT_KID_BOY_IMAGE;
			} else if (memberAge < 60) {
				return ImageConstants.DEFAULT_MAN_IMAGE;
			} else {
				return ImageConstants.DEFAULT_OLD_MAN_IMAGE;
			}
		}
	}
	
	private static String getDefaultThumbnailImagePath (AddFamilyMemberRequest opRequest) {
		return getDefaultImagePath(opRequest);
	}

}
