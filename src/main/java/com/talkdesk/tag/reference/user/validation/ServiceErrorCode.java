package com.talkdesk.tag.reference.user.validation;

import com.td.athena.commons.errorhandling.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum ServiceErrorCode implements ErrorCode {

    INVALID_FIELDS("invalid-fields-0001", "Invalid fields.", BAD_REQUEST.getStatusCode()),
    INVALID_PER_PAGE_VALUE("invalid-per-page-value-0001", "Per_page query parameter must be positive.", BAD_REQUEST.getStatusCode()),
    INVALID_PAGE_VALUE("invalid-page-value-0001", "Page query parameter must be positive.", BAD_REQUEST.getStatusCode()),
    FORBIDDEN_OPERATION("auth-0001", "The user does not have permissions to execute this request.",
            FORBIDDEN.getStatusCode());

    private final String code;
    private final String message;
    private final int httpStatusCode;
}
