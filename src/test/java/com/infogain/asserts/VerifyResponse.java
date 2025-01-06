package com.infogain.asserts;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.machinezoo.noexception.Exceptions;

public abstract class VerifyResponse<SELF_TYPE extends VerifyResponse<SELF_TYPE>> {

  protected Response response;
  protected SoftAssertions softAssertions;

  // Constructor for initializing with response and soft assertions
  protected VerifyResponse(Response response) {
    this.response = response;
    this.softAssertions = new SoftAssertions();
  }

  // Factory method for creating the appropriate VerifyResponse instance
  public static <SELF_TYPE extends VerifyResponse<SELF_TYPE>> SELF_TYPE assertThat(Response response,
      Class<SELF_TYPE> clazz) {
    try {
      return clazz.getDeclaredConstructor(Response.class).newInstance(response); // Create instance using reflection
    } catch (Exception e) {
      throw new RuntimeException("Unable to create VerifyResponse instance", e);
    }
  }

  public SELF_TYPE statusCodeIs(int statusCode) {
    Assertions.assertThat(response.getStatusCode()).describedAs("statusCode").isEqualTo(statusCode);
    return selfType();
  }

  public SELF_TYPE responseTimeBelow(int timeInMilliSeconds) {
    Assertions.assertThat(response.timeIn(TimeUnit.MILLISECONDS))
        .describedAs("response")
        .isLessThan(timeInMilliSeconds);
    return selfType();
  }

  public SELF_TYPE contentTypeIs(ContentType contentType) {
    softAssertions.assertThat(response.getContentType())
        .as("Content Type validation")
        .isEqualTo(contentType.toString());
    return selfType();
  }

  public SELF_TYPE matchingRule(Predicate<Response> condition, String errorMessage) {
    softAssertions.assertThat(condition).withFailMessage(errorMessage).accepts(response);
    return selfType();
  }

  public SELF_TYPE hasKeyWithValue(String key, String expectedValue) {
    String actualValue = response.jsonPath().getString(key);
    softAssertions.assertThat(actualValue)
        .as("body node validation in response")
        .isEqualTo(expectedValue);
    return selfType();
  }

  public SELF_TYPE containsValue(String value) {
    softAssertions.assertThat(response.getBody().asString())
        .as("Response should contains the value: " + value)
        .contains(value);
    return selfType();
  }

  public SELF_TYPE doesNotContainsValue(String value) {
    softAssertions.assertThat(response.getBody().asString())
        .as("Response should not contains the value: " + value)
        .doesNotContain(value);
    return selfType();
  }

  public SELF_TYPE containsKey(String key) {
    Exceptions.wrap(e -> new RuntimeException("Failed to validate key in JSON response", e))
        .run(() -> {
          ObjectMapper mapper = new ObjectMapper();
          JsonNode responseBody = mapper.readTree(response.body().asString());

          // Perform assertion
          softAssertions.assertThat(responseBody.has(key))
              .as("Response should contain the key: " + key)
              .isTrue();
        });

    return selfType();
  }

  public SELF_TYPE doesNotContainKey(String key) {
    Exceptions.wrap(e -> new RuntimeException("Failed to validate absence of key in JSON response", e))
        .run(() -> {
          ObjectMapper mapper = new ObjectMapper();
          JsonNode responseBody = mapper.readTree(response.body().asString());

          // Perform assertion
          softAssertions.assertThat(responseBody.has(key))
              .as("Response should not contain the key: " + key)
              .isFalse();
        });

    return selfType();
  }

  public SELF_TYPE matchesSchema(String fileClassPath) {
    softAssertions.assertThat(response.then().body(matchesJsonSchemaInClasspath(fileClassPath)))
        .describedAs("Schema validation")
        .getWritableAssertionInfo();
    return selfType();
  }

  public void assertAll() {
    softAssertions.assertAll();
  }

  // Return self type
  protected SELF_TYPE selfType() {
    return (SELF_TYPE) this;
  }
}
