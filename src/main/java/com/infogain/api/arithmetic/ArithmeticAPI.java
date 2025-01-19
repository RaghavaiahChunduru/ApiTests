package com.infogain.api.arithmetic;

import static com.infogain.utils.ConfigUtil.CONFIG;
import static io.restassured.RestAssured.given;
import com.infogain.api.basespec.SpecFactory;
import com.infogain.report.ExtentLogger;
import com.infogain.utils.JsonUtil;
import groovy.util.logging.Slf4j;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Slf4j
public final class ArithmeticAPI {

  private ArithmeticAPI() {
  }

  public static ArithmeticAPI getInstance() {
    return new ArithmeticAPI();
  }

  public Response performAddition(Object operand1, Object operand2) {

    String endpoint = CONFIG.getString("ADDITION_ENDPOINT");
    String fullUrl = CONFIG.getString("ARITHMETIC_BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpecForArithmeticService();

    AdditionOperands additionOperands = AdditionOperands.builder()
        .setFirstOperand(operand1)
        .setSecondOperand(operand2)
        .build();

    String requestBody = JsonUtil.serialize(additionOperands);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody);

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

    RequestSpecification requestSpec = SpecFactory.getSpecForArithmeticService();

    DivisionOperands additionOperands = DivisionOperands.builder()
        .setFirstOperand(operand1)
        .setSecondOperand(operand2)
        .build();

    String requestBody = JsonUtil.serialize(additionOperands);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody);

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

    String endpoint = CONFIG.getString("FACTORIAL_NUM_ENDPOINT");
    String fullUrl = CONFIG.getString("ARITHMETIC_BASE_URL") + endpoint;
    fullUrl = fullUrl.replace("{operand}", operand.toString());

    RequestSpecification requestSpec = SpecFactory.getSpecForArithmeticService();

    ExtentLogger.logRequest(requestSpec, fullUrl);

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .post(endpoint, operand)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }
}