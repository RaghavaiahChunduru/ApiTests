package com.infogain.report;

import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;

public final class ExtentLogger {
  private ExtentLogger() {}

  public static void pass(String message) {
    ExtentReportManager.getExtent().pass(message);
  }

  public static void fail(String message) {
    ExtentReportManager.getExtent().fail(message);
  }

  public static void info(String message) {
    ExtentReportManager.getExtent().info(message);
  }

  public static void info(Markup markup) {
    ExtentReportManager.getExtent().log(com.aventstack.extentreports.Status.INFO, markup);
  }

  public static void skip(String message) {
    ExtentReportManager.getExtent().info(message);
  }

  public static void logResponse(Response response) {
    info("Response Status: " + response.getStatusCode() + " (" + response.getStatusLine() + ")");

    String responseBody = response.getBody().asString();
    if (responseBody == null || responseBody.isEmpty()) {
      info("Response Body: No content in the response.");
    } else {
      info("Response Body:");
      info(MarkupHelper.createCodeBlock(responseBody, CodeLanguage.JSON));
    }
  }

  public static void logRequest(
      RequestSpecification requestSpecification, String fullUrl, String requestBody) {
    QueryableRequestSpecification query = SpecificationQuerier.query(requestSpecification);

    info("Request URL: " + fullUrl);

    if (requestBody != null && !requestBody.isEmpty()) {
      info("Request Body:");
      info(MarkupHelper.createCodeBlock(requestBody, CodeLanguage.JSON));
    } else {
      info("Request Body: No body present in this request.");
    }
    logHeaders(query);
  }

  public static void logRequest(RequestSpecification requestSpecification, String fullUrl) {
    QueryableRequestSpecification query = SpecificationQuerier.query(requestSpecification);

    info("Request URL: " + fullUrl);
    info("Request Body: GET requests do not have a body.");

    logHeaders(query);
  }

  private static void logHeaders(QueryableRequestSpecification query) {
    if (query.getHeaders() != null && !query.getHeaders().asList().isEmpty()) {
      StringBuilder headersJson = new StringBuilder("{\n");
      query
          .getHeaders()
          .asList()
          .forEach(
              header -> {
                headersJson
                    .append("  \"")
                    .append(header.getName())
                    .append("\": \"")
                    .append(header.getValue())
                    .append("\",\n");
              });
      if (headersJson.length() > 2) {
        headersJson.setLength(headersJson.length() - 2);
      }
      headersJson.append("\n}");
      info("Request Headers:");
      info(MarkupHelper.createCodeBlock(headersJson.toString(), CodeLanguage.JSON));
    } else {
      info("Request Headers: None present.");
    }
  }
}
