package com.infogain.api.healthcheck;

import static com.infogain.utils.ConfigUtil.CONFIG;
import static io.restassured.RestAssured.given;

import com.infogain.api.basespec.SpecFactory;
import com.infogain.report.ExtentLogger;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class HealthCheckAPI {

  public static Response healthCheckForUserManagementApi() {

    String endpoint = CONFIG.getString("USER_MANAGEMENT_HEALTHCHECK_ENDPOINT");
    String fullUrl = CONFIG.getString("USER_MANAGEMENT_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    ExtentLogger.logRequest(requestSpec, fullUrl);

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .get(endpoint)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public static Response healthCheckForArithmeticApi() {

    String endpoint = CONFIG.getString("ARITHMETIC_HEALTHCHECK_ENDPOINT");
    String fullUrl = CONFIG.getString("ARITHMETIC_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    ExtentLogger.logRequest(requestSpec, fullUrl);

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .get(endpoint)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }
}
