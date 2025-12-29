package com.neasaa.familytree.excel;

import com.neasaa.excel.ExcelSheet;
import com.neasaa.familytree.dto.ExcelFamilyMember;
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
public class MemberRowMapper {

    private static final int SPOUSE_NAME_COLUMN_INDEX = 14;
    private static final int CHILDREN_NAMES_COLUMN_INDEX = SPOUSE_NAME_COLUMN_INDEX + 1;
    private static final int FAMILY_ID_COLUMN_INDEX = CHILDREN_NAMES_COLUMN_INDEX + 1;
    public static final int MEMBER_ID_COLUMN_INDEX = FAMILY_ID_COLUMN_INDEX + 1;


    public ExcelFamilyMember mapRow(Row row, int rowNum) {
        String familyIdStr = getBigNumericCellValue(row.getCell(FAMILY_ID_COLUMN_INDEX));
        int familyId = 0;
        if (familyIdStr != null && !familyIdStr.isEmpty()) {
            familyId = Integer.parseInt(familyIdStr);
        }
        String familyMemberIdStr = getBigNumericCellValue(row.getCell(MEMBER_ID_COLUMN_INDEX));
        int familyMemberId = -1;
        if (familyMemberIdStr != null && !familyMemberIdStr.isEmpty()) {
            familyMemberId = Integer.parseInt(familyMemberIdStr);
        }
        int columnCounter = 0;
        ExcelFamilyMember memberDetails = ExcelFamilyMember.builder()
                .familyId(familyId)
                .memberId(familyMemberId)
                .firstName(getStringCellValue(row.getCell(columnCounter++)))
                .firstNameInHindi(getStringCellValue(row.getCell(columnCounter++)))
                .headOfFamily(getBooleanCellValue(row.getCell(columnCounter++)))
                .nickName(getStringCellValue(row.getCell(columnCounter++)))
                .gender(getStringCellValue(row.getCell(columnCounter++)))
                .birthDay(getShortNumericCellValue(row.getCell(columnCounter++)))
                .birthMonth(getStringCellValue(row.getCell(columnCounter++)))
                .birthYear(getShortNumericCellValue(row.getCell(columnCounter++)))
                .maritalStatus(getStringCellValue(row.getCell(columnCounter++)))
                .phone(getBigNumericCellValue(row.getCell(columnCounter++)))
                .email(getStringCellValue(row.getCell(columnCounter++)))
                .educationDetails(getStringCellValue(row.getCell(columnCounter++)))
                .occupation(getStringCellValue(row.getCell(columnCounter++)))
                .weddingDate(getDateCellValue(row.getCell(columnCounter++)))
                .addressSameAsFamily(true)
                .excelRowNumber(rowNum)
                .build();

        String spouseName = ExcelSheet.getCellValue(row.getCell(SPOUSE_NAME_COLUMN_INDEX));
        String childrenCSVNames = ExcelSheet.getCellValue(row.getCell(CHILDREN_NAMES_COLUMN_INDEX));
        List<String> childrenNamesList = new ArrayList<>();
        if(childrenCSVNames != null && !childrenCSVNames.isEmpty()) {
            childrenNamesList = List.of(childrenCSVNames.split(","));
            childrenNamesList = childrenNamesList.stream().map(name -> name == null ? null : name.trim())
                    .toList();
        }
        memberDetails.setSpouseName(spouseName);
        memberDetails.setChildrenNamesList(childrenNamesList);

        log.debug("Member loaded from excel: {} from rowNum: {} ", memberDetails, rowNum);
        return memberDetails;
    }

    private static String getBigNumericCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        // This will return the cell value as it appears in Excel (as a String)
        String cellValue = formatter.formatCellValue(cell);
        if(cellValue != null && !cellValue.isEmpty()) {
            return cellValue.trim();
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

    private static Short getShortNumericCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        String cellValue = formatter.formatCellValue(cell);
        if (cellValue != null && !cellValue.isEmpty()) {
            return Short.parseShort(cellValue);
        }
        return null;
    }

    private String getStringCellValue(Cell cell) {
        String cellValue = ExcelSheet.getCellValue(cell);
        if(cellValue != null && !cellValue.isEmpty()) {
            return cellValue.trim();
        }
        return null;
    }
}
