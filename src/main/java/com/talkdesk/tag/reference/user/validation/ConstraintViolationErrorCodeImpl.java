package com.talkdesk.tag.reference.user.validation;

import com.td.athena.commons.errorhandling.ConstraintViolationErrorCode;
import com.td.athena.commons.errorhandling.ErrorCode;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import static com.talkdesk.tag.reference.user.validation.ServiceErrorCode.INVALID_FIELDS;

@ApplicationScoped
@Priority(1000)
@Alternative
public class ConstraintViolationErrorCodeImpl implements ConstraintViolationErrorCode {

    @Override
    public ErrorCode getInvalidFieldErrorCode() {
        return INVALID_FIELDS;
    }
}
