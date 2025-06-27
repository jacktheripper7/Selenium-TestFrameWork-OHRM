package com.orangehrm.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class APIUtility {

    //Method to send GET request
    public static Response sendGetRequest(String endpoint) {
        return RestAssured.get(endpoint);
    }

    public static Response sendPostRequest(String endpoint, String requestBody) {
        return RestAssured.given().header("Content-Type", "application/json")
                .body(requestBody)
                .post(endpoint);
    }

    public static Response sendPutRequest(String endpoint, String requestBody) {
        return RestAssured.given().header("Content-Type", "application/json")
                .body(requestBody)
                .put(endpoint);
    }

    //Method to validate response status code
    public static boolean validateResponseStatusCode(Response response, int expectedStatusCode) {
        return response.getStatusCode() == expectedStatusCode;
    }

    //Method to extract a JSON response value
    public static String extractJsonResponse(Response response, String value) {
        return response.jsonPath().getString(value);
    }
}
