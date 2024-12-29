package com.infogain.api.healthcheck;

import static com.infogain.api.auth.Scope.GUEST;
import static com.infogain.utils.ConfigUtil.CONFIG;
import static io.restassured.RestAssured.given;

import com.infogain.api.basespec.SpecFactory;
import com.infogain.report.ExtentLogger;
import io.restassured.response.Response;

public class HealthCheckAPI {

  public static Response healthCheck() {

    String endpoint = CONFIG.getString("HEALTHCHECK_ENDPOINT");
    String fullUrl = CONFIG.getString("BASE_URL") + endpoint;

    var requestSpec = SpecFactory.getSpecFor(GUEST);

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
