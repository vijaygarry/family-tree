package com.neasaa.familytree;

public class OpsTool {
    public static void main(String[] args) {
        System.out.println("Ops Tool Started");
        MemberProcessor memberProcessor = new MemberProcessor();
        String excelFilepath = "/Users/vijaygarothaya/work/product/chippaSamaj/Documents/FamilyData/BachchuBhaiya-Magroriya.xlsx";
        try {
            memberProcessor.processMemberData(excelFilepath);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to process Ops Tool.");
            System.exit(1);
        }
        System.out.println("Ops Tool Finished");
    }
}
