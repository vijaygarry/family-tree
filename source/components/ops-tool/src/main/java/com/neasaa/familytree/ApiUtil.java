package com.neasaa.familytree;

import com.neasaa.familytree.operation.family.model.ProcessFamilyRegistrationRequest;

public class ApiUtil {
    public void callProcessFamilyRegistrationRequest(String action, int familyRegistrationId) throws Exception {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.createSession();
        httpUtils.whoAmI();
        try {
            callApi(httpUtils, familyRegistrationId, action);
        } finally {
            httpUtils.logout();
        }
    }

    public void callApi(HttpUtils httpUtils, int familyRequestId, String action) throws Exception {
        ProcessFamilyRegistrationRequest request = ProcessFamilyRegistrationRequest
                .builder().familyRegistrationId(familyRequestId).action(action).build();
        System.out.println("Processing family registration: " + familyRequestId + " action: " + action);
        httpUtils.processFamilyRegistrationRequest(request);
    }
}
