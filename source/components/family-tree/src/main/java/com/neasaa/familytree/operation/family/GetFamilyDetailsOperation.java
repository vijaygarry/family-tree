package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.AddressDao;
import com.neasaa.familytree.dao.pg.FamilyDao;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.Family;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.entity.MemberRelationship;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.FamilyMemberDto;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("GetFamilyDetailsOperation")
@Scope("prototype")
public class GetFamilyDetailsOperation extends AbstractOperation<GetFamilyDetailsRequest, GetFamilyDetailsResponse> {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private FamilyDao familyDao;

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Autowired
    private MemberRelationshipDao memberRelationshipDao;

    @Override
    public String getOperationName() {
        return OperationNames.GET_FAMILY_DETAILS;
    }

    @Override
    public void doValidate(GetFamilyDetailsRequest opRequest) throws OperationException {
        if (opRequest != null && opRequest.getFamilyId() != null) {
            if(opRequest.getFamilyId() <  1 ){
                throw new ValidationException("Invalid family id provided.");
            }
        }
    }

    @Override
    public GetFamilyDetailsResponse doExecute(GetFamilyDetailsRequest opRequest) throws OperationException {
        // Fetch family details using family id.
        Family familyDetailsFromDB = familyDao.getFamilyByFamilyId(opRequest.getFamilyId());
        // If family not found, throw ValidationException.
        if (familyDetailsFromDB == null) {
            throw new ValidationException("Family not found for the provided family id " + opRequest.getFamilyId());
        }
        GetFamilyDetailsResponse familyDetailsResponse = new GetFamilyDetailsResponse();
        setFamilyDetails(familyDetailsResponse, familyDetailsFromDB);

        // Fetch all the members of the family.
        List<FamilyMember> familyMembers = familyMemberDao.allMembersForFamily(opRequest.getFamilyId());
        FamilyMember headOfFamily = getHeadOfFamily(familyMembers);
        // If head of family not found, throw ValidationException.
        if (headOfFamily != null) {
            FamilyMemberDto familyTreeRoot = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(headOfFamily);
            familyDetailsResponse.setFamilyTreeRoot(familyTreeRoot);
            Map<Integer, FamilyMember> familyMemberMap =  familyMembers.stream().collect(Collectors.toMap(FamilyMember::getMemberId, member -> member));
            buildFamilyTreeStructure(familyTreeRoot, familyMemberMap);
        }

        // Fetch the relationships of the family members and build the family tree structure.
        return familyDetailsResponse;
    }

    private void setFamilyDetails(GetFamilyDetailsResponse familyDetailsResponse, Family familyDetailsFromDB) {
        familyDetailsResponse.setFamilyId(familyDetailsFromDB.getFamilyId());
        familyDetailsResponse.setFamilyName(familyDetailsFromDB.getFamilyName());
        familyDetailsResponse.setGotra(familyDetailsFromDB.getGotra());
        familyDetailsResponse.setFamilyAddress(AddressDto.getAddressDtoFromEntity(addressDao.getAddressById(familyDetailsFromDB.getAddressId())));
        familyDetailsResponse.setPhone(familyDetailsFromDB.getPhone());
        familyDetailsResponse.setEmail(familyDetailsFromDB.getEmail());
        familyDetailsResponse.setFamilyImage(familyDetailsFromDB.getFamilyImage());
    }

    private FamilyMember getHeadOfFamily(List<FamilyMember> familyMembers) {
        for (FamilyMember member : familyMembers) {
            if (member.isHeadOfFamily()) {
                return member;
            }
        }
        return null;
    }

    private void buildFamilyTreeStructure(FamilyMemberDto treeNode, Map<Integer, FamilyMember> familyMemberMap) {
        List<MemberRelationship> relationshipsForMember = memberRelationshipDao.getRelationshipsForMember(treeNode.getMemberId());
        for (MemberRelationship relationship : relationshipsForMember) {
            FamilyMember relatedMember = familyMemberMap.get(relationship.getRelatedMemberId());
            if (relatedMember == null) {
                continue;
            }
            FamilyMemberDto relatedMemberDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(relatedMember);
            switch (relationship.getRelationshipType()) {
                case Wife:
                case Husband:
                    treeNode.setSpouse(relatedMemberDto);
                    break;
                case Son:
                case Daughter:
                    treeNode.addChild(relatedMemberDto);
                    break;
                default:
                    throw new RuntimeException("Unexpected relationship type: " + relationship.getRelationshipType());
            }
        }

        if(treeNode.getChildren() != null && !treeNode.getChildren().isEmpty()) {
            for (FamilyMemberDto child : treeNode.getChildren()) {
                buildFamilyTreeStructure(child, familyMemberMap);
            }
        }
    }
}
