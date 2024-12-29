package com.infogain.api.booking;

import static com.infogain.utils.ConfigUtil.CONFIG;
import static io.restassured.RestAssured.given;

import com.infogain.api.auth.Scope;
import com.infogain.api.basespec.SpecFactory;
import com.infogain.report.ExtentLogger;
import com.infogain.utils.JsonUtil;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BookingAPI {
  private Scope scope;

  private BookingAPI(Scope scope) {
    this.scope = scope;
  }

  public static BookingAPI useAs(Scope scope) {
    return new BookingAPI(scope);
  }

  public Response newBooking(Booking booking) {
    String endpoint = CONFIG.getString("BOOKING_ENDPOINT");
    RequestSpecification requestSpec = SpecFactory.getSpecFor(scope);

    return given()
        .spec(requestSpec)
        .body(booking)
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

  public Response getBooking(Long bookingId) {
    String endpoint = CONFIG.getString("BOOKING_ID_ENDPOINT");
    String fullUrl = CONFIG.getString("BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpecFor(scope);

    ExtentLogger.logRequest(requestSpec, fullUrl);

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .get(endpoint, bookingId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response updateBooking(Booking booking, Long bookingId) {
    String endpoint = CONFIG.getString("BOOKING_ID_ENDPOINT");
    String fullUrl = CONFIG.getString("BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpecFor(scope);

    String requestBody = JsonUtil.serialize(booking);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody);

    return given()
        .spec(requestSpec)
        .body(booking)
        .log()
        .ifValidationFails()
        .when()
        .put(endpoint, bookingId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response patchBooking(Booking booking, Long bookingId) {
    String endpoint = CONFIG.getString("BOOKING_ID_ENDPOINT");
    String fullUrl = CONFIG.getString("BASE_URL") + endpoint;

    RequestSpecification requestSpec = SpecFactory.getSpecFor(scope);

    String requestBody = JsonUtil.serialize(booking);

    ExtentLogger.logRequest(requestSpec, fullUrl, requestBody);

    return given()
        .spec(requestSpec)
        .body(booking)
        .log()
        .ifValidationFails()
        .when()
        .patch(endpoint, bookingId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }

  public Response deleteBooking(Long bookingId) {
    String endpoint = CONFIG.getString("BOOKING_ID_ENDPOINT");
    RequestSpecification requestSpec = SpecFactory.getSpecFor(scope);

    return given()
        .spec(requestSpec)
        .log()
        .ifValidationFails()
        .when()
        .delete(endpoint, bookingId)
        .then()
        .log()
        .ifError()
        .extract()
        .response();
  }
}
