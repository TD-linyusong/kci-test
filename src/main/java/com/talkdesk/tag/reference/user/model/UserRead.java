package com.talkdesk.tag.reference.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbProperty;

/**
 * API representation for the User object.
 *
 * In the Model Object, we use Lombok annotations to generate some of the required code and to keep the class clean.
 *
 * It requires a no args constructor so it can be serialized / deserialized over the wire.
 *
 * You can document the model using the MicroProfile OpenAPI annotations. Remember to follow Talkdesk API guidelines:
 * https://td-s-api-guidelines.herokuapp.com
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter(onMethod = @__({@Schema(hidden = true)}))
@Data
@Builder(toBuilder = true)

@Schema(
    description = "The User object.",
    readOnly = true)
public class UserRead {
    @Schema(
        description = "The User id."
    )
    private String id;

    @JsonbProperty("first_name")
    @Schema(
        description = "The User first name."
    )
    private String firstName;

    @JsonbProperty("last_name")
    @Schema(
        description = "The User last name."
    )
    private String lastName;

    @Schema(
        description = "The User age."
    )
    private Integer age;
}
