package com.infogain.api.basespec;

import static com.infogain.utils.ConfigUtil.CONFIG;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import java.util.Base64;

public class SpecFactory {

  public static RequestSpecification getSpec() {

    String username = CONFIG.getString("USERNAME");
    String password = CONFIG.getString("PASSWORD");

    String authValue = "Basic " +
        Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

    return new RequestSpecBuilder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", authValue)
        .setBaseUri(CONFIG.getString("BASE_URL"))
        .setConfig(RestAssuredConfig.config()
            .logConfig(LogConfig.logConfig().blacklistHeaders(Arrays.asList("Authorization"))))
        .build();
  }
}
