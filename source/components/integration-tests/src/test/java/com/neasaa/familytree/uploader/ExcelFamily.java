package com.neasaa.familytree.uploader;

import com.neasaa.excel.ExcelSheet;
import com.neasaa.excel.ExcelWorkBook;
import com.neasaa.familytree.operation.model.AddFamilyRequest;
import com.neasaa.familytree.operation.model.ExcelFamilyMemberDetails;
import com.neasaa.util.FileUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class ExcelFamily {
    private static final String FAMILY_SHEET_NAME = "FamilyDetails";
    private static final String FAMILY_MEMBER_SHEET_NAME = "Family Members";

    private final String excelFilepath;
    private ExcelWorkBook workbook = null;
    private ExcelSheet familySheet = null;
    private ExcelSheet familyMembersSheet = null;
    @Getter
    private AddFamilyRequest familyDetails = null;
    private List<ExcelFamilyMemberDetails> familyMembers = null;
    @Getter
    private MemberTreeNode root = null;

    public ExcelFamily(String excelFilepath) throws Exception {
        this.excelFilepath = excelFilepath;
        this.workbook = getExcelWorkbook(excelFilepath);
        this.familySheet = workbook.getExcelSheetByName(FAMILY_SHEET_NAME);
        this.familyMembersSheet = workbook.getExcelSheetByName(FAMILY_MEMBER_SHEET_NAME);
    }

    public void loadDataFromExcel() throws Exception {
        familyDetails = FamilyExcelUtils.getFamilyDetails(familySheet);
        log.info("Family details loaded from excel: {}", familyDetails);
        familyMembers = FamilyExcelUtils.getFamilyMembers(familyMembersSheet);
        root = buildMemberTree(familyMembers, familyDetails);
        log.info(root);
    }
    private void saveExcel() throws Exception {
        workbook.save();
        workbook = getExcelWorkbook(excelFilepath);
        this.familySheet = workbook.getExcelSheetByName(FAMILY_SHEET_NAME);
        this.familyMembersSheet = workbook.getExcelSheetByName(FAMILY_MEMBER_SHEET_NAME);
    }

    public void printFamilyTree() {
        if (root == null) {
            log.warn("Family tree is not built yet. Please load data from excel first.");
            return;
        }
        log.info("Printing family tree:");
        printMemberTree(root, "Head Of Family: ", "");
    }

    public void updateFamilyId(int familyId) throws Exception {
        if (familyDetails == null) {
            log.warn("Family details are not loaded yet. Please load data from excel first.");
            return;
        }
        familyDetails.setFamilyId(familyId);
        FamilyExcelUtils.updateFamilyIdInExcel(familySheet, familyId);
        saveExcel();
        log.info("Family ID updated in excel for family: {}", familyDetails.getFamilyName());
    }

    public void updateMemberId(MemberTreeNode familyMemberNode) throws Exception {
        if (familyMemberNode == null) {
            log.warn("Family member details are not loaded yet. Please load data from excel first.");
            return;
        }
        FamilyExcelUtils.updateFamilyMemberIdInExcel(familyMembersSheet, familyMemberNode.getMember().getMemberId(), familyMemberNode.getMember().getFirstName(), familyMemberNode.getMember().getExcelRowNumber());
        saveExcel();
        log.info("Member ID updated in excel for family: {}", familyMemberNode.getMember().getFirstName());
    }

    private MemberTreeNode buildMemberTree (List<ExcelFamilyMemberDetails> familyMembers, AddFamilyRequest familyDetails) {
        if (familyMembers == null || familyMembers.isEmpty()) {
            log.warn("No family members found to build the tree.");
            return null;
        }
        List<MemberTreeNode> memberNodes = familyMembers.stream().map(MemberTreeNode::new).toList();

        MemberTreeNode rootNode = getHeadOfFamily(memberNodes);
        setRelationships(rootNode, memberNodes);
        setParentsForHeadOfFamily(rootNode, memberNodes);
        return rootNode;
    }


    private static  MemberTreeNode getHeadOfFamily (List<MemberTreeNode> memberNodes) {
        if (memberNodes == null || memberNodes.isEmpty()) {
            throw new RuntimeException("No family members found to get head of family.");
        }
        MemberTreeNode headOfFamily = null;
        for (MemberTreeNode memberNode : memberNodes) {
            if (memberNode.getMember().isHeadOfFamily()) {
                if(headOfFamily != null) {
                    log.warn("Multiple heads of family found");
                    throw new RuntimeException("Multiple heads of family found in the list. Head of family define are " + headOfFamily.getMember().getFirstName() + " and " + memberNode.getMember().getFirstName());
                }
                headOfFamily = memberNode;
            }
        }
        if (headOfFamily != null) {
            log.info("Head of family found: {}", headOfFamily.getMember().getFirstName());
            return headOfFamily;
        }
        log.warn("No head of family found in the list.");
        throw new RuntimeException("No family members found to get head of family.");
    }

    private static void setRelationships (MemberTreeNode node, List<MemberTreeNode> memberNodes) {
        if(node == null || node.isMemberAddedToTree()) {
            return;
        }
        log.info("Setting relationships for member: {}", node.getMember().getFirstName());

        if(node.getMember().getSpouseName() != null) {
            node.setSpouse(findMemberByName(memberNodes, node.getMember().getSpouseName()));
        }
        if (node.getMember().getChildrenNamesList() != null && !node.getMember().getChildrenNamesList().isEmpty()) {
            for (String childName : node.getMember().getChildrenNamesList()) {
                MemberTreeNode childNode = findMemberByName(memberNodes, childName);
                node.addChild(childNode);
            }
        }
        node.setMemberAddedToTree();
        setRelationships(node.getSpouse(), memberNodes);
        if (node.getChildren()  != null && !node.getChildren().isEmpty()) {
            for (MemberTreeNode child : node.getChildren()) {
                setRelationships(child, memberNodes);
            }
        }

    }

    private void setParentsForHeadOfFamily (MemberTreeNode headOfFamily, List<MemberTreeNode> memberNodes) {
        if (headOfFamily == null) {
            return;
        }

        MemberTreeNode fatherOfHeadOfFamily = null;
        for(MemberTreeNode memberNode : memberNodes) {
            if (memberNode.getMember().getChildrenNamesList() != null && !memberNode.getMember().getChildrenNamesList().isEmpty()) {
                for (String childName : memberNode.getMember().getChildrenNamesList()) {
                    if (childName.equalsIgnoreCase(headOfFamily.getMember().getFirstName())) {
                        if (memberNode.getMember().getGender().equalsIgnoreCase("Male")) {
                            if (headOfFamily.getFather() != null) {
                                log.warn("Multiple fathers define in excel for : {}", headOfFamily.getMember().getFirstName());
                                throw new RuntimeException("Multiple fathers define in excel for : " + headOfFamily.getMember().getFirstName());
                            }
                            headOfFamily.setFather(memberNode);
                            fatherOfHeadOfFamily = memberNode;
                        } else {
                            if (headOfFamily.getMother() != null) {
                                log.warn("Multiple mothers define in excel for : {}", headOfFamily.getMember().getFirstName());
                                throw new RuntimeException("Multiple mothers define in excel for : " + headOfFamily.getMember().getFirstName());
                            }
                            headOfFamily.setMother(memberNode);
                        }
                    }
                }
            }
        }

        if(fatherOfHeadOfFamily != null) {
            setParentsForHeadOfFamily(fatherOfHeadOfFamily, memberNodes);
        }
    }

    private static MemberTreeNode findMemberByName(List<MemberTreeNode> memberNodes, String memberName) {
        for (MemberTreeNode memberNode : memberNodes) {
            if (memberNode.getMember().getFirstName() != null && memberNode.getMember().getFirstName().equalsIgnoreCase(memberName)) {
                return memberNode;
            }
        }
        throw new RuntimeException("No member found with name: " + memberName);
    }

    private void printMemberTree(MemberTreeNode node, String prefix, String spaces) {
        if (node == null) {
            return;
        }
        log.info("{}{}: {}", spaces, prefix, node.getMember().getFirstName());
        if (node.getSpouse() != null) {
            log.info("{}{}: {}", spaces, "Spouse", node.getSpouse().getMember().getFirstName());
        }
        if( node.getFather() != null) {
            log.info("{}{}: {}", spaces, "Father", node.getFather().getMember().getFirstName());
        }
        if( node.getMother() != null) {
            log.info("{}{}: {}", spaces, "Mother", node.getMother().getMember().getFirstName());
        }
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            for (MemberTreeNode child : node.getChildren()) {
                printMemberTree(child, "Child: ", spaces + "\t");
            }
        }
    }



    /**
     * Opens the excel file and returns the workbook object.
     *
     * @param excelFilepath Path to the excel file.
     * @return ExcelWorkBook object.
     * @throws Exception if the file does not exist or cannot be opened.
     */
    private static ExcelWorkBook getExcelWorkbook(String excelFilepath) throws Exception {
        if (!FileUtils.isFileExists(excelFilepath)) {
            throw new Exception("Excel file `" + excelFilepath + "` does not exists");
        }
        log.info("Opening {} to load family details.", excelFilepath);
        return new ExcelWorkBook(excelFilepath);
    }
}
