package com.talkdesk.tag.reference.user.resource;

import com.talkdesk.tag.reference.common.JsonbObjectMapper;
import com.talkdesk.tag.reference.user.mapper.UserMapper;
import com.talkdesk.tag.reference.user.model.UserCreate;
import com.talkdesk.tag.reference.user.model.UserPaginated;
import com.talkdesk.tag.reference.user.model.UserRead;
import com.talkdesk.tag.reference.user.validation.ServiceErrorCode;
import com.td.athena.commons.errorhandling.ErrorPayload;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Header;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;

import javax.inject.Inject;

import static com.talkdesk.tag.reference.TokenUtils.generateTokenStringForAccount;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@Slf4j
class UserResourceIT {

    @Inject
    UserMapper userMapper;

    @Inject
    Flyway flyway;

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
    void listPaginated() throws Exception {
        UserPaginated userPaginated = given()
                .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
                .when()
                .queryParam("page", 1)
                .queryParam("per_page", 2)
                .get("/users")
                .then()
                .statusCode(OK.getStatusCode()).extract().body().as(UserPaginated.class);
        Assert.equals(1, userPaginated.getPage());
        Assert.equals(2, userPaginated.getEmbeddedItems().getItems().size());
        Assert.equals(2, userPaginated.getCount());
        Assert.equals(6, userPaginated.getTotal());
        Assert.equals(3, userPaginated.getTotalPages());

        userPaginated = given()
                .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
                .when()
                .queryParam("page", 2)
                .queryParam("per_page", 2)
                .get("/users")
                .then()
                .statusCode(OK.getStatusCode()).extract().body().as(UserPaginated.class);

        Assert.equals(2, userPaginated.getPage());
        Assert.equals(2, userPaginated.getEmbeddedItems().getItems().size());
        Assert.equals(2, userPaginated.getCount());
        Assert.equals(6, userPaginated.getTotal());
        Assert.equals(3, userPaginated.getTotalPages());
    }

    @Test
    void errorCodesListPaginated() throws Exception {
        ErrorPayload errorPayload = given()
                .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
                .when()
                .queryParam("page", -1)
                .queryParam("per_page", 1)
                .get("/users")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorPayload.class);

        assertEquals("Page query parameter must be positive.", errorPayload.getMessage());
        assertEquals("invalid-page-value-0001", errorPayload.getCode());

        errorPayload = given()
                .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
                .when()
                .queryParam("page", 1)
                .queryParam("per_page", -1)
                .get("/users")
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorPayload.class);

        assertEquals("Per_page query parameter must be positive.", errorPayload.getMessage());
        assertEquals("invalid-per-page-value-0001", errorPayload.getCode());
    }

    @Test
    void listPaginatedSortedByLastName() throws Exception {
        final UserPaginated userPaginated = given()
                .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
                .when()
                .queryParam("page", 1)
                .queryParam("per_page", 6)
                .queryParam("order_by", "lastName:asc")
                .get("/users")
                .then()
                .statusCode(OK.getStatusCode()).extract().body().as(UserPaginated.class);
            Assert.equals(1, userPaginated.getPage());
        Assert.equals(6, userPaginated.getEmbeddedItems().getItems().size());
        Assert.equals(6, userPaginated.getCount());
        Assert.equals(6, userPaginated.getTotal());
        Assert.equals(1, userPaginated.getTotalPages());
        final UserRead user = userPaginated.getEmbeddedItems().getItems().get(0);
        Assert.equals("Bob", user.getFirstName());
    }

    @Test
    void create() throws Exception {
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
            .statusCode(CREATED.getStatusCode());
    }

    @Test
    void createInvalid() throws Exception {
        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .post("/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body("code", equalTo(ServiceErrorCode.INVALID_FIELDS.getCode()));

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .body(new UserCreate())
            .post("/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body("message", equalTo("Constraint violation(s) occurred during input validation."))
            .body("fields", hasItem(allOf(hasEntry(equalTo("name"), containsString("firstName")),
                                          hasEntry(equalTo("description"), containsString("must not be blank")))))
            .body("fields", hasItem(allOf(hasEntry(equalTo("name"), containsString("lastName")),
                                          hasEntry(equalTo("description"), containsString("must not be blank")))))
            .body("fields", hasItem(allOf(hasEntry(equalTo("name"), containsString("age")),
                                          hasEntry(equalTo("description"), containsString("must not be null")))));
    }

    @Test
    void createAlreadyExists() throws Exception {
        final UserCreate userCreate =
            UserCreate.builder()
                      .firstName("Naruto")
                      .lastName("Uzumaki")
                      .age(17)
                      .build();

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .body(userCreate)
            .post("/users")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode())
            .body("message", equalTo("Constraint violation(s) occurred during input validation."))
            .body("fields", hasItem(allOf(
                hasEntry(equalTo("name"), anything()),
                hasEntry(equalTo("description"), equalTo("User Naruto Uzumaki already exists.")))));
    }

    @Test
    void update() throws Exception {
        final UserCreate create =
            UserCreate.builder()
                      .firstName("Shawn")
                      .lastName("The Sheep")
                      .age(12)
                      .build();

        final UserRead created = given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .body(create)
            .post("/users")
            .then()
            .statusCode(CREATED.getStatusCode())
            .body(not(emptyString()))
            .extract().as(UserRead.class);

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .body(userMapper.toUserUpdate(created).toBuilder().age(17).build())
            .put("/users/{user_id}", created.getId())
            .then()
            .statusCode(OK.getStatusCode());

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .get("/users/{user_id}", created.getId())
            .then()
            .statusCode(OK.getStatusCode())
            .body("first_name", equalTo("Shawn"))
            .body("last_name", equalTo("The Sheep"))
            .body("age", equalTo(17));
    }

    @Test
    void delete() throws Exception {
        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .delete("/users/{user_id}", 3)
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        given()
            .auth().oauth2(generateTokenStringForAccount("5c938b75ba67ef0008bbfe2a"))
            .when()
            .get("/users/{user_id}", 3)
            .then()
            .statusCode(NOT_FOUND.getStatusCode());
    }
}
