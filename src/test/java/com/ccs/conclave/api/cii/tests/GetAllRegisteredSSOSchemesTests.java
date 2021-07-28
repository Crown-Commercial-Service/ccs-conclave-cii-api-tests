package com.ccs.conclave.api.cii.tests;

import com.ccs.conclave.api.cii.pojo.AdditionalSchemeInfo;
import com.ccs.conclave.api.cii.pojo.SchemeInfo;
import com.ccs.conclave.api.cii.requests.RestRequests;
import com.ccs.conclave.api.common.BaseClass;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static com.ccs.conclave.api.cii.data.OrgDataProvider.*;
import static com.ccs.conclave.api.cii.data.RequestPayloads.getSchemeInfoWithoutAddIdentifiers;
import static com.ccs.conclave.api.cii.data.SchemeRegistry.*;
import static com.ccs.conclave.api.cii.requests.RestRequests.*;
import static com.ccs.conclave.api.cii.verification.VerifyEndpointResponses.*;

public class GetAllRegisteredSSOSchemesTests extends BaseClass {
    private final static Logger logger = Logger.getLogger(GetAllRegisteredSSOSchemesTests.class);

    @Test
    public void getAllRegisteredSchemes() {
        SchemeInfo schemeInfo = getExpectedSchemeInfo(NORTHERN_CHARITY_WITH_COH);
        SchemeInfo schemeInfoWithoutSFId = getExpSchemeInfoWithoutSFIdentifier(NORTHERN_CHARITY_WITH_COH);

        Response getSchemeInfo = getSchemeInfo(NORTHERN_CHARITY_WITH_COH, schemeInfo.getIdentifier().getId());
        Response postSchemeRes = RestRequests.postSchemeInfo(getSchemeInfo.asString());
        verifyPostSchemeInfoResponse(schemeInfoWithoutSFId, postSchemeRes);
        logger.info("Successfully registered organisation...");

        // set hidden = false as organisation is registered with additional identifier
        schemeInfo.getAdditionalIdentifiers().get(0).setHidden("false");
        // SF id is always hidden=true, which is set on the test data

        logger.info("Get All registered schemes...");
        Response registeredSchemesRes = getAllRegisteredSchemesInfo(getCCSOrgId());
        verifyAllRegisteredSchemes(registeredSchemesRes, schemeInfo);

        // Delete Database entry if the Org. is already registered
        deleteOrganisation(getCCSOrgId());
    }

    @Test
    public void getAllRegisteredSchemesIfAddIdentifiersNotSelected() {
        if(!isMockTestEnabled()) {
            SchemeInfo schemeInfo = getExpectedSchemeInfo(SCOTLAND_CHARITY_WITH_CHC_COH);
            SchemeInfo expectedPostRes = getExpSchemeInfoWithoutAddIdentifiers(SCOTLAND_CHARITY_WITH_CHC_COH);

            String getSchemeInfo = getSchemeInfoWithoutAddIdentifiers(SCOTLAND_CHARITY_WITH_CHC_COH);

            Response postSchemeRes = RestRequests.postSchemeInfo(getSchemeInfo);
            verifyPostSchemeInfoResponse(expectedPostRes, postSchemeRes);
            logger.info("Successfully registered organisation without additional identifiers...");

            // set hidden = true because no additional identifiers used for registration
            schemeInfo.getAdditionalIdentifiers().get(0).setHidden("true");
            schemeInfo.getAdditionalIdentifiers().get(1).setHidden("true");
            // SF id is always hidden=true, which is set on the test data

            logger.info("Get registered schemes...");
            Response registeredSchemesRes = getAllRegisteredSSOSchemesInfo(getCCSOrgId());
            verifyAllRegisteredSchemes(registeredSchemesRes, schemeInfo);

            // Delete Database entry if the Org. is already registered
            deleteOrganisation(getCCSOrgId());
        }
    }
}
