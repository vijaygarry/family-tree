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
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.FamilyMemberDto;
import com.neasaa.familytree.operation.family.model.GetMemberProfileRequest;
import com.neasaa.familytree.operation.family.model.GetMemberProfileResponse;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        FamilyMember memberEntity = null;
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
        FamilyMemberDto familyTreeRoot = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(memberEntity);

        buildFamilyTreeStructure(familyTreeRoot, 0);

        GetMemberProfileResponse.MemberProfile memberProfile = GetMemberProfileResponse.MemberProfile.fromFamilyMemberDBEntity(memberEntity, getAddress(memberEntity));

        return GetMemberProfileResponse.builder()
                .memberProfile(memberProfile)
                .familyTreeRoot(familyTreeRoot)
                .build();
    }

    private void buildFamilyTreeStructure(FamilyMemberDto treeNode, int numberOfLevels) {
        log.info("Adding details for {} with number of levels in family tree: {}", treeNode.getFirstName(), numberOfLevels);
        if(numberOfLevels > 2) {
            log.info("Not adding family details for member {} as the family tree depth limit is reached.", treeNode.getFirstName());
            return; // Limit the depth of the family tree to 2 levels
        }
        int spouseMemberId = -1;
        //Set the spouse only for selected member. Should not set spouse for children. i.e. if numberOfLevels is 0.
        if(numberOfLevels ==0 && treeNode.getSpouse() == null) {
            MemberRelationship spouseForMember = memberRelationshipDao.getSpouseForMemberById(treeNode.getMemberId());
            if(spouseForMember != null) {
                FamilyMember spouse = familyMemberDao.getMemberById(spouseForMember.getRelatedMemberId());
                spouseMemberId = spouseForMember.getRelatedMemberId();
                if (spouse != null) {
                    treeNode.setSpouse(FamilyMemberDto.getFamilyMemberDtoFromDBEntity(spouse));
                }
            }
        }

        ++numberOfLevels;
        List<MemberRelationship> childrenForMember = memberRelationshipDao.getChildrenForMemberById(treeNode.getMemberId(), spouseMemberId);
        if (childrenForMember != null) {
            for (MemberRelationship childRelation : childrenForMember) {
                FamilyMember child = familyMemberDao.getMemberById(childRelation.getRelatedMemberId());
                if (child != null) {
                    FamilyMemberDto childDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(child);
                    treeNode.addChild(childDto);
                    if(childDto.getFamilyId() == treeNode.getFamilyId()) {
                        log.info("Member " + childDto.getFirstName() + " is part of the same family as " + treeNode.getFamilyId());
                        buildFamilyTreeStructure(childDto, numberOfLevels);
                    } else {
                        log.info("Member {} with family id {} is not part of the same family as {}", childDto.getFirstName(), childDto.getFamilyId(), treeNode.getFamilyId());
                    }
                }
            }
        }
    }

//    private FamilyMemberDto getRelationships(FamilyMember memberEntity) {
//        FamilyMemberDto memberDto = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(memberEntity);
//        FamilyMemberDto familyTreeRoot = memberDto;
//
//        //Get all the relationships of the member.
//        List<MemberRelationship> memberDirectRelationships = memberRelationshipDao.getRelationshipByMemberIdOrRelatedMemberId(memberEntity.getMemberId());
//        memberDto.setSpouse(getSpouseDtoForMember(memberEntity, memberDirectRelationships));
//        memberDto.setChildren(getChildrenDtoForMember(memberDto, memberDirectRelationships));
//
//        return memberDto;
//    }
//
//    private FamilyMemberDto getSpouseDtoForMember(FamilyMember memberEntity, List<MemberRelationship> memberDirectRelationships) {
//
//        if (memberDirectRelationships == null || memberDirectRelationships.isEmpty()) {
//            return null;
//        }
//        int spouseMemberId = -1;
//        for (MemberRelationship relationship : memberDirectRelationships) {
//            if (relationship.getRelationshipType() == RelationshipType.Wife || relationship.getRelationshipType() == RelationshipType.Husband) {
//                if (relationship.getMemberId() == memberEntity.getMemberId()) {
//                    spouseMemberId = relationship.getRelatedMemberId();
//                } else {
//                    spouseMemberId = relationship.getMemberId();
//                }
//                FamilyMember spouseMemberEntity = familyMemberDao.getMemberById(spouseMemberId);
//                return FamilyMemberDto.getFamilyMemberDtoFromDBEntity(spouseMemberEntity);
//            }
//        }
//        return null;
//    }
//
//    private List<FamilyMemberDto> getChildrenDtoForMember(FamilyMemberDto memberDto, List<MemberRelationship> memberDirectRelationships) {
//        List<FamilyMemberDto> children = null;
//        List<Integer> memberIds = List.of(memberDto.getMemberId());
//        if(memberDto.getSpouse() != null) {
//            memberIds = List.of(memberDto.getMemberId(), memberDto.getSpouse().getMemberId());
//        }
//        List<MemberRelationship> childrenRelationship = memberRelationshipDao.getRelatedMembersByIdAndRelationType(memberIds, List.of(RelationshipType.Son, RelationshipType.Daughter));
//        if(childrenRelationship == null || childrenRelationship.isEmpty()) {
//            return null;
//        }
//        for (MemberRelationship relationship : childrenRelationship) {
//            FamilyMember childMemberEntity = familyMemberDao.getMemberById(relationship.getRelatedMemberId());
//            if (childMemberEntity != null) {
//                if (children == null) {
//                    children = new ArrayList<>();
//                } else if (children.stream().anyMatch(child -> child.getMemberId() == childMemberEntity.getMemberId())) {
//                    continue; // Skip if already added
//                }
//                children.add(FamilyMemberDto.getFamilyMemberDtoFromDBEntity(childMemberEntity));
//            }
//        }
//        return children;
//    }

//    private List<FamilyMemberDto> getParents(FamilyMemberDto familyMemberDto) {
//        FamilyMemberDto father = null;
//        FamilyMemberDto mother = null;
//
//        List<MemberRelationship> parents = memberRelationshipDao.getRelationByRelatedIdAndRelationType(familyMemberDto.getMemberId(), List.of(RelationshipType.Son, RelationshipType.Daughter));
//        if (parents == null || parents.isEmpty()) {
//            return null;
//        } else if (parents.size() == 1) {
//            FamilyMember parent1MemberEntity = familyMemberDao.getMemberById(parents.get(0).getMemberId());
//            if(parent1MemberEntity.getGender() == Gender.Male) {
//                father = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(parent1MemberEntity);
//            } else {
//                mother = FamilyMemberDto.getFamilyMemberDtoFromDBEntity(parent1MemberEntity);
//            }
//
//
//            List<MemberRelationship> parentsSpouse = memberRelationshipDao.getRelatedMembersByIdAndRelationType(List.of(parents.get(0).getMemberId()), List.of(RelationshipType.Wife, RelationshipType.Husband));
//            if (parentsSpouse != null && !parentsSpouse.isEmpty()) {
//
//                return FamilyMemberDto.getFamilyMemberDtoFromDBEntity(familyMemberDao.getMemberById(parents.get(0).getRelatedMemberId()));
//            }
//            parents.add(parentsSpouse);
//        }
//        FamilyMember childMemberEntity = familyMemberDao.getMemberById(relationship.getRelatedMemberId());
//
//    }

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
