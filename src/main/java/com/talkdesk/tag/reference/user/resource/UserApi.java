package com.talkdesk.tag.reference.user.resource;

import com.talkdesk.tag.reference.RestApplication;
import com.talkdesk.tag.reference.user.entity.User;
import com.talkdesk.tag.reference.user.mapper.UserMapper;
import com.talkdesk.tag.reference.user.model.UserCreate;
import com.talkdesk.tag.reference.user.model.UserRead;
import com.talkdesk.tag.reference.user.model.UserUpdate;
import com.td.athena.commons.errorhandling.ErrorPayload;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.STRING;

/**
 * Use an interface to add all of your REST and OpenAPI annotations. Since the annotation can become verbose, this will
 * keep the business code clean from all the distraction of the annotations.
 *
 * This particial REST API, provides a CRUD (Create, Read, Update, Delete) for a particular object. Notice that the
 * object type is not the same that is stored in the database. This is to create a separation between the database
 * layer and the API layer, since both model may not match.
 *
 * Also, separate objects are used for each operation type. This allows to fine tune the object representation and
 * validations. For instance, the id is useful in the GET operation, but we cannot set it in the POST operation, so it
 * doesn't make sense to expose the id property in the POST. This is heavily inspired by the Amazon API.
 *
 * Objects are mapped between them using the MapStruct library.
 *
 * You can document the model using the MicroProfile OpenAPI annotations. Remember to follow Talkdesk API guidelines:
 * https://td-s-api-guidelines.herokuapp.com
 *
 * For OpenAPI documentation, we try to reuse the components definitions when possible. For instance, API Responses for
 * Not Found, Unauthorized, Forbidden or Internal Error are always the same regardless of the endpoint. This allows you
 * to keep the API Interface clean.
 *
 * @see User
 * @see UserRead
 * @see UserCreate
 * @see UserUpdate
 * @see UserMapper
 * @see RestApplication
 */
@Path("/users")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Reference-App")
public interface UserApi {
    @GET
    @Path("/{user_id}")
    @Operation(
        operationId = "GetUser",
        summary = "Find User by Id"
    )
    @APIResponse(responseCode = "200",
                 description = "The user",
                 content = @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = User.class, ref = "user")))
    @APIResponse(
        name = "unauthorized",
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "forbidden",
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "notFound",
        responseCode = "404",
        description = "EventType not found",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "internalError",
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    Response get(
        @Parameter(name = "user_id",
                   description = "Id of the User that needs to be fetched",
                   required = true,
                   example = "81471222-5798-11e9-ae24-57fa13b361e1",
                   schema = @Schema(description = "uuid", required = true))
        @PathParam("user_id") String id);

    @POST
    @Operation(
        operationId = "CreateUser",
        summary = "Create a new User"
    )
    @RequestBody(content = @Content(
        mediaType = APPLICATION_JSON,
        schema = @Schema(implementation = UserCreate.class, ref = "userCreate")),
                 description = "The EventType to create",
                 required = true)
    @APIResponse(
        responseCode = "201",
        description = "The payload was accepted by the server and stored in the DB successfully",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = UserRead.class, ref = "user")),
        headers = @Header(
            name = "Location",
            description = "Information about the location of a newly created resource",
            schema = @Schema(implementation = String.class))
    )
    @APIResponse(
        name = "badRequest",
        responseCode = "400",
        description = "Validation errors were found in the submitted data.",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "unauthorized",
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "forbidden",
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "internalError",
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    Response create(
        @Valid
        @NotNull UserCreate userCreate);

    @PUT
    @Path("/{user_id}")
    @Operation(
        operationId = "UpdateUser",
        summary = "Update an existent User"
    )
    @RequestBody(
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = UserUpdate.class, ref = "userUpdate")),
        description = "The existing user to update",
        required = true
    )
    @APIResponse(responseCode = "200",
                 description = "The Updated EventType",
                 content = @Content(mediaType = APPLICATION_JSON,
                                    schema = @Schema(implementation = UserRead.class, ref = "user"))
    )
    @APIResponse(
        name = "badRequest",
        responseCode = "400",
        description = "Validation errors were found in the submitted data.",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "unauthorized",
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "notFound",
        responseCode = "404",
        description = "EventType not found",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "forbidden",
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "internalError",
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    Response update(
        @Parameter(name = "user_id",
                   description = "Id of the EventType that needs to be updated",
                   required = true,
                   example = "81471222-5798-11e9-ae24-57fa13b361e1",
                   schema = @Schema(description = "UUId", required = true))
        @PathParam("user_id") String id,
        @Valid
        @NotNull UserUpdate userUpdate);

    @DELETE
    @Path("/{user_id}")
    @Operation(
        operationId = "DeleteUser",
        summary = "Delete an existent User"
    )
    @APIResponse(responseCode = "204", description = "User successfully deleted")
    @APIResponse(
        name = "unauthorized",
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "forbidden",
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "notFound",
        responseCode = "404",
        description = "EventType not found",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
        name = "internalError",
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
            mediaType = APPLICATION_JSON,
            schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    Response delete(
        @PathParam("user_id")
        @Parameter(name = "user_id", schema = @Schema(type = STRING))
            String id);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            operationId = "ListUser",
            summary = "List Users"
    )
    @APIResponse(responseCode = "200", description = "Users successfully returned")
    @APIResponse(
            name = "unauthorized",
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
            name = "forbidden",
            responseCode = "403",
            description = "Forbidden",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
            name = "notFound",
            responseCode = "404",
            description = "EventType not found",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    @APIResponse(
            name = "internalError",
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(
                    mediaType = APPLICATION_JSON,
                    schema = @Schema(implementation = ErrorPayload.class, ref = "error"))
    )
    Response listPaginated(@QueryParam("page") @DefaultValue("1") Integer page,
                           @QueryParam("per_page") Integer perPage,
                           @QueryParam("order_by") List<String> orderBy);
}
