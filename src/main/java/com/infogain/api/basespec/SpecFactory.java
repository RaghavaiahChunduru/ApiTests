package com.infogain.api.basespec;

import static com.infogain.api.auth.Scope.ADMIN;
import static com.infogain.api.auth.Scope.MAINTAINER;
import static com.infogain.utils.ConfigUtil.CONFIG;

import com.infogain.api.auth.Scope;
import com.infogain.api.auth.TokenFactory;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;

public class SpecFactory {

  public static RequestSpecification getSpecFor(Scope scope) {
    RequestSpecification requestSpec;
    switch (scope) {
      case GUEST:
        requestSpec = get().build();
        break;
      case MAINTAINER:
        requestSpec = get(TokenFactory.getInstance().getTokenFor(MAINTAINER)).build();
        break;
      case ADMIN:
        requestSpec = get(TokenFactory.getInstance().getTokenFor(ADMIN)).build();
        break;
      default:
        throw new IllegalStateException(
            "Not a valid scope. Pick a scope from " + Arrays.toString(Scope.values()));
    }
    return requestSpec;
  }

  private static RequestSpecBuilder get() {
    return new RequestSpecBuilder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .setBaseUri(CONFIG.getString("BASE_URL"));
  }

  private static RequestSpecBuilder get(String token) {
    return get()
        .addHeader("Cookie", "token=" + token)
        .addHeader("Authorization", "some-value")
        .setConfig(
            RestAssuredConfig.config()
                .logConfig(
                    LogConfig.logConfig()
                        .blacklistHeaders(Arrays.asList("Cookie", "Authorization"))));
  }
}
