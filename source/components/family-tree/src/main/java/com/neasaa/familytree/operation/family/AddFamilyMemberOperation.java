package com.neasaa.familytree.operation.family;

import static com.neasaa.base.app.utils.ValidationUtils.checkObjectPresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValuePresent;
import static com.neasaa.base.app.utils.ValidationUtils.checkValueRange;

import java.time.Year;
import java.util.List;

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
import com.neasaa.familytree.entity.Address;
import com.neasaa.familytree.entity.Family;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.entity.MemberRelationship;
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
		return OperationNames.ADD_FAMILY_MEMBER;
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
		log.info("Input birth date: " + opRequest.getBirthDay() + "/" + opRequest.getBirthMonth() + "/" + opRequest.getBirthYear());
		if(opRequest.getBirthDay() != null) {
			checkValueRange(opRequest.getBirthDay().intValue(), 1, 31, "birth day");
		}
		
		checkObjectPresent(opRequest.getBirthMonth(), "birth month");
		if(opRequest.getBirthMonth() == null || opRequest.getBirthMonth().isEmpty()) {
			throw new ValidationException ("Invalid value for field birth month");
		}
		//Check if month is valid
		if(Month.fromName(opRequest.getBirthMonth()) == null) {
			throw new ValidationException ("Invalid value for field birth month");
		}
		
		checkObjectPresent(opRequest.getBirthYear(), "birth year");
		int currentYear = Year.now().getValue();
		checkValueRange(opRequest.getBirthYear().intValue(), 1900, currentYear, "birth year");
		
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
		Family family = familyDao.getFamilyByFamilyId(opRequest.getFamilyId());
		if(family == null) {
			log.info("Family not found for family id {}", opRequest.getFamilyId());
			throw new ValidationException ("Family not found");
		}
		
		FamilyMember relatedMember = null;
		
		//Fetch list of family members
		List<FamilyMember> familyMembers = familyMemberDao.allMembersForFamily(opRequest.getFamilyId());
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
//			checkObjectPresent(opRequest.getRelashinship(), "relationship");
//			relatedMember = RelationshipUtils.findMemberByIdInList(familyMembers, opRequest.getRelashinship().getRelatedMemberId());
//			if(relatedMember == null) {
//				throw new ValidationException ("Related member does not exists in same family");
//			}
		}
		
		
		
		Address memberAddress = getAddressFromRequest(opRequest);
		int addressId = Constants.MEMBER_ADDRESS_SAME_AS_FAMILY_ADDRESS;
		if(memberAddress != null) {
			addressId = addressDao.addAddress(memberAddress);
		}
		
		FamilyMember newMemberFromDb = familyMemberDao.addFamilyMember(getFamilyMemberFromRequest(opRequest, family, addressId));
//		if(!opRequest.isHeadOfFamily()) {
			//TODO: Uncomment this when relationship is implemented
			//Add relationship
//			RelationshipType relationshipType = RelationshipType.getRelationshipType(opRequest.getRelashinship().getRelationshipType());
//			List<MemberRelationship> relationships = RelationshipUtils.buildRelationships(newMemberFromDb, relationshipType, relatedMember, getAuditInfo());
//			updateRelationships(relationships);
//		}
		
		AddFamilyMemberResponse response = AddFamilyMemberResponse.builder().firstName(opRequest.getFirstName()).lastName(family.getFamilyName()).memberId(newMemberFromDb.getMemberId()).build();
		response.setOperationMessage(String.format("Member %s %s added successfully !!!", opRequest.getFirstName(), family.getFamilyName()));
		return response;
	}
	
	private Address getAddressFromRequest (AddFamilyMemberRequest opRequest) {
		AddressDto inputAddress = opRequest.getMemberAddress();
		if(inputAddress ==null) {
			log.info("Member address is not provided, not creating address for member");
			return null;
		}
		
		AuditInfo auditInfo = getAuditInfo();
		return Address.builder()
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
	
	private FamilyMember getFamilyMemberFromRequest (AddFamilyMemberRequest opRequest, Family family, int addressId) {
		AuditInfo auditInfo = getAuditInfo();
		String phoneNumber = DataFormatter.formatPhoneNumber(opRequest.getPhone());
		return FamilyMember.builder()
				.familyId(family.getFamilyId())
				.headOfFamily(opRequest.isHeadOfFamily())
				.firstName(opRequest.getFirstName())
				.lastName(family.getFamilyName())
				.maidenLastName(opRequest.getMaidenLastName())
				.nickName(opRequest.getNickName())
				.addressSameAsFamily(opRequest.isAddressSameAsFamily())
				.memberAddressId(addressId)
				.phone(phoneNumber)
				.isPhoneWhatsappRegistered(opRequest.isPhoneWhatsappRegistered())
				.email(opRequest.getEmail())
				.linkedinUrl(opRequest.getLinkedinUrl())
				.gender(Gender.getGenderByString(opRequest.getGender()))
				.birthDay(opRequest.getBirthDay())
				.birthMonth(Month.fromName(opRequest.getBirthMonth()))
				.birthYear(opRequest.getBirthYear())
				.maritalStatus(MaritalStatus.getMaritalStatus(opRequest.getMaritalStatus()))
				.educationDetails(opRequest.getEducationDetails())
				.occupation(opRequest.getOccupation())
				.workingAt(opRequest.getWorkingAt())
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
	
	private void updateRelationships (List<MemberRelationship> relationships) {
		log.info("Adding relationship");
		for(MemberRelationship relationship : relationships) {
			MemberRelationship memberRelationshipFromDb = memberRelationshipDao.getRelationshipBetweenMembers(relationship.getMemberId(), relationship.getRelatedMemberId());
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
		if(gender == Gender.Female) {
			return Constants.DEFALT_FEMALE_MEMBER_IMAGE;
		}
		return Constants.DEFALT_MALE_MEMBER_IMAGE;
	}
	
	private static String getDefaultThumbnailImagePath (AddFamilyMemberRequest opRequest) {
		Gender gender = Gender.getGenderByString(opRequest.getGender());
		if(gender == Gender.Female) {
			return Constants.DEFALT_FEMALE_MEMBER_IMAGE;
		}
		return Constants.DEFALT_MALE_MEMBER_IMAGE;
	}

}
