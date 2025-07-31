package com.neasaa.familytree.uploader;

import com.neasaa.base.app.utils.AssertionUtils;
import com.neasaa.excel.ExcelSheet;
import com.neasaa.excel.ExcelWorkBook;
import com.neasaa.familytree.operation.model.AddFamilyRequest;
import com.neasaa.familytree.operation.model.ExcelFamilyMemberDetails;
import com.neasaa.familytree.operation.model.InputRelationship;
import com.neasaa.util.FileUtils;
import com.neasaa.util.OperationUtils;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.neasaa.util.OperationUtils.logoutUser;

@Log4j2
public class UploadFamily {

    public static void main(String[] args) throws Exception {
        String excelFilepath = "/Users/vijaygarothaya/work/product/family-tree/Documents/GarothayaFamily-Bhagwatnarayan.xlsx";
        ExcelFamily excelFamily = new ExcelFamily(excelFilepath);
        excelFamily.loadDataFromExcel();
        excelFamily.printFamilyTree();

        String sessionId = OperationUtils.loginWithSuperUserAndGetJSessionId();
        AddFamilyRequest familyDetails = excelFamily.getFamilyDetails();
        if(familyDetails.getFamilyId() == 0) {
            int familyId = addFamilyToApplication(familyDetails, sessionId);
            familyDetails.setFamilyId(familyId);
            excelFamily.updateFamilyId(familyId);
        }
        MemberTreeNode root = excelFamily.getRoot();
        addMemberNodesToApplication(root, familyDetails, sessionId, excelFamily);



//        familyMembersSheet = workbook.getExcelSheetByName(FAMILY_MEMBER_SHEET_NAME);
//        for (int i=0; i < familyMembers.size(); i++) {
//            ExcelFamilyMemberDetails familyMember = familyMembers.get(i);
//            familyMember.setFamilyId(familyDetails.getFamilyId());
//            if( familyMember.getMemberId() == 0) {
//                log.info("Family member {} is skipped.", familyMember.getFirstName());
//                continue;
//            }
//            if (familyMember.getMemberId() > 0) {
//                log.info("Family member {} already exists, skipping add operation.", familyMember.getFirstName());
//                continue;
//            }
//            log.info("Adding family member: {}", familyMember.getFirstName());
//            addFamilyMemberAndUpdateMemberIdInExcel(
//                    familyMembersSheet, familyMember, sessionId, familyDetails, familyMembers.indexOf(familyMember)
//            );
//            workbook.save();
//            workbook = getExcelWorkbook(excelFilepath);
//            familyMembersSheet = workbook.getExcelSheetByName(FAMILY_MEMBER_SHEET_NAME);
//        }
        logoutUser(sessionId);
    }


    private static int addFamilyToApplication (AddFamilyRequest familyDetails, String sessionId) throws Exception {
        if(familyDetails.getFamilyId() > 0) {
            log.info("Family {} is already present in the excel, skipping add operation.", familyDetails.getFamilyName());
            return familyDetails.getFamilyId();
        }
        Response response = OperationUtils.addFamily(familyDetails, sessionId);
        AssertionUtils.assertResponse(response, 200, familyDetails.getFamilyName() + " family added successfully !!!");
        String familyId = response.jsonPath().getString("familyId");
        log.info("Family {} is added successfully with familyIdd {}", familyDetails.getFamilyName(), familyId);
        return Integer.parseInt(familyId);
    }

    private static void addMemberNodesToApplication (MemberTreeNode memberNode, AddFamilyRequest familyDetails, String sessionId, ExcelFamily excelFamily) throws Exception {
        if(memberNode == null || memberNode.getMember() == null) {
            log.info("Member node is null or member is not specified, skipping add operation.");
            return;
        }
        if(memberNode.isMemberAddedToApplication()) {
            log.info("Member {} is already added to the application, skipping add operation.", memberNode.getMember().getFirstName());
            return;
        }

        ExcelFamilyMemberDetails currentMemberDetails = memberNode.getMember();
        log.info("Adding family member: {}", currentMemberDetails.getFirstName());
        addFamilyMemberAndUpdateMemberIdInExcel(memberNode, familyDetails, sessionId, excelFamily);

        MemberTreeNode fatherNode = memberNode.getFather();
        if (fatherNode != null) {
            String relationshipType = "Son";
            if(currentMemberDetails.getGender().equalsIgnoreCase("Female")) {
                relationshipType = "Daughter";
            }
            InputRelationship relationship = InputRelationship.builder().relationshipType(relationshipType).relatedMemberId(currentMemberDetails.getMemberId()).relatedMemberFullName(currentMemberDetails.getFirstName())
                    .build();
            fatherNode.getMember().setRelashinship(relationship);
            addFamilyMemberAndUpdateMemberIdInExcel(fatherNode, familyDetails, sessionId, excelFamily);
        }
        MemberTreeNode motherNode = memberNode.getMother();
        if (motherNode != null) {
            InputRelationship relationship = null;
            if( fatherNode != null ) {
                relationship = InputRelationship.builder().relationshipType("Husband").relatedMemberId(fatherNode.getMember().getMemberId()).relatedMemberFullName(fatherNode.getMember().getFirstName())
                        .build();
            } else {
                String relationshipType = "Son";
                if(currentMemberDetails.getGender().equalsIgnoreCase("Female")) {
                    relationshipType = "Daughter";
                }
                relationship = InputRelationship.builder().relationshipType(relationshipType).relatedMemberId(currentMemberDetails.getMemberId()).relatedMemberFullName(currentMemberDetails.getFirstName())
                        .build();
            }
            motherNode.getMember().setRelashinship(relationship);
            addFamilyMemberAndUpdateMemberIdInExcel(motherNode, familyDetails, sessionId, excelFamily);
        }

        MemberTreeNode spouseNode = memberNode.getSpouse();
        if (spouseNode != null) {
            String relationshipType = "Husband";
            if(currentMemberDetails.getGender().equalsIgnoreCase("Female")) {
                relationshipType = "Wife";
            }
            InputRelationship relationship = InputRelationship.builder().relationshipType(relationshipType).relatedMemberId(currentMemberDetails.getMemberId()).relatedMemberFullName(currentMemberDetails.getFirstName())
                    .build();
            spouseNode.getMember().setRelashinship(relationship);
            addFamilyMemberAndUpdateMemberIdInExcel(spouseNode, familyDetails, sessionId, excelFamily);
        }

        List<MemberTreeNode> children = memberNode.getChildren();
        if (children != null && !children.isEmpty()) {
            for (MemberTreeNode child : children) {
                String relationshipType = "Father";
                if(currentMemberDetails.getGender().equalsIgnoreCase("Female")) {
                    relationshipType = "Mother";
                }
                InputRelationship relationship = InputRelationship.builder().relationshipType(relationshipType).relatedMemberId(currentMemberDetails.getMemberId()).relatedMemberFullName(currentMemberDetails.getFirstName())
                        .build();
                child.getMember().setRelashinship(relationship);
                addFamilyMemberAndUpdateMemberIdInExcel(child, familyDetails, sessionId, excelFamily);
            }
        }
        // Member wth dependents is added to application
        memberNode.setMemberAddedToApplication(true);
        addMemberNodesToApplication(fatherNode, familyDetails, sessionId, excelFamily);
        addMemberNodesToApplication(motherNode, familyDetails, sessionId, excelFamily);
        addMemberNodesToApplication(spouseNode, familyDetails, sessionId, excelFamily);
        if (children != null && !children.isEmpty()) {
            for (MemberTreeNode child : children) {
                addMemberNodesToApplication(child, familyDetails, sessionId, excelFamily);
            }
        }
    }

    private static void addFamilyMemberAndUpdateMemberIdInExcel(
            MemberTreeNode memberNode, AddFamilyRequest familyDetails, String sessionId, ExcelFamily excelFamily) throws Exception {
        ExcelFamilyMemberDetails familyMemberDetails = memberNode.getMember();
        if (familyMemberDetails == null) {
            log.info("Family member is not specify, skipping add operation.");
            return;
        }
        if (familyMemberDetails.getMemberId() > 0) {
            log.info("Family member {} is already present in the application with id {} , skipping add operation.", familyMemberDetails.getFirstName(), familyMemberDetails.getMemberId());
            return;
        }
        if(memberNode.getMember().getFamilyId() < 0){
            log.info("Member belongs to different family and family is unknown for member {}, skipping add operation.", memberNode.getMember().getFirstName());
            return;
        }
        if(memberNode.getMember().getFamilyId() == 0) {
            memberNode.getMember().setFamilyId(familyDetails.getFamilyId());
        }
        if(familyMemberDetails.getRelashinship() != null ) {
            log.info("Sending relationship as {} is {} of {}", familyMemberDetails.getFirstName(), familyMemberDetails.getRelashinship().getRelationshipType(), familyMemberDetails.getRelashinship().getRelatedMemberFullName());
        }

        Response response = OperationUtils.addFamilyMember(familyMemberDetails.toAddFamilyMemberRequest(), sessionId);
        AssertionUtils.assertResponse(response, 200, "Member " + familyMemberDetails.getFirstName() + " " + familyMemberDetails.getLastName() + " added successfully !!!");
        int familyMemberId = response.jsonPath().getInt("memberId");
        log.info("Family member {} is added successfully with member id {}.", familyMemberDetails.getFirstName(), familyMemberId);
        familyMemberDetails.setMemberId(familyMemberId);
        excelFamily.updateMemberId(memberNode);
    }


}
