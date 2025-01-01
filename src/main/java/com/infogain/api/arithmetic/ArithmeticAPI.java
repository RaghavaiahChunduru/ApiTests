package com.infogain.api.arithmetic;

import static com.infogain.utils.ConfigUtil.CONFIG;
import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;
import com.infogain.api.basespec.SpecFactory;
import com.infogain.report.ExtentLogger;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class ArithmeticAPI {

  private ArithmeticAPI() {
  }

  public static ArithmeticAPI getInstance() {
    return new ArithmeticAPI();
  }

  public Response performAddition(Object operand1, Object operand2) {

    String endpoint = CONFIG.getString("ADDITION_ENDPOINT");
    String fullUrl = CONFIG.getString("ARITHMETIC_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("firstOperand", operand1);
    requestBody.put("secondOperand", operand2);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody.toString());

    return given()
        .spec(requestSpec)
        .body(requestBody)
        .log()
        .ifValidationFails()
        .when()
        .post(fullUrl)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response performDivision(Object operand1, Object operand2) {

    String endpoint = CONFIG.getString("DIVISION_ENDPOINT");
    String fullUrl = CONFIG.getString("ARITHMETIC_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("firstOperand", operand1);
    requestBody.put("secondOperand", operand2);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody.toString());

    return given()
        .spec(requestSpec)
        .body(requestBody)
        .log()
        .ifValidationFails()
        .when()
        .post(fullUrl)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response getFactorial(Object operand) {

    String endpoint = CONFIG.getString("FACTORIAL_ENDPOINT");
    String fullUrl = CONFIG.getString("ARITHMETIC_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpec();

    ExtentLogger.logRequest(requestSpec, fullUrl);

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .get(endpoint, operand)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }
}