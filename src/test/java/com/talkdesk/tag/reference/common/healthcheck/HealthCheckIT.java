package com.talkdesk.tag.reference.common.healthcheck;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.tdx.wiremock.talkdesk.api.WireMockTalkdeskApi;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class HealthCheckIT {

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void init() {
        wireMockServer = new WireMockServer(WireMockTalkdeskApi.config().port(9000));
        wireMockServer.start();
    }

    @AfterAll
    public static void finish() {
        wireMockServer.stop();
    }

    @Test
    void checkServiceHealthStatus() {
        given()
            .get("/q/health")
            .then()
            .log().all()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.size()", equalTo(12))
            .body("checks.name", Matchers.hasItems("Database connections health check", "database-ready",
                                                   "Version Deployed", "java-reference-implementation Live"));

        given()
            .get("/q/health/live")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));

        given()
            .get("/q/health/ready")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));

    }
}
