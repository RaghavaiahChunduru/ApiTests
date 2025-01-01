package com.infogain.api.usermanagement;

import static com.infogain.utils.ConfigUtil.CONFIG;
import static io.restassured.RestAssured.given;

import com.infogain.api.basespec.SpecFactory;
import com.infogain.report.ExtentLogger;
import com.infogain.utils.JsonUtil;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class UserManagementAPI {

  private UserManagementAPI() {
  }

  public static UserManagementAPI getInstance() {
    return new UserManagementAPI();
  }

  public Response newUser(User user) {

    String endpoint = CONFIG.getString("USER_ENDPOINT");
    RequestSpecification requestSpec = SpecFactory.getSpec();

    return given()
        .spec(requestSpec)
        .body(user)
        .log()
        .ifValidationFails()
        .when()
        .post(endpoint)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response getUser(Long userId) {
    String endpoint = CONFIG.getString("USER_ID_ENDPOINT");
    String fullUrl = CONFIG.getString("USER_MANAGEMENT_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    ExtentLogger.logRequest(requestSpec, fullUrl);

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .get(endpoint, userId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response updateUser(User user, Long userId) {
    String endpoint = CONFIG.getString("USER_ID_ENDPOINT");
    String fullUrl = CONFIG.getString("USER_MANAGEMENT_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    String requestBody = JsonUtil.serialize(user);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody);

    return given()
        .spec(requestSpec)
        .body(user)
        .log()
        .ifValidationFails()
        .when()
        .put(endpoint, userId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response patchUser(User user, Long userId) {
    String endpoint = CONFIG.getString("USER_ID_ENDPOINT");
    String fullUrl = CONFIG.getString("USER_MANAGEMENT_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    String requestBody = JsonUtil.serialize(user);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody);

    return given()
        .spec(requestSpec)
        .body(user)
        .log()
        .ifValidationFails()
        .when()
        .patch(endpoint, userId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response deleteUser(Long userId) {
    String endpoint = CONFIG.getString("USER_ID_ENDPOINT");
    RequestSpecification requestSpec = SpecFactory.getSpec();

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .delete(endpoint, userId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }
}
