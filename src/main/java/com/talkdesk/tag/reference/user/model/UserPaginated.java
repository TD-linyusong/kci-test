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

package com.talkdesk.tag.reference.user.model;

import com.td.athena.pagination.data.LinksPageItems;
import com.td.athena.pagination.data.ListItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter(onMethod_ = {@Schema(hidden = true)})
@SuperBuilder(toBuilder = true)
@Schema(description = "List with the paginated samples.",
        readOnly = true)
public class UserPaginated extends LinksPageItems<UserPaginated.Users> {

    @JsonbProperty("_embedded")
    private Users embeddedItems;

    @Data
    @Getter(onMethod_ = {@Schema(hidden = true)})
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Users implements ListItem<UserRead> {

        @JsonbProperty("users")
        @Schema(description = "List of the associated resources paginated.")
        private List<UserRead> items;
    }
}
