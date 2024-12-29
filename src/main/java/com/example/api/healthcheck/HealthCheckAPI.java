package com.example.api.healthcheck;

import static io.restassured.RestAssured.given;
import static com.example.api.auth.Scope.GUEST;
import io.restassured.response.Response;
import com.example.api.basespec.SpecFactory;
import com.example.report.ExtentLogger;
import static com.example.utils.ConfigUtil.CONFIG;

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
