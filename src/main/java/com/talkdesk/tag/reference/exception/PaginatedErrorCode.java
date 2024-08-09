package com.talkdesk.tag.reference.exception;

import com.talkdesk.tag.reference.user.validation.ServiceErrorCode;
import com.td.athena.commons.errorhandling.ErrorCode;
import com.td.athena.pagination.validation.PaginatedResourceErrorCode;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaginatedErrorCode implements PaginatedResourceErrorCode {
    @Override
    public ErrorCode getNegativePageIndexErrorCode() {
        return ServiceErrorCode.INVALID_PAGE_VALUE;
    }

    @Override
    public ErrorCode getNegativePerPageErrorCode() {
        return ServiceErrorCode.INVALID_PER_PAGE_VALUE;
    }
}
