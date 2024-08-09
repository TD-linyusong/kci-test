/*
 * Talkdesk Confidential
 *
 * Copyright (C) Talkdesk Inc. 2020
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office. Unauthorized copying of this file, via any medium
 * is strictly prohibited.
 */

package com.talkdesk.tag.reference;

import com.td.athena.security.xss.jsonb.SafeStringDeserializer;
import io.quarkus.jsonb.JsonbConfigCustomizer;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

@Singleton
public class JsonConfigCustomizer implements JsonbConfigCustomizer {
    @Override
    public void customize(final JsonbConfig jsonbConfig) {
        jsonbConfig.withDeserializers(new SafeStringDeserializer());
    }
}
