package com.ccs.conclave.api.cii.response;

import com.ccs.conclave.api.cii.pojo.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PostDataMigrationEndpointResponse {
    private OrganisationInfo organisationInfo;

    public PostDataMigrationEndpointResponse(OrganisationInfo organisationInfo) {
        this.organisationInfo = organisationInfo;
    }
}
