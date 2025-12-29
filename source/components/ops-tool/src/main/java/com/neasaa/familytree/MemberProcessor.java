package com.neasaa.familytree;

import com.neasaa.base.app.utils.EmailValidator;
import com.neasaa.familytree.dto.ExcelFamilyMember;
import com.neasaa.familytree.dto.FamilyTreeNode;
import com.neasaa.familytree.enums.Gender;
import com.neasaa.familytree.enums.MaritalStatus;
import com.neasaa.familytree.excel.FamilyExcelUtil;
import com.neasaa.familytree.operation.family.model.AddFamilyMemberRequest;
import com.neasaa.familytree.operation.family.model.RelationshipDto;
import com.neasaa.familytree.utils.FamilytreeValidationUtils;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.neasaa.familytree.utils.FamilytreeValidationUtils.validateStringLength;

@Log4j2
public class MemberProcessor {
    private HttpUtils httpUtils;
    public void processMemberData(String excelFilepath) throws Exception {
        FamilyExcelUtil excelUtil = new FamilyExcelUtil(excelFilepath);
        List<ExcelFamilyMember> familyMembers = excelUtil.getFamilyMembers();
        validateMemberDetails(familyMembers);
        // Map where key = familyId and value = list of family members in that family
        Map<Integer, List<ExcelFamilyMember>> familyMemberMap = getFamilyMemberMap(familyMembers);
        httpUtils = new HttpUtils();
        httpUtils.createSession();
        httpUtils.whoAmI();
        try {
            for (Map.Entry<Integer, List<ExcelFamilyMember>> entry : familyMemberMap.entrySet()) {
                FamilyTreeNode headOfFamilyNode = buildFamilyTree(entry.getValue());
                processMemberNode(headOfFamilyNode, null, excelUtil);
            }
        } finally {
            httpUtils.logout();
        }
        System.out.println("Processing member data...");
    }

    private Map<Integer, List<ExcelFamilyMember>> getFamilyMemberMap(List<ExcelFamilyMember> familyMembers) {
        Map<Integer, List<ExcelFamilyMember>> familyMemberMap = familyMembers.stream()
                .collect(Collectors.groupingBy(ExcelFamilyMember::getFamilyId));
        return familyMemberMap;
    }
    private FamilyTreeNode buildFamilyTree(List<ExcelFamilyMember> familyMembers) throws Exception {
        //Assuming all the members belong to a single family tree for simplicity
        FamilyTreeNode headOfFamily = null;
        Map<String, FamilyTreeNode> nameToNodeMap = new HashMap<>();
        for(ExcelFamilyMember familyMember : familyMembers) {
            FamilyTreeNode node = nameToNodeMap.get(familyMember.getFirstName());
            if(node == null) {
                node = new FamilyTreeNode(familyMember);
            } else {
                throw new Exception ("Duplicate member name " + familyMember.getFirstName() + " found in family ID "+ familyMember.getFamilyId());
            }
            nameToNodeMap.put(familyMember.getFirstName(), node);
            if(familyMember.isHeadOfFamily()) {
                if(headOfFamily != null) {
                    throw new Exception ("Multiple heads of family found in family ID "+ familyMember.getFamilyId());
                }
                headOfFamily = node;
            }
        }
        if (headOfFamily == null) {
            throw new Exception("Head of family not found in family ID " + familyMembers.get(0).getFamilyId());
        }
       //Set spouse, children and parents for each node
        for(Map.Entry<String, FamilyTreeNode> entry : nameToNodeMap.entrySet()) {
            FamilyTreeNode currentNode = entry.getValue();
            if(currentNode.isAddedToTree()) {
                continue;
            }
            ExcelFamilyMember member = currentNode.getFamilyMember();
            // Get spouse node for current member
            FamilyTreeNode spouseNode = getSpouseForMember(currentNode, nameToNodeMap);
            // Get children nodes for current member and spouse
            List<FamilyTreeNode> kidsForCouple = getKidsForCouple(currentNode, spouseNode, nameToNodeMap);

            // Link spouse and children
            linkFamilyMembers(currentNode, spouseNode, kidsForCouple);
        }

        // return head of family
        return headOfFamily;
    }

    private FamilyTreeNode getSpouseForMember(FamilyTreeNode memberNode, Map<String, FamilyTreeNode> nameToNodeMap) throws Exception {
        ExcelFamilyMember familyMember = memberNode.getFamilyMember();
        String spouseName = familyMember.getSpouseName();
        if (spouseName == null || spouseName.isEmpty()) {
            return null;
        }

        FamilyTreeNode spouseNode = nameToNodeMap.get(spouseName);
        if (spouseNode == null) {
            throw new Exception("Spouse " + spouseName + " not found for member " + familyMember.getFirstName());
        }
        if (familyMember.getMaritalStatus().equalsIgnoreCase("Single")) {
            throw new Exception("Member " + familyMember.getFirstName() + "'s marital status is define as Single and spouse name " + spouseName + " is provided.");
        }
        ExcelFamilyMember spouseMember = spouseNode.getFamilyMember();
        if (spouseMember.getMaritalStatus().equalsIgnoreCase("Single")) {
            throw new Exception("Member " + spouseMember.getFirstName() + " is set as " + familyMember.getFirstName() + "'s spouse and marital status for " + spouseMember.getFirstName() + " is Single.");
        }

        if(familyMember.getGender().equalsIgnoreCase(spouseMember.getGender())) {
            throw new Exception("Member " + familyMember.getFirstName() + " and spouse " + spouseName + " have same gender " + familyMember.getGender() + ".");
        }
        return spouseNode;
    }

    private List<FamilyTreeNode> getKidsForCouple(FamilyTreeNode memberNode, FamilyTreeNode spouseNode, Map<String, FamilyTreeNode> nameToNodeMap) throws Exception {
        ExcelFamilyMember familyMember = memberNode.getFamilyMember();
        List<String> memberChildrenNamesList = familyMember.getChildrenNamesList();
        List<String> spouseChildrenNames = null;
        if(spouseNode != null) {
            spouseChildrenNames = spouseNode.getFamilyMember().getChildrenNamesList();
        }
        if((memberChildrenNamesList == null || memberChildrenNamesList.isEmpty()) && (spouseChildrenNames == null || spouseChildrenNames.isEmpty())) {
            return null;
        }
        List<FamilyTreeNode> childrenNodes = new ArrayList<>();
        Set<String> childrenNamesAdded = new HashSet<>();
        if(memberChildrenNamesList != null) {
            for (String childName : memberChildrenNamesList) {
                if(childrenNamesAdded.contains(childName)) {
                    continue;
                }
                childrenNamesAdded.add(childName);
                FamilyTreeNode childNode = nameToNodeMap.get(childName);
                if (childNode != null) {
                    //TODO: Validate child age should not be greater than any of the parents
                    childrenNodes.add(childNode);
                } else {
                    throw new Exception("Child " + childName + " not found for member " + familyMember.getFirstName());
                }
            }
        }

        if(spouseChildrenNames != null) {
            for (String childName : spouseChildrenNames) {
                if(childrenNamesAdded.contains(childName)) {
                    continue;
                }
                childrenNamesAdded.add(childName);
                FamilyTreeNode childNode = nameToNodeMap.get(childName);
                if (childNode != null) {
                    //TODO: Validate child age should not be greater than any of the parents
                    childrenNodes.add(childNode);
                } else {
                    throw new Exception("Child " + childName + " not found for member " + familyMember.getFirstName());
                }
            }
        }
        return childrenNodes;
    }

    private void linkFamilyMembers(FamilyTreeNode memberNode, FamilyTreeNode spouseNode, List<FamilyTreeNode> kidsForCouple) throws Exception {
        // is spouseNode null
        if(spouseNode == null) {
            memberNode.setChildren(kidsForCouple);
            memberNode.setAddedToTree(true);
            return;
        }
        // Find husband in couple and add chidren to him
        FamilyTreeNode husbandNode = null;
        FamilyTreeNode wifeNode = null;
        if(memberNode.getFamilyMember().getGender().equalsIgnoreCase("Male")) {
            husbandNode = memberNode;
            wifeNode = spouseNode;
        } else {
            husbandNode = spouseNode;
            wifeNode = memberNode;
        }
        husbandNode.setChildren(kidsForCouple);
        husbandNode.setSpouse(wifeNode);
        husbandNode.setAddedToTree(true);
        wifeNode.setSpouse(null);
        wifeNode.setChildren(null);
        wifeNode.setAddedToTree(true);
        if(husbandNode.getFamilyMember().getWeddingDate() != null && wifeNode.getFamilyMember().getWeddingDate() == null) {
            wifeNode.getFamilyMember().setWeddingDate(husbandNode.getFamilyMember().getWeddingDate());
        }
        if(wifeNode.getFamilyMember().getWeddingDate() != null && husbandNode.getFamilyMember().getWeddingDate() == null) {
            husbandNode.getFamilyMember().setWeddingDate(wifeNode.getFamilyMember().getWeddingDate());
        }
    }

    private void validateMemberDetails(List<ExcelFamilyMember> familyMembers) throws Exception {
        for(ExcelFamilyMember member : familyMembers) {
            String firstName = member.getFirstName();
            // Check if first name is not empty
            if (firstName == null || firstName.isEmpty()) {
                throw new Exception ("Validation Error: First name is missing for member ID " + member.getMemberId());
            }

            if (member.getFamilyId() <= 0) {
                throw new Exception ("Validation Error: Invalid family ID '" + member.getFamilyId() + "' for member " + firstName);
            }

            log.info("Validating information for member: {}", firstName);
            if (Gender.getGenderByString(member.getGender()) == null) {
                throw new Exception("Validation Error: Invalid gender '" + member.getGender() + "' for member " + firstName);
            }

            FamilytreeValidationUtils.validateBirthDate(member.getBirthDay(), member.getBirthMonth(), member.getBirthYear());

            //Validate Marital Status
            if (MaritalStatus.getMaritalStatus(member.getMaritalStatus()) == null) {
                throw new Exception("Validation Error: Invalid marital status '" + member.getMaritalStatus() + "' for member " + firstName);
            }

            EmailValidator.validateEmail(member.getEmail(), false);
            if (member.getPhone() != null) {
                FamilytreeValidationUtils.validatePhoneNumber(member.getPhone());
            }

            if (member.getEducationDetails() != null) {
                validateStringLength(member.getEducationDetails(), "education details", 200);
            }

            if (member.getOccupation() != null) {
                validateStringLength(member.getOccupation(), "occupation", 100);
            }
        }
    }

    private void processMemberNode(FamilyTreeNode node, RelationshipDto relationshipDto, FamilyExcelUtil excelUtil) throws Exception {
        ExcelFamilyMember familyMember = node.getFamilyMember();
        System.out.println("Processing member: " + familyMember.getFirstName());
        if (familyMember.getMemberId() > 0) {
            log.info("Member {} already added with member ID {}", familyMember.getFirstName(), familyMember.getMemberId());
        } else {
            addMembersToApplication(node, relationshipDto);
            excelUtil.updateMemberIdInExcel(familyMember);
        }

        // Add member id to familyMember after adding to application
        //Add logic to add member to application
        if (node.getSpouse() != null) {
            FamilyTreeNode spouseNode = node.getSpouse();
            String relationshipType = null;
            if (spouseNode.getFamilyMember().getGender().equalsIgnoreCase("Male")) {
                relationshipType = "Husband";
            } else {
                relationshipType = "Wife";
            }
            RelationshipDto spouseRelationshipDto = RelationshipDto.builder()
                    .memberId(familyMember.getMemberId())
                    .memberName(familyMember.getFirstName())
                    .relationshipType(relationshipType).build();
            processMemberNode(node.getSpouse(), spouseRelationshipDto, excelUtil);
        }
        if (node.getChildren() != null) {
            for (FamilyTreeNode childNode : node.getChildren()) {
                String relationshipType = null;
                if (childNode.getFamilyMember().getGender().equalsIgnoreCase("Male")) {
                    relationshipType = "Son";
                } else {
                    relationshipType = "Daughter";
                }
                RelationshipDto childRelationshipDto = RelationshipDto.builder()
                        .memberId(familyMember.getMemberId())
                        .memberName(familyMember.getFirstName())
                        .relationshipType(relationshipType).build();

                processMemberNode(childNode, childRelationshipDto, excelUtil);
            }
        }
    }

    public void addMembersToApplication(FamilyTreeNode familyTreeNode, RelationshipDto relationshipDto) throws Exception {

        ExcelFamilyMember familyMember = familyTreeNode.getFamilyMember();
        System.out.println("Adding member: " + familyMember.getFirstName());
        //Add logic to add member to application
        AddFamilyMemberRequest request = new AddFamilyMemberRequest();
        request.setFamilyId(familyMember.getFamilyId());
        request.setFirstName(familyMember.getFirstName());
        request.setFirstNameInHindi(familyMember.getFirstNameInHindi());
        request.setHeadOfFamily(familyMember.isHeadOfFamily());
        request.setGender(familyMember.getGender());
        request.setBirthDay(familyMember.getBirthDay());
        request.setBirthMonth(familyMember.getBirthMonth());
        request.setBirthYear(familyMember.getBirthYear());
        request.setMaritalStatus(familyMember.getMaritalStatus());
        request.setWeddingDate(getDateInISOFormat(familyMember.getWeddingDate()));
        request.setEmail(familyMember.getEmail());
        request.setPhone(familyMember.getPhone());
        request.setAddressSameAsFamily(familyMember.isAddressSameAsFamily());
        request.setRelationship(relationshipDto);
        int memberId = httpUtils.addFamilyMember(request);
        familyMember.setMemberId(memberId);
    }

    private String getDateInISOFormat(Date dateValue) {
        if(dateValue == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate localDate = LocalDate.ofInstant(dateValue.toInstant(), ZoneId.systemDefault());
        return localDate.format(formatter);
    }


}