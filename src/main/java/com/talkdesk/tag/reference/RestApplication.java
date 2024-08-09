package com.talkdesk.tag.reference;

import com.talkdesk.tag.reference.user.model.UserCreate;
import com.talkdesk.tag.reference.user.model.UserRead;
import com.talkdesk.tag.reference.user.model.UserUpdate;
import com.td.athena.commons.errorhandling.ErrorPayload;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.microprofile.openapi.annotations.enums.ParameterIn.PATH;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.STRING;

/**
 * JAX-RS Application additional metadata.
 *
 * This class is used to define common elements for OpenAPI documentation using the OpenAPIDefinition annotation. This
 * allows you to reuse the components in the actual REST endpoints by name reference.
 *
 * On their own, these common components do not have any effect.
 */
@ApplicationPath("/")
@OpenAPIDefinition(
    info = @Info(
        title = "Java Reference Application",
        version = "1.0"
    ),
    components = @Components(
        parameters = {
            @Parameter(
                name = "userId",
                description = "Id of the User to perform the operation",
                required = true,
                example = "81471222-5798-11e9-ae24-57fa13b361e1",
                in = PATH,
                schema = @Schema(type = STRING)
            )
        },
        requestBodies = {
            @RequestBody(
                name = "userCreate",
                description = "The User to create",
                required = true,
                content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "userCreate"))
            ),
            @RequestBody(
                name = "userUpdate",
                description = "The User to update",
                required = true,
                content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "userUpdate"))
            )
        },
        responses = {
            @APIResponse(
                name = "unauthorized",
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "error"))
            ),
            @APIResponse(
                name = "forbidden",
                responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "error"))
            ),
            @APIResponse(
                name = "notFound",
                responseCode = "404",
                description = "Object Not found",
                content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "error"))
            ),
            @APIResponse(
                name = "internalError",
                responseCode = "500",
                description = "Internal Server Error",
                content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "error"))
            )
        },
        schemas = {
            @Schema(name = "error", implementation = ErrorPayload.class),
            @Schema(name = "user", implementation = UserRead.class),
            @Schema(name = "userCreate", implementation = UserCreate.class),
            @Schema(name = "userUpdate", implementation = UserUpdate.class),
        }
    )
)
public class RestApplication extends Application {

}
