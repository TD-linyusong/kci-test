package com.talkdesk.tag.reference.user.validation;

import com.talkdesk.tag.reference.user.model.UserCreate;
import com.talkdesk.tag.reference.user.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.talkdesk.tag.reference.user.validation.UserAlreadyExists.UserAlreadyExistsValidator;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = UserAlreadyExistsValidator.class)
@Documented
public @interface UserAlreadyExists {
    String message() default "{validation.user.alreadyExists}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @ApplicationScoped
    class UserAlreadyExistsValidator implements ConstraintValidator<UserAlreadyExists, UserCreate> {
        @Inject
        UserRepository userRepository;

        @Override
        public boolean isValid(final UserCreate user, final ConstraintValidatorContext context) {
            return userRepository.findByFirstNameAndLastName(user.getFirstName(), user.getLastName())
                                 .isEmpty();
        }
    }
}
