package com.infogain.arithmeticapi;

import com.infogain.asserts.VerifyResponse;
import io.restassured.response.Response;

public class VerifyArithmeticOperationResponse extends VerifyResponse<VerifyArithmeticOperationResponse> {

    public VerifyArithmeticOperationResponse(Response response) {
        super(response);
    }
}
