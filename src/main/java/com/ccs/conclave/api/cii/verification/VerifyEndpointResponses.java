package com.ccs.conclave.api.cii.verification;

import com.ccs.conclave.api.cii.pojo.*;
import com.ccs.conclave.api.cii.requests.RestRequests;
import com.ccs.conclave.api.cii.response.*;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;
import java.util.Arrays;

import static com.ccs.conclave.api.cii.data.SchemeRegistry.*;
import static com.ccs.conclave.api.common.StatusCodes.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class VerifyEndpointResponses {
    private final static Logger logger = Logger.getLogger(VerifyEndpointResponses.class);
    private static String ccsOrgId;

    public static void verifyGetSchemeInfoResponse(SchemeInfo expectedSchemeInfo, Response response) {
        GetSchemeInfoResponse actualRes = new GetSchemeInfoResponse(response.getBody().as(SchemeInfo.class));
        verifyResponseCodeForSuccess(response);
        verifySchemeInfo(actualRes.getSchemeInfo(), expectedSchemeInfo);
    }

    private static void verifySchemeInfo(SchemeInfo actualSchemeInfo, SchemeInfo expectedSchemeInfo) {
        logger.info("SchemeInfo:Name " + actualSchemeInfo.getName());
        Assert.assertEquals(actualSchemeInfo.getName(), expectedSchemeInfo.getName(), "Wrong SchemeInfo:Name in response!");

        logger.info("SchemeInfo:Scheme " + actualSchemeInfo.getIdentifier().getScheme());
        logger.info("SchemeInfo:Id " + actualSchemeInfo.getIdentifier().getId());
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getScheme(), expectedSchemeInfo.getIdentifier().getScheme(), "Wrong Identifier:scheme in response!");
        // Bug:CON-488 Fixed
        Assert.assertTrue(actualSchemeInfo.getIdentifier().getId().contains(expectedSchemeInfo.getIdentifier().getId()), "Wrong Identifier:id in response!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getLegalName(), expectedSchemeInfo.getIdentifier().getLegalName(), "Wrong Identifier:legalName in response!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getUri(), expectedSchemeInfo.getIdentifier().getUri(), "Wrong Identifier:url in response!");

        Assert.assertEquals(actualSchemeInfo.getAdditionalIdentifiers().size(), expectedSchemeInfo.getAdditionalIdentifiers().size(), "AdditionalIdentifier array size is invalid!");
        for (int i = 0; i < expectedSchemeInfo.getAdditionalIdentifiers().size(); i++) {
            logger.info("Additional Identifier :ID " + actualSchemeInfo.getAdditionalIdentifiers().get(0).getId());
            Assert.assertEquals(actualSchemeInfo.getAdditionalIdentifiers().get(i).getId(), expectedSchemeInfo.getAdditionalIdentifiers().get(i).getId(), "Wrong Additional Identifier:id in response!");
            Assert.assertEquals(actualSchemeInfo.getAdditionalIdentifiers().get(i).getScheme(), expectedSchemeInfo.getAdditionalIdentifiers().get(i).getScheme(), "Wrong Additional Identifier:scheme in response!");
            Assert.assertEquals(actualSchemeInfo.getAdditionalIdentifiers().get(i).getLegalName(), expectedSchemeInfo.getAdditionalIdentifiers().get(i).getLegalName(), "Wrong Additional Identifier:legal name in response!");
            Assert.assertEquals(actualSchemeInfo.getAdditionalIdentifiers().get(i).getUri(), expectedSchemeInfo.getAdditionalIdentifiers().get(i).getUri(), "Wrong Additional Identifier:Uri in response!");
        }


        verifyAddress(expectedSchemeInfo.getAddress(), actualSchemeInfo.getAddress());

        verifyContactPoint(expectedSchemeInfo.getContactPoint(), actualSchemeInfo.getContactPoint());
    }

    private static void verifyAddress(Address expected, Address actual) {
        logger.info("Address:StreetAddress " + actual.getStreetAddress());
        Assert.assertEquals(actual.getStreetAddress(), expected.getStreetAddress(), "Wrong address:streetAddress in response!");
        Assert.assertEquals(actual.getLocality(),expected.getLocality(), "Wrong address:locality in response!");
        logger.info("Address:region " + actual.getRegion());
        Assert.assertEquals(actual.getRegion(), expected.getRegion(), "Wrong address:region in response!");
        Assert.assertEquals(actual.getPostalCode(), expected.getPostalCode(), "Wrong address:postalCode in response!");
        Assert.assertEquals(actual.getCountryName(), expected.getCountryName(), "Wrong address:countryName in response!");

    }

    // Verify Contact point is not empty if expected
    private static void verifyContactPoint(ContactPoint expected, ContactPoint actual) {
        if (!expected.getName().isEmpty())
            Assert.assertTrue(!(actual.getName().isEmpty()), "Wrong contactPoint:name in response!");
        else
            Assert.assertTrue((actual.getName().isEmpty()), "Wrong contactPoint:name in response!");

        if (!expected.getEmail().isEmpty())
            Assert.assertTrue(!(actual.getEmail().isEmpty()), "Wrong contactPoint:email in response!");
        else
            Assert.assertTrue((actual.getEmail().isEmpty()), "Wrong contactPoint:email in response!");

        if (!expected.getFaxNumber().isEmpty())
            Assert.assertTrue(!(actual.getFaxNumber().isEmpty()), "Wrong contactPoint:faxNumber in response!");
        else
            Assert.assertTrue((actual.getFaxNumber().isEmpty()), "Wrong contactPoint:FaxNumber in response!");

        if (!expected.getTelephone().isEmpty())
            Assert.assertTrue(!(actual.getTelephone().isEmpty()), "Wrong contactPoint:telephone in response!");
        else
            Assert.assertTrue((actual.getTelephone().isEmpty()), "Wrong contactPoint:telephone in response!");

        if (!expected.getUri().isEmpty())
            Assert.assertTrue(!(actual.getUri().isEmpty()), "Wrong contactPoint:uri in response!");
        else
            Assert.assertTrue((actual.getUri().isEmpty()), "Wrong contactPoint:uri in response!");
    }

    public static void verifyGetSchemesResponse(Response response) {
        verifyResponseCodeForSuccess(response);
        logger.info("GetSchemesResponse" + response.asString());

        GetSchemesResponse schemesResponse = new GetSchemesResponse(Arrays.asList(response.getBody().as(Scheme[].class)));
        Assert.assertEquals(schemesResponse.getSchemes().size(), 5, "Wrong number of schemes are returned!");

        Scheme scheme = schemesResponse.getSchemes().get(0);
        Assert.assertEquals(scheme.getScheme(), getSchemeCode(COMPANIES_HOUSE), "Invalid SchemeCode!");
        Assert.assertEquals(scheme.getSchemeName(), getSchemeName(COMPANIES_HOUSE), "Invalid SchemeName!");
        Assert.assertEquals(scheme.getSchemeCountryCode(), getSchemeCountryCode(COMPANIES_HOUSE), "Invalid CountryCode!");

        scheme = schemesResponse.getSchemes().get(1);
        Assert.assertEquals(scheme.getScheme(), getSchemeCode(DUN_AND_BRADSTREET), "Invalid SchemeCode!");
        Assert.assertEquals(scheme.getSchemeName(), getSchemeName(DUN_AND_BRADSTREET), "Invalid SchemeName!");
        Assert.assertEquals(scheme.getSchemeCountryCode(), getSchemeCountryCode(DUN_AND_BRADSTREET), "Invalid CountryCode!");

        scheme = schemesResponse.getSchemes().get(2);
        Assert.assertEquals(scheme.getScheme(), getSchemeCode(CHARITIES_COMMISSION), "Invalid SchemeCode!");
        Assert.assertEquals(scheme.getSchemeName(), getSchemeName(CHARITIES_COMMISSION), "Invalid SchemeName!");
        Assert.assertEquals(scheme.getSchemeCountryCode(), getSchemeCountryCode(CHARITIES_COMMISSION), "Invalid CountryCode!");

        scheme = schemesResponse.getSchemes().get(3);
        Assert.assertEquals(scheme.getScheme(), getSchemeCode(SCOTLAND_CHARITY), "Invalid SchemeCode!");
        Assert.assertEquals(scheme.getSchemeName(), getSchemeName(SCOTLAND_CHARITY), "Invalid SchemeName!");
        Assert.assertEquals(scheme.getSchemeCountryCode(), getSchemeCountryCode(SCOTLAND_CHARITY), "Invalid CountryCode!");

        scheme = schemesResponse.getSchemes().get(4);
        Assert.assertEquals(scheme.getScheme(), getSchemeCode(NORTHERN_CHARITY), "Invalid SchemeCode!");
        Assert.assertEquals(scheme.getSchemeName(), getSchemeName(NORTHERN_CHARITY), "Invalid SchemeName!");
        Assert.assertEquals(scheme.getSchemeCountryCode(), getSchemeCountryCode(NORTHERN_CHARITY), "Invalid CountryCode!");
    }

    // This method verifies only the registered schemes; to verify the hidden schemes as part of post need to use VerifyAllRegistered
    // Schemes
    public static void verifyPostSchemeInfoResponse(SchemeInfo expectedSchemeInfo, Response response) {
        verifyResponseCodeForCreatedResource(response);
        PostSchemeInfoResponse actualResponse = new PostSchemeInfoResponse(response.getBody().as(OrgIdentifier.class));
        Assert.assertTrue(!actualResponse.getOrgIdentifier().getCcsOrgId().isEmpty(), "Not expected Post response! OrgId is empty!!");
        ccsOrgId = actualResponse.getOrgIdentifier().getCcsOrgId();
        logger.info("CcsOrgId: " + actualResponse.getOrgIdentifier().getCcsOrgId());

        // get registered schemes after post
        Response regSchemesRes = RestRequests.getRegisteredSchemesInfo(ccsOrgId);
        verifyRegisteredSchemes(regSchemesRes, expectedSchemeInfo, expectedSchemeInfo.getAdditionalIdentifiers().size());
    }

    public static void verifyDuplicatePostDataMigrationEndptResponse(SchemeInfo expectedSchemeInfo, Response response) {
        verifyResponseCodeForDuplicateResource(response);
        PostSchemeInfoResponse actualResponse = new PostSchemeInfoResponse(response.getBody().as(OrgIdentifier.class));
        Assert.assertTrue(!actualResponse.getOrgIdentifier().getCcsOrgId().isEmpty(), "Not expected Post response! OrgId is empty!!");
        ccsOrgId = actualResponse.getOrgIdentifier().getCcsOrgId();
        logger.info("CcsOrgId: " + actualResponse.getOrgIdentifier().getCcsOrgId());

        // get registered schemes after post
        Response regSchemesRes = RestRequests.getAllRegisteredSchemesInfo(ccsOrgId);
        verifyAllRegisteredSchemes(regSchemesRes, expectedSchemeInfo);
    }

    public static void verifyPostDataMigrationEndpointResponse(SchemeInfo expectedSchemeInfo, Response response) {
        verifyResponseCodeForCreatedResource(response);
        PostDataMigrationEndpointResponse actualResponse = new PostDataMigrationEndpointResponse(response.getBody().as(OrganisationInfo.class));
        Assert.assertTrue(!actualResponse.getOrganisationInfo().getOrganisationId().isEmpty(), "Not expected Post response! OrgId is empty!!");
        ccsOrgId = actualResponse.getOrganisationInfo().getOrganisationId();
        logger.info("CcsOrgId: " + ccsOrgId);

        Identifier identifier = actualResponse.getOrganisationInfo().getIdentifier();
        Assert.assertEquals(identifier.getId(), expectedSchemeInfo.getIdentifier().getId(), "Invalid Id returned via get registered schemes!!");
        Assert.assertEquals(identifier.getScheme(), expectedSchemeInfo.getIdentifier().getScheme(), "Invalid scheme returned via get registered schemes!!!!");
        Assert.assertEquals(identifier.getUri(), expectedSchemeInfo.getIdentifier().getUri(), "Invalid uri returned via get registered schemes!!!!");
        Assert.assertEquals(identifier.getLegalName(), expectedSchemeInfo.getIdentifier().getLegalName(), "Invalid Legal name returned via get registered schemes!!!!");

        Assert.assertEquals(expectedSchemeInfo.getAdditionalIdentifiers().size(), actualResponse.getOrganisationInfo().getAdditionalIdentifiers().size(), "Wrong number of additional identifiers!!!!");

        // The order of additional identifier may be different in the response
        int addIdentifierPresent = 0;
        for (Identifier expectedAddIdentifier : expectedSchemeInfo.getAdditionalIdentifiers()) {
            for (Identifier actualAddIdentifier : actualResponse.getOrganisationInfo().getAdditionalIdentifiers()) {
                if (actualAddIdentifier.getId().equals(expectedAddIdentifier.getId()) &&
                        actualAddIdentifier.getScheme().equals(expectedAddIdentifier.getScheme())) {
                    ++addIdentifierPresent;
                    Assert.assertEquals(actualAddIdentifier.getId(), expectedAddIdentifier.getId(), "Invalid Id for add identifier returned via get registered schemes!!!!");
                    Assert.assertEquals(actualAddIdentifier.getScheme(), expectedAddIdentifier.getScheme(), "Invalid scheme for add identifier returned via get registered schemes!!");
                    Assert.assertEquals(actualAddIdentifier.getUri(), expectedAddIdentifier.getUri(), "Invalid uri for add identifier returned via get registered schemes!!");
                    Assert.assertEquals(actualAddIdentifier.getLegalName(), expectedAddIdentifier.getLegalName(), "Invalid legal name for add identifier returned via get registered schemes!!");
                }
            }
            Assert.assertEquals(addIdentifierPresent, 1, "Additional identifier is not returned as part of Get All Registered Schemes!!!!");
            --addIdentifierPresent;
        }

        verifyAddress(expectedSchemeInfo.getAddress(), actualResponse.getOrganisationInfo().getAddress());
        verifyContactPoint(expectedSchemeInfo.getContactPoint(), actualResponse.getOrganisationInfo().getContactPoint());
    }

    public static void verifyUpdatedScheme(String ccsOrgId, AdditionalSchemeInfo expectedAdditionalSchemeInfo) {
        Response actualRes = RestRequests.getRegisteredSchemesInfo(ccsOrgId);
        GetRegisteredSchemesResponse registeredSchemeInfoRes = new GetRegisteredSchemesResponse(actualRes.getBody().as(RegisteredSchemeInfo.class));
        RegisteredSchemeInfo actualSchemeInfo = registeredSchemeInfoRes.getRegisteredSchemesInfo();

        int addIdentifierPresent = 0;
        if (actualSchemeInfo.getIdentifier().getId().equals(expectedAdditionalSchemeInfo.getIdentifier().getId())) {
            Assert.assertEquals(actualSchemeInfo.getIdentifier().getId(), expectedAdditionalSchemeInfo.getIdentifier().getId(), "Invalid Id returned via get registered schemes!!");
            Assert.assertEquals(actualSchemeInfo.getIdentifier().getScheme(), expectedAdditionalSchemeInfo.getIdentifier().getScheme(), "Invalid scheme returned via get registered schemes!!!!");
            Assert.assertEquals(actualSchemeInfo.getIdentifier().getUri(), expectedAdditionalSchemeInfo.getIdentifier().getUri(), "Invalid uri returned via get registered schemes!!!!");
            Assert.assertEquals(actualSchemeInfo.getIdentifier().getLegalName(), expectedAdditionalSchemeInfo.getIdentifier().getLegalName(), "Invalid Legal name returned via get registered schemes!!!!");
            ++addIdentifierPresent;
        }  else {
            for (Identifier addIdentifier : actualSchemeInfo.getAdditionalIdentifiers()) {
                if (addIdentifier.getId().equals(expectedAdditionalSchemeInfo.getIdentifier().getId())) {
                    Assert.assertEquals(addIdentifier.getId(), expectedAdditionalSchemeInfo.getIdentifier().getId(), "Invalid Id returned via get registered schemes!!");
                    Assert.assertEquals(addIdentifier.getScheme(), expectedAdditionalSchemeInfo.getIdentifier().getScheme(), "Invalid scheme returned via get registered schemes!!!!");
                    Assert.assertEquals(addIdentifier.getUri(), expectedAdditionalSchemeInfo.getIdentifier().getUri(), "Invalid uri returned via get registered schemes!!!!");
                    Assert.assertEquals(addIdentifier.getLegalName(), expectedAdditionalSchemeInfo.getIdentifier().getLegalName(), "Invalid Legal name returned via get registered schemes!!!!");
                    ++addIdentifierPresent;
                }
            }
        }
        Assert.assertEquals(addIdentifierPresent, 1, "Additional identifier is updated as part of Update call!!!!");
    }

    public static void verifyDeletedScheme(String ccsOrgId, AdditionalSchemeInfo deletedAdditionalSchemeInfo) {
        Response actualRes = RestRequests.getAllRegisteredSchemesInfo(ccsOrgId);
        GetRegisteredSchemesResponse registeredSchemeInfoRes = new GetRegisteredSchemesResponse(actualRes.getBody().as(RegisteredSchemeInfo.class));
        RegisteredSchemeInfo actualSchemeInfo = registeredSchemeInfoRes.getRegisteredSchemesInfo();

        if (actualSchemeInfo.getIdentifier().getId().equals(deletedAdditionalSchemeInfo.getIdentifier().getId())) {
            Assert.fail("Deleted Scheme call failed!!");
        }  else {
            for (Identifier addIdentifier : actualSchemeInfo.getAdditionalIdentifiers()) {
                if (addIdentifier.getId().equals(deletedAdditionalSchemeInfo.getIdentifier().getId())) {
                    Assert.fail("Deleted Scheme call failed!!");
                }
            }
        }
    }

    public static void verifyResponseCodeForCreatedResource(Response response) {
        Assert.assertEquals(response.getStatusCode(), CREATED.getCode(), "Unexpected Status code returned for created!!");
    }

    public static void verifyResponseCodeForDuplicateResource(Response response) {
        Assert.assertEquals(response.getStatusCode(), DUPLICATE_RESOURCE.getCode(), "Unexpected Status code returned for Duplicate Resource!!");
    }

    public static void verifyResponseCodeForSuccess(Response response) {
        Assert.assertEquals(response.getStatusCode(), OK.getCode(), "Unexpected Status code returned for deleted Resource!!");
    }

    public static void verifyBadRequestResponse(Response response) {
        Assert.assertEquals(response.getStatusCode(), BAD_REQUEST.getCode(), "Unexpected Status code returned for Invalid Scheme!!");
    }

    public static void verifyInvalidIdResponse(Response response) {
        Assert.assertEquals(response.getStatusCode(), NOT_FOUND.getCode(), "Unexpected Status code returned for Invalid Id!!");
    }

    public static String getCCSOrgId() {
        return ccsOrgId;
    }

    public static String getCCSOrgIdFromDataMigrationEndpoint(Response response) {
        PostDataMigrationEndpointResponse actualResponse = new PostDataMigrationEndpointResponse(response.getBody().as(OrganisationInfo.class));
        Assert.assertTrue(!actualResponse.getOrganisationInfo().getOrganisationId().isEmpty(), "Not expected Post response!");
        ccsOrgId = actualResponse.getOrganisationInfo().getOrganisationId();
        return ccsOrgId;
    }

    public static void verifyManageIdentifiersResponse(Response expectedRes, Response actualRes) {
        // Address, ContactPoint and name are not part of ManageIdentifiers get call response
        String actualResIdentifiers = actualRes.asString().split("address")[0].split("identifier")[1];
        String expectedResIdentifiers = expectedRes.asString().split("address")[0].split("identifier")[1];
        assertThat(expectedResIdentifiers, is(actualResIdentifiers));
    }

    public static void verifyRegisteredSchemes(Response actualRes, SchemeInfo expectedSchemeInfo, int selectedAddIds) {
        verifyResponseCodeForSuccess(actualRes);
        GetRegisteredSchemesResponse registeredSchemeInfoRes = new GetRegisteredSchemesResponse(actualRes.getBody().as(RegisteredSchemeInfo.class));
        RegisteredSchemeInfo actualSchemeInfo = registeredSchemeInfoRes.getRegisteredSchemesInfo();
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getId(), expectedSchemeInfo.getIdentifier().getId(), "Invalid Id returned via get registered schemes!!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getScheme(), expectedSchemeInfo.getIdentifier().getScheme(), "Invalid scheme returned via get registered schemes!!!!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getUri(), expectedSchemeInfo.getIdentifier().getUri(), "Invalid uri returned via get registered schemes!!!!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getLegalName(), expectedSchemeInfo.getIdentifier().getLegalName(), "Invalid Legal name returned via get registered schemes!!!!");

        Assert.assertEquals(actualSchemeInfo.getAdditionalIdentifiers().size(), selectedAddIds, "Wrong number of hidden:false(visible) additional identifiers!!!!");

        int addIdentifierPresent = 0;
        for (int i = 0; i < selectedAddIds; i++) {
            Identifier expectedAddIdentifier = expectedSchemeInfo.getAdditionalIdentifiers().get(i);
            for (Identifier actualAddIdentifier : actualSchemeInfo.getAdditionalIdentifiers()) {
                if (actualAddIdentifier.getId().equals(expectedAddIdentifier.getId()) &&
                        actualAddIdentifier.getScheme().equals(expectedAddIdentifier.getScheme())) {
                    ++addIdentifierPresent;
                    Assert.assertEquals(actualAddIdentifier.getId(), expectedAddIdentifier.getId(), "Invalid Id for add identifier returned via get registered schemes!!!!");
                    Assert.assertEquals(actualAddIdentifier.getScheme(), expectedAddIdentifier.getScheme(), "Invalid scheme for add identifier returned via get registered schemes!!");
                    Assert.assertEquals(actualAddIdentifier.getUri(), expectedAddIdentifier.getUri(), "Invalid uri for add identifier returned via get registered schemes!!");
                    Assert.assertEquals(actualAddIdentifier.getLegalName(), expectedAddIdentifier.getLegalName(), "Invalid legal name for add identifier returned via get registered schemes!!");
                }
            }
            Assert.assertEquals(addIdentifierPresent, 1, "Additional identifier is not returned as part of Get Registered Schemes!!!!");
            --addIdentifierPresent;
        }
    }

    public static void verifyAllRegisteredSchemes(Response actualRes, SchemeInfo expectedSchemeInfo) {
        verifyResponseCodeForSuccess(actualRes);
        GetRegisteredSchemesResponse registeredSchemeInfoRes = new GetRegisteredSchemesResponse(actualRes.getBody().as(RegisteredSchemeInfo.class));
        RegisteredSchemeInfo actualSchemeInfo = registeredSchemeInfoRes.getRegisteredSchemesInfo();
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getId(), expectedSchemeInfo.getIdentifier().getId(), "Invalid Id returned via get registered schemes!!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getScheme(), expectedSchemeInfo.getIdentifier().getScheme(), "Invalid scheme returned via get registered schemes!!!!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getUri(), expectedSchemeInfo.getIdentifier().getUri(), "Invalid uri returned via get registered schemes!!!!");
        Assert.assertEquals(actualSchemeInfo.getIdentifier().getLegalName(), expectedSchemeInfo.getIdentifier().getLegalName(), "Invalid Legal name returned via get registered schemes!!!!");

        Assert.assertEquals(expectedSchemeInfo.getAdditionalIdentifiers().size(), actualSchemeInfo.getAdditionalIdentifiers().size(), "Wrong number of additional identifiers!!!!");

        // The order of additional identifier may be different in the response
        int addIdentifierPresent = 0;
        for (Identifier expectedAddIdentifier : expectedSchemeInfo.getAdditionalIdentifiers()) {
            for (Identifier actualAddIdentifier : actualSchemeInfo.getAdditionalIdentifiers()) {
                if (actualAddIdentifier.getId().equals(expectedAddIdentifier.getId()) &&
                        actualAddIdentifier.getScheme().equals(expectedAddIdentifier.getScheme())) {
                    ++addIdentifierPresent;
                    Assert.assertEquals(actualAddIdentifier.getId(), expectedAddIdentifier.getId(), "Invalid Id for add identifier returned via get registered schemes!!!!");
                    Assert.assertEquals(actualAddIdentifier.getScheme(), expectedAddIdentifier.getScheme(), "Invalid scheme for add identifier returned via get registered schemes!!");
                    Assert.assertEquals(actualAddIdentifier.getUri(), expectedAddIdentifier.getUri(), "Invalid uri for add identifier returned via get registered schemes!!");
                    Assert.assertEquals(actualAddIdentifier.getLegalName(), expectedAddIdentifier.getLegalName(), "Invalid legal name for add identifier returned via get registered schemes!!");
                    Assert.assertEquals(actualAddIdentifier.getHidden(), expectedAddIdentifier.getHidden(), "Hidden value in add identifier is returned via get registered schemes!!");
                }
            }
            Assert.assertEquals(addIdentifierPresent, 1, "Additional identifier is not returned as part of Get All Registered Schemes!!!!");
            --addIdentifierPresent;
        }
    }
}

