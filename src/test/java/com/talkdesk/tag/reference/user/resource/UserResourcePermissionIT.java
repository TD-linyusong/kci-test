/*
 * Talkdesk Confidential
 *
 * Copyright (C) Talkdesk Inc. 2020
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office. Unauthorized copying of this file, via any medium
 * is strictly prohibited.
 */

package com.talkdesk.tag.reference.user.resource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.talkdesk.tag.reference.common.JsonbObjectMapper;
import com.talkdesk.tag.reference.user.model.UserCreate;
import com.talkdesk.tag.reference.user.model.UserUpdate;
import com.tdx.wiremock.talkdesk.api.WireMockTalkdeskApi;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Header;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static com.talkdesk.tag.reference.TokenUtils.generateTokenStringForAccount;
import static com.talkdesk.tag.reference.common.PoliciesHelper.stubPoliciesEvaluateEndpoint;
import static com.talkdesk.tag.reference.common.TestPermissionsEndpointPoliciesRetriever.callPoliciesEndpoint;
import static com.talkdesk.tag.reference.common.TestPermissionsEndpointPoliciesRetriever.useDefaultPoliciesForTests;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;

@QuarkusTest
@Slf4j
class UserResourcePermissionIT {

    @Inject
    Flyway flyway;

    private final WireMockServer wireMockServer = new WireMockServer(WireMockTalkdeskApi.config().port(9123));

    static {
        RestAssured.filters(
            (requestSpec, responseSpec, ctx) -> {
                requestSpec.header(new Header(CONTENT_TYPE, APPLICATION_JSON));
                requestSpec.header(new Header(ACCEPT, APPLICATION_JSON));
                return ctx.next(requestSpec, responseSpec);
            },
            new RequestLoggingFilter(),
            new ResponseLoggingFilter());

        RestAssured.config = RestAssured.config().objectMapperConfig(new ObjectMapperConfig(new JsonbObjectMapper()));
    }

    @BeforeEach
    void beforeEach() {
        flyway.clean();
        flyway.migrate();
        useDefaultPoliciesForTests();
        wireMockServer.start();
    }

    @AfterEach
    void after() {
        wireMockServer.stop();
    }

    @Test
    void get() throws Exception {
        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .get("/users/{user_id}", 1)
            .then()
            .statusCode(OK.getStatusCode());
    }

    @Test
    void getWithoutPolicies() throws Exception {
        callPoliciesEndpoint();
        stubPoliciesEvaluateEndpoint(wireMockServer, List.of("another.policy"));

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .get("/users/{user_id}", 1)
            .then()
            .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void createWithoutPolicies() throws Exception {
        callPoliciesEndpoint();
        stubPoliciesEvaluateEndpoint(wireMockServer, List.of("another.policy"));

        final UserCreate userCreate =
            UserCreate.builder()
                      .firstName("Sasuke")
                      .lastName("Uchiha")
                      .age(17)
                      .build();

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .body(userCreate)
            .post("/users")
            .then()
            .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void updateWithoutPolicies() throws Exception {
        callPoliciesEndpoint();
        stubPoliciesEvaluateEndpoint(wireMockServer, List.of("another.policy"));


        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .body(UserUpdate.builder().firstName("foo").lastName("bar").age(12).build())
            .put("/users/{user_id}", 1)
            .then()
            .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void deleteWithoutPolicies() throws Exception {
        callPoliciesEndpoint();
        stubPoliciesEvaluateEndpoint(wireMockServer, List.of("another.policy"));

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .delete("/users/{user_id}", 3)
            .then()
            .statusCode(FORBIDDEN.getStatusCode());
    }
}
