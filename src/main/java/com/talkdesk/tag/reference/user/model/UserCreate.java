package com.talkdesk.tag.reference.user.model;

import com.talkdesk.tag.reference.user.validation.BusinessChecks;
import com.talkdesk.tag.reference.user.validation.UserAlreadyExists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * API representation to create an User object.
 *
 * In the Model Object, we use Lombok annotations to generate some of the required code and to keep the class clean.
 *
 * It requires a no args constructor so it can be serialized / deserialized over the wire.
 *
 * Notice the Bean Validation annotations at each field to validate the intended behaviour of the field.
 *
 * A Custom Bean Validation is also included, to validate the uniqueness of the User name. This is given by the
 * {@link UserAlreadyExists} annotation. This will instruct Bean Validation to run the Custom Validator
 * {@link com.talkdesk.tag.reference.user.validation.UserAlreadyExists.UserAlreadyExistsValidator}. Also, note the @GroupSequence annotation.
 * This is used to make sure that validations are perfomed by steps. This means that first, validations on the model
 * will be performed and it will only proceed to the next Validation Group if the model is valid. This is required
 * because these validations have dependencies. We cannot validate if the User already exists if the firstName or
 * lastName is null.
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
@Builder

@UserAlreadyExists(groups = BusinessChecks.class)
@GroupSequence({UserCreate.class, BusinessChecks.class})

@Schema(
    description = "The User object to create.")
public class UserCreate {
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
