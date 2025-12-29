package com.neasaa.familytree.excel;

import com.neasaa.excel.ExcelSheet;
import com.neasaa.excel.ExcelWorkBook;
import com.neasaa.familytree.dto.ExcelFamilyMember;
import com.neasaa.util.FileUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FamilyExcelUtil {
    private static final String FAMILY_SHEET_NAME = "FamilyDetails";
    private static final String FAMILY_MEMBER_SHEET_NAME = "Family Members";

    private final String excelFilepath;
    private ExcelWorkBook workbook = null;
    private ExcelSheet familySheet = null;
    private ExcelSheet familyMembersSheet = null;

    public FamilyExcelUtil(String excelFilepath) throws Exception {
        this.excelFilepath = excelFilepath;
        this.workbook = getExcelWorkbook(excelFilepath);
        this.familySheet = workbook.getExcelSheetByName(FAMILY_SHEET_NAME);
        this.familyMembersSheet = workbook.getExcelSheetByName(FAMILY_MEMBER_SHEET_NAME);
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

    public List<ExcelFamilyMember> getFamilyMembers()
            throws Exception {
        log.info("Loading members from excel");
        int numberOfRows = familyMembersSheet.getLastRowNum();
        int startRowNumber = 1; // Index starts with 0
        List<ExcelFamilyMember> familyMembers = new ArrayList<>();
        MemberRowMapper rowMapper = new MemberRowMapper();
        for (int rowNum = startRowNumber; rowNum <= numberOfRows; rowNum++) {
            try {
                Row row = familyMembersSheet.getRow(rowNum);
                ExcelFamilyMember memberDetails = rowMapper.mapRow(row, rowNum);
                if(memberDetails.getFirstName() == null || memberDetails.getFirstName().isEmpty()) {
                    log.warn("Skipping row {} as first name is missing", rowNum + 1);
                    continue;
                }
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

    public void updateMemberIdInExcel(ExcelFamilyMember familyMember) throws Exception {
        int memberIdColumnIndex = MemberRowMapper.MEMBER_ID_COLUMN_INDEX;
        int memberNameColumnIndex = 0;

        Cell cell = familyMembersSheet.getCell(familyMember.getExcelRowNumber(), memberNameColumnIndex);
        String excelFirstName = ExcelSheet.getCellValue(cell);
        if( excelFirstName == null || !excelFirstName.equals(familyMember.getFirstName())) {
            log.warn("Family member name in excel does not match: expected {}, found {}", familyMember.getFirstName(), excelFirstName);
        }
        // Update the member ID in the second column
        cell = familyMembersSheet.getCell(familyMember.getExcelRowNumber(), memberIdColumnIndex);
        if (cell == null) {
            Row row = familyMembersSheet.getRow(familyMember.getExcelRowNumber());
            cell = row.createCell(memberIdColumnIndex);
        }
        cell.setCellValue(String.valueOf(familyMember.getMemberId()));
        saveExcel();
        log.info("Member ID {} updated in excel for family: {}", familyMember.getMemberId(), familyMember.getFirstName());
    }

    private void saveExcel() throws Exception {
        workbook.save();
        workbook = getExcelWorkbook(excelFilepath);
        this.familySheet = workbook.getExcelSheetByName(FAMILY_SHEET_NAME);
        this.familyMembersSheet = workbook.getExcelSheetByName(FAMILY_MEMBER_SHEET_NAME);
    }
}
