package com.neasaa.familytree.operation.family;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.AuditInfo;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.dao.pg.FamilyMemberDao;
import com.neasaa.familytree.dao.pg.MemberRelationshipDao;
import com.neasaa.familytree.entity.FamilyMember;
import com.neasaa.familytree.entity.MemberRelationship;
import com.neasaa.familytree.enums.RelationshipType;
import com.neasaa.familytree.operation.family.model.ManageRelationshipRequest;
import com.neasaa.familytree.operation.family.model.ManageRelationshipResponse;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import com.neasaa.familytree.utils.RelationshipUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.neasaa.familytree.operation.OperationNames.MANAGE_RELATIONSHIP;

@Log4j2
@Component("ManageRelationshipOperation")
@Scope("prototype")
public class ManageRelationshipOperation extends AbstractOperation<ManageRelationshipRequest, ManageRelationshipResponse> {

    @Autowired
    private FamilyMemberDao familyMemberDao;

    @Autowired
    private MemberRelationshipDao memberRelationshipDao;

    @Override
    public String getOperationName() {
        return MANAGE_RELATIONSHIP;
    }

    @Override
    public void doValidate(ManageRelationshipRequest opRequest) throws OperationException {
        if((opRequest.getToAdd() == null || opRequest.getToAdd().isEmpty()) && (opRequest.getToRemove() == null || opRequest.getToRemove().isEmpty())) {
            throw new ValidationException("No relationships provided to add or remove.");
        }
        if(opRequest.getToAdd() != null && !opRequest.getToAdd().isEmpty()) {
            opRequest.getToAdd().forEach(this::validateRelationship);
        }

        if(opRequest.getToRemove() != null && !opRequest.getToRemove().isEmpty()) {
            opRequest.getToRemove().forEach(this::validateRelationship);
        }
    }

    private void validateRelationship(RelationshipDto relationship) {
        if(relationship == null) {
            throw new ValidationException("Relationship cannot be null.");
        }
        if(relationship.getMemberId() <= 0) {
            throw new ValidationException("Invalid member ID " + relationship.getMemberId() + " provided.");
        }
        if(relationship.getRelatedMemberId() <= 0) {
            throw new ValidationException("Invalid related member ID " + relationship.getRelatedMemberId() + " provided.");
        }
        if(RelationshipType.getRelationshipType(relationship.getRelationshipType() ) == null) {
            throw new ValidationException("Invalid relationship type " + relationship.getRelationshipType() + " provided");
        }
        if(relationship.getMemberName() == null || relationship.getRelatedMemberName() == null) {
            throw new ValidationException("Member name and related member name should be provided.");
        }
    }

    @Override
    public ManageRelationshipResponse doExecute(ManageRelationshipRequest opRequest) throws OperationException {
        ManageRelationshipResponse response = new ManageRelationshipResponse();
        response.setRemovedResult(new ArrayList<>());
        response.setAddedResult(new ArrayList<>());

        // First process relationships to delete
        if(opRequest.getToRemove() != null && !opRequest.getToRemove().isEmpty()) {
            opRequest.getToRemove().forEach(relationshipDto -> {
                removeRelationship(relationshipDto);
                response.getRemovedResult().add("Relationship between member " + relationshipDto.getMemberName() +
                        " and related member name " + relationshipDto.getRelatedMemberName() + " removed successfully.");
            });
        }

        // process relationships to Add
        if(opRequest.getToAdd() != null && !opRequest.getToAdd().isEmpty()) {
            opRequest.getToAdd().forEach(relationshipDto -> {
                addRelationship(relationshipDto);
                response.getAddedResult().add("Relationship between member " + relationshipDto.getMemberName() +
                        " and related member name " + relationshipDto.getRelatedMemberName() + " added successfully.");
            });
        }
        return response;
    }


    private void removeRelationship(RelationshipDto relationship) {
        // Fetch both the members and make sure both members exist in the database
        FamilyMember member = getAndValidateMember(relationship.getMemberId(), relationship.getMemberName());
        getAndValidateMember(relationship.getRelatedMemberId(), relationship.getRelatedMemberName());

        // Normalize the relationship type e.g. Father to Son or Husband to Wife
        relationship = RelationshipUtils.normalizeRelationship(relationship, member);
        RelationshipType relationshipType = RelationshipType.getRelationshipType(relationship.getRelationshipType());
        MemberRelationship relationshipBetweenMembers = memberRelationshipDao.getRelationshipBetweenMembers(relationship.getMemberId(), relationship.getRelatedMemberId());
        if (relationshipBetweenMembers == null) {
            log.info("No relationship found between member ID {} and related member ID {}. Nothing to remove.", relationship.getMemberId(), relationship.getRelatedMemberId());
            return;
        }
        if(relationshipBetweenMembers.getRelationshipType() == relationshipType) {
            log.info("Removing relationship between member ID {} and related member ID {} with relationship type {}",
                    relationship.getMemberId(), relationship.getRelatedMemberId(), relationshipType);
            try {
                memberRelationshipDao.deleteMemberRelationship(relationshipBetweenMembers);
            } catch (SQLException e) {
                throw new RuntimeException("Internal error while removing relationship between member ID " + relationship.getMemberId() +
                        " and related member ID " + relationship.getRelatedMemberId());
            }
        } else {
            log.warn("Relationship type mismatch. Expected: {}, Found: {}. Not removing relationship.",
                    relationshipType, relationshipBetweenMembers.getRelationshipType());
            throw new ValidationException("Relationship type mismatch. Cannot remove relationship.");
        }
    }

    private void addRelationship(RelationshipDto relationship) {
        // Fetch both the members and make sure both members exist in the database
        FamilyMember member = getAndValidateMember(relationship.getMemberId(), relationship.getMemberName());
        getAndValidateMember(relationship.getRelatedMemberId(), relationship.getRelatedMemberName());

        // Normalize the relationship type e.g. Father to Son or Husband to Wife
        relationship = RelationshipUtils.normalizeRelationship(relationship, member);
        RelationshipType relationshipType = RelationshipType.getRelationshipType(relationship.getRelationshipType());
        MemberRelationship relationshipBetweenMembers = memberRelationshipDao.getRelationshipBetweenMembers(relationship.getMemberId(), relationship.getRelatedMemberId());
        if (relationshipBetweenMembers == null) {
            AuditInfo auditInfo = getAuditInfo();
            MemberRelationship memberRelationship = MemberRelationship.builder()
                    .memberId(relationship.getMemberId())
                    .relationshipType(relationshipType)
                    .relatedMemberId(relationship.getRelatedMemberId())
                    .createdBy(auditInfo.getCreatedBy())
                    .createdDate(auditInfo.getCreatedDate())
                    .lastUpdatedBy(auditInfo.getLastUpdatedBy())
                    .lastUpdatedDate(auditInfo.getLastUpdatedDate())
                    .build();

            memberRelationshipDao.addMemberRelationship(memberRelationship);
            log.info("Added relationship between member ID {} and related member ID {} with relationship type {}",
                    relationship.getMemberId(), relationship.getRelatedMemberId(), relationshipType);
        } else {
            log.info("Relationship already exists between member ID {} and related member ID {} with relationship type {}",
                    relationship.getMemberId(), relationship.getRelatedMemberId(), relationshipType);
        }
    }

    private FamilyMember getAndValidateMember(int memberId, String memberName) {
        FamilyMember familyMember = familyMemberDao.getMemberById(memberId);
        if (familyMember == null) {
            throw new ValidationException("Member not found for the member ID " + memberId);
        }
        if (!familyMember.getFirstName().equalsIgnoreCase(memberName)) {
            throw new ValidationException("Member name " + memberName + " does not match with the provided member name " + memberName);
        }

        return familyMember;
    }

}
