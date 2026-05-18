package com.neasaa.familytree;

import java.util.HashMap;
import java.util.Map;

public class OpsTool {
    public static void main(String[] args) {
        System.out.println("Ops Tool Started");

        Map<String, String> params = parseArgs(args);

        String cmd = params.get("-cmd");
        if (cmd == null) {
            System.err.println("Missing required parameter: -cmd (upload|registration)");
            System.exit(1);
        }

        try {
            switch (cmd) {
                case "upload":
                    runUpload(params);
                    break;
                case "registration":
                    runRegistration(params);
                    break;
                default:
                    System.err.println("Unknown -cmd value: " + cmd + ". Expected: upload|registration");
                    System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to process Ops Tool.");
            System.exit(1);
        }

        System.out.println("Ops Tool Finished");
    }

    private static void runUpload(Map<String, String> params) throws Exception {
        String filepath = params.get("-f");
        if (filepath == null) {
            System.err.println("Missing required parameter for upload: -f <excel-file-path>");
            System.exit(1);
        }
        MemberProcessor memberProcessor = new MemberProcessor();
        memberProcessor.processMemberData(filepath);
    }

    private static void runRegistration(Map<String, String> params) throws Exception {
        String action = params.get("-action");
        String familyRegistrationIdStr = params.get("-familyregistrationid");

        if (action == null) {
            System.err.println("Missing required parameter for registration: -action (Approve|Invalid|Duplicate)");
            System.exit(1);
        }
        if (familyRegistrationIdStr == null) {
            System.err.println("Missing required parameter for registration: -familyregistrationid");
            System.exit(1);
        }

        int familyRegistrationId;
        try {
            familyRegistrationId = Integer.parseInt(familyRegistrationIdStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid -familyregistrationid value: " + familyRegistrationIdStr + ". Must be a number.");
            System.exit(1);
            return;
        }

        ApiUtil apiUtil = new ApiUtil();
        apiUtil.callProcessFamilyRegistrationRequest(action.toUpperCase(), familyRegistrationId);
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<>();
        if (args == null) return params;
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].startsWith("-")) {
                params.put(args[i], args[i + 1]);
                i++;
            }
        }
        return params;
    }
}
