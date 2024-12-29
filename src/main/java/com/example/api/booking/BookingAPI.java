package com.example.api.booking;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.example.api.auth.Scope;
import com.example.api.basespec.SpecFactory;
import com.example.report.ExtentLogger;
import com.example.utils.JsonUtil;

import static com.example.utils.ConfigUtil.CONFIG;

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
