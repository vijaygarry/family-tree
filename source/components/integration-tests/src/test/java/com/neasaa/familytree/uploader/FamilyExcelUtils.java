package com.neasaa.familytree.uploader;

import com.neasaa.excel.ExcelSheet;
import com.neasaa.familytree.operation.model.AddFamilyRequest;
import com.neasaa.familytree.operation.model.ExcelFamilyMemberDetails;
import com.neasaa.familytree.operation.model.InputAddress;
import com.neasaa.familytree.operation.model.InputRelationship;
import com.neasaa.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class FamilyExcelUtils {

    public static AddFamilyRequest getFamilyDetails(ExcelSheet familySheet)
            throws Exception {
        int columnStartIndex = 1;
        int rowCounter = 1;


        String familyIdStr = familySheet.getCellValue(rowCounter++, columnStartIndex);
        int familyId = 0;
        if (familyIdStr != null && !familyIdStr.isEmpty()) {
            familyId = Integer.parseInt(familyIdStr);
        }

        AddFamilyRequest familyDetails = AddFamilyRequest.builder()
                .familyId(familyId)
                .familyName(familySheet.getCellValue(rowCounter++, columnStartIndex))
        .familyNameInHindi(familySheet.getCellValue(rowCounter++, columnStartIndex))
        .gotra(familySheet.getCellValue(rowCounter++, columnStartIndex))
                .email(familySheet.getCellValue(rowCounter++, columnStartIndex))
                .phone(getBigNumericCellValue(familySheet.getCell(rowCounter++, columnStartIndex)))
                .isPhoneWhatsappRegistered(
                        StringUtils.parseBooleanValue(familySheet.getCellValue(rowCounter++, columnStartIndex)))
                .address(InputAddress.builder()
                        .addressLine1(familySheet.getCellValue(rowCounter++, columnStartIndex))
                        .addressLine2(familySheet.getCellValue(rowCounter++, columnStartIndex))
                        .addressLine3(familySheet.getCellValue(rowCounter++, columnStartIndex))
                        .city(familySheet.getCellValue(rowCounter++, columnStartIndex))
                        .district(familySheet.getCellValue(rowCounter++, columnStartIndex))
                        .state(familySheet.getCellValue(rowCounter++, columnStartIndex))
                        .postalCode(getBigNumericCellValue(familySheet.getCell(rowCounter++, columnStartIndex)))
                        .country(familySheet.getCellValue(rowCounter++, columnStartIndex))
                        .build())
                .build();


        return familyDetails;
    }

    public static void updateFamilyIdInExcel (ExcelSheet familySheet, int familyId) {
        int columnStartIndex = 1;
        int rowCounter = 1;

        // Update the family ID in the first row
        Cell cell = familySheet.getCell(rowCounter, columnStartIndex);
        cell.setCellValue(String.valueOf(familyId));
    }

    public static void updateFamilyMemberIdInExcel (ExcelSheet familyMembersSheet, int memberId, String firstName, int memberIndex) {
        int memberIdColumnIndex = 1;
        int memberNameColumnIndex = 2;
//        int rowCounter = memberIndex;

        Cell cell = familyMembersSheet.getCell(memberIndex, memberNameColumnIndex);
        String excelFirstName = ExcelSheet.getCellValue(cell);
        if( excelFirstName == null || !excelFirstName.equals(firstName)) {
            log.warn("Family member name in excel does not match: expected {}, found {}", firstName, excelFirstName);
        }
        // Update the member ID in the second column
        cell = familyMembersSheet.getCell(memberIndex, memberIdColumnIndex);
        if (cell == null) {
            Row row = familyMembersSheet.getRow(memberIndex);
            cell = row.createCell(memberIdColumnIndex);
        }
        cell.setCellValue(String.valueOf(memberId));
    }

    public static List<ExcelFamilyMemberDetails> getFamilyMembers(ExcelSheet familyMembersSheet)
            throws Exception {
        log.info("Loading members from excel");
        int numberOfRows = familyMembersSheet.getLastRowNum();
        int startRowNumber = 1; // Index starts with 0
        List<ExcelFamilyMemberDetails> familyMembers = new ArrayList<>();
        ExcelFamilyMemberRowMapper rowMapper = new ExcelFamilyMemberRowMapper();
        for (int rowNum = startRowNumber; rowNum <= numberOfRows; rowNum++) {
            try {
                Row row = familyMembersSheet.getRow(rowNum);
                ExcelFamilyMemberDetails memberDetails = rowMapper.mapRow(row, rowNum);
                familyMembers.add(memberDetails);
            } catch (Exception e) {
                log.error("Error reading row {} from excel", rowNum + 1);
                throw e;
            }
        }
        log.info("{} members loaded from excel file", familyMembers.size());
        log.info("Members: {}", familyMembers);
        return familyMembers;
    }

    public static List<InputRelationship> getRelationships(ExcelSheet membersRelationshipSheet) {
        log.info("Loading relationships from excel");
        int numberOfRows = membersRelationshipSheet.getLastRowNum();
        int startRowNumber = 1; // Index starts with 0

        List<InputRelationship> relationships =new ArrayList<>();
        ExcelMemberRelationshipRowMapper rowMapper = new ExcelMemberRelationshipRowMapper();
        for (int rowNum = startRowNumber; rowNum <= numberOfRows; rowNum++) {
            try {
                Row row = membersRelationshipSheet.getRow(rowNum);
                InputRelationship relationship = rowMapper.mapRow(row, rowNum);
                relationships.add(relationship);
            } catch (Exception e) {
                log.error("Error reading row {} from excel", rowNum + 1);
                throw e;
            }
        }
        return relationships;
    }


    private static String getBigNumericCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        // This will return the cell value as it appears in Excel (as a String)
        return formatter.formatCellValue(cell);
    }

    private static Short getShortNumericCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        String cellValue = formatter.formatCellValue(cell);
        if (cellValue != null && !cellValue.isEmpty()) {
            return Short.parseShort(cellValue);
        }
        return null;
    }

    private static Date getDateCellValue(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }
        return null;
    }
    private static boolean getBooleanCellValue(Cell cell) {
        String cellValue = ExcelSheet.getCellValue(cell);
        if (cellValue == null || cellValue.isEmpty()) {
            return false; // Default to false if the cell is empty
        }
        return StringUtils.parseBooleanValue(cellValue);
    }

    private static class ExcelFamilyMemberRowMapper {
        public ExcelFamilyMemberDetails mapRow(Row row, int rowNum) {
            int columnCounter = 0;
            String familyIdStr = getBigNumericCellValue(row.getCell(columnCounter++));
            int familyId = 0;
            if (familyIdStr != null && !familyIdStr.isEmpty()) {
                familyId = Integer.parseInt(familyIdStr);
            }
            String familyMemberIdStr = getBigNumericCellValue(row.getCell(columnCounter++));
            int familyMemberId = -1;
            if (familyMemberIdStr != null && !familyMemberIdStr.isEmpty()) {
                familyMemberId = Integer.parseInt(familyMemberIdStr);
            }

            ExcelFamilyMemberDetails memberDetails = ExcelFamilyMemberDetails.builder()
                    .familyId(familyId)
                    .memberId(familyMemberId)
                    .firstName(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .firstNameInHindi(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .lastName(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .maidenLastName(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .nickName(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .nickNameInHindi(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .gender(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .birthDay(getShortNumericCellValue(row.getCell(columnCounter++)))
                    .birthMonth(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .birthYear(getShortNumericCellValue(row.getCell(columnCounter++)))
                    .maritalStatus(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .weddingDate(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .phone(getBigNumericCellValue(row.getCell(columnCounter++)))
                    .isPhoneWhatsappRegistered(
                            getBooleanCellValue(row.getCell(columnCounter++)))
                    .email(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .educationDetails(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .occupation(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .workingAt(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .linkedinUrl(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .hobby(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .headOfFamily(
                            getBooleanCellValue(row.getCell(columnCounter++)))
                    .addressSameAsFamily(
                            getBooleanCellValue(row.getCell(columnCounter++)))
                    .memberAddress(getAddress(row, columnCounter))
                    .dateOfDeath(getDateCellValue(row.getCell(columnCounter+8)))

                    .excelRowNumber(rowNum)
                    .build();
            columnCounter = columnCounter + 9; // Skip to the next column after address
//            String fatherName = ExcelSheet.getCellValue(row.getCell(columnCounter++));
//            String motherName = ExcelSheet.getCellValue(row.getCell(columnCounter++));
            String spouseName = ExcelSheet.getCellValue(row.getCell(columnCounter++));
            String childrenCSVNames = ExcelSheet.getCellValue(row.getCell(columnCounter++));
            memberDetails.setBelongsToOtherFamily(getBooleanCellValue(row.getCell(columnCounter++)));
            List<String> childrenNamesList = new ArrayList<>();
            if(childrenCSVNames != null && !childrenCSVNames.isEmpty()) {
                childrenNamesList = List.of(childrenCSVNames.split(","));
                childrenNamesList = childrenNamesList.stream().map(name -> name == null ? null : name.trim())
                        .toList();
            }

//            memberDetails.setFatherName(fatherName);
//            memberDetails.setMotherName(motherName);
            memberDetails.setSpouseName(spouseName);
            memberDetails.setChildrenNamesList(childrenNamesList);

            log.info("Member loaded from excel: {} from rowNum: {} ", memberDetails, rowNum);
            log.info("Spouse: {}, Children: {}", spouseName, childrenNamesList);
            return memberDetails;
        }

        private InputAddress getAddress(Row row, int columnCounter) {
            if(getBooleanCellValue(row.getCell((columnCounter-1)))) {
                // If addressSameAsFamily is true, return null
                return null;
            }
            return InputAddress.builder()
                    .addressLine1(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .addressLine2(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .addressLine3(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .city(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .district(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .state(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .postalCode(getBigNumericCellValue(row.getCell(columnCounter++)))
                    .country(ExcelSheet.getCellValue(row.getCell(columnCounter++)))
                    .build();
        }
    }

    private static class ExcelMemberRelationshipRowMapper {
        public InputRelationship mapRow(Row row, int rowNum) {
            int columnCounter = 0;
            String memberName = ExcelSheet.getCellValue(row.getCell(columnCounter++));
            String relationshipType = ExcelSheet.getCellValue(row.getCell(columnCounter++));
            String relatedMemberName = ExcelSheet.getCellValue(row.getCell(columnCounter++));
            String relatedMemberId = getBigNumericCellValue(row.getCell(columnCounter++));

            if (memberName == null || relatedMemberId == null || relatedMemberId.isEmpty()) {
                return null;
            }

            return InputRelationship.builder()
                    .memberName(memberName)
                    .relationshipType(relationshipType)
                    .relatedMemberName(relatedMemberName)
                    .relatedMemberId(Integer.parseInt(relatedMemberId))
                    .build();
        }
    }

}
