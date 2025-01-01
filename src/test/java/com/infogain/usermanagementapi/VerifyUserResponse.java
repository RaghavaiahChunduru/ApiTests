package com.infogain.usermanagementapi;

import com.infogain.asserts.VerifyResponse;
import io.restassured.response.Response;

public class VerifyUserResponse extends VerifyResponse<VerifyUserResponse> {

  public VerifyUserResponse(Response response) {
    super(response);
  }
}
