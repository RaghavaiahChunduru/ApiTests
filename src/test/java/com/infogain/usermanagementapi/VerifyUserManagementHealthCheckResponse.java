package com.infogain.usermanagementapi;

import com.infogain.asserts.VerifyResponse;
import io.restassured.response.Response;

public class VerifyUserManagementHealthCheckResponse extends VerifyResponse<VerifyUserManagementHealthCheckResponse> {

    public VerifyUserManagementHealthCheckResponse(Response response) {
        super(response);

    }
}
