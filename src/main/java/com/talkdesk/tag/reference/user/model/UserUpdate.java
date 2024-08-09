package com.talkdesk.tag.reference.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * API representation to update an User object.
 *
 * In the Model Object, we use Lombok annotations to generate some of the required code and to keep the class clean.
 *
 * It requires a no args constructor so it can be serialized / deserialized over the wire.
 *
 * Notice the Bean Validation annotations at each field to validate the intended behaviour of the field.
 *
 * You can document the model using the MicroProfile OpenAPI annotations. Remember to follow Talkdesk API guidelines:
 * https://td-s-api-guidelines.herokuapp.com
 *
 * Unfortunately, there is an overlap between the Bean Validation annotatons and the OpenAPI ones, so you do need to
 * repeat the same information using different annotations.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)

@Schema(
    description = "The User object to update.")
public class UserUpdate {
    @NotBlank
    @Size(max = 40)
    @JsonbProperty("first_name")
    @Schema(
        description = "The User first name.",
        required = true,
        maxLength = 40
    )
    private String firstName;

    @NotBlank
    @Size(max = 40)
    @JsonbProperty("last_name")
    @Schema(
        description = "The User last name.",
        required = true,
        maxLength = 40
    )
    private String lastName;

    @NotNull
    @PositiveOrZero
    @Schema(
        description = "The User age.",
        required = true,
        minimum = "0"
    )
    private Integer age;

}
