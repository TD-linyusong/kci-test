/*
 *
 *  * Talkdesk Confidential
 *  *
 *  * Copyright (C) Talkdesk Inc. 2020
 *  *
 *  * The source code for this program is not published or otherwise divested
 *  * of its trade secrets, irrespective of what has been deposited with the
 *  * U.S. Copyright Office. Unauthorized copying of this file, via any medium
 *  * is strictly prohibited.
 *
 */

package com.talkdesk.tag.reference.user.pagination;

import com.td.athena.pagination.util.SortOption;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Getter
@Schema(description = "The available options to sort the Request resource.")
public enum UserSortOption implements SortOption {

    FIRST_NAME_DESC(List.of("firstName:desc"), "first_name", "desc"),
    FIRST_NAME_ASC(List.of("firstName:asc"), "first_name", "asc"),
    LAST_NAME_ASC(List.of("lastName:asc"), "last_name", "asc");

    private final List<String> value;
    private final String columnName;
    private final String sortDirection;

    UserSortOption(final List<String> value, final String columnName, final String sortDirection) {
        this.value = value;
        this.columnName = columnName;
        this.sortDirection = sortDirection;
    }

    @Override
    public List<String> getValues() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
