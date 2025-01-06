package com.infogain.arithmeticapi;

import com.infogain.asserts.VerifyResponse;
import io.restassured.response.Response;

public class VerifyArithmeticHealthCheckResponse extends VerifyResponse<VerifyArithmeticHealthCheckResponse> {

    public VerifyArithmeticHealthCheckResponse(Response response) {
        super(response);
    }
}
