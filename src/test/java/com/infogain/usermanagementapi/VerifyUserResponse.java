package com.infogain.usermanagementapi;

import com.infogain.api.usermanagement.User;
import com.infogain.api.usermanagement.UserResponse;
import com.infogain.asserts.VerifyResponse;
import io.restassured.response.Response;

public class VerifyUserResponse extends VerifyResponse<VerifyUserResponse> {

  private VerifyUserResponse(Response response) {
    super(VerifyUserResponse.class, response);
  }

  public static VerifyUserResponse assertThat(Response response) {
    return new VerifyUserResponse(response);
  }

  public VerifyUserResponse postHasUser(User expectedUser) {
    UserResponse userResponse = response.then().extract().response().as(UserResponse.class);

    softAssertions.assertThat(userResponse.getUser()).describedAs("user").isEqualTo(expectedUser);

    return this;
  }

  public VerifyUserResponse hasUser(User expectedUser) {
    User userResponse = response.then().extract().response().as(User.class);

    softAssertions.assertThat(userResponse).describedAs("user").isEqualTo(expectedUser);

    return this;
  }
}
