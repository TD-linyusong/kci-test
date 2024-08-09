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

package com.talkdesk.tag.reference.common;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbProperty;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public final class PoliciesHelper {
    private PoliciesHelper() { }

    public static void stubPoliciesEvaluateEndpoint(final WireMockServer wireMockServer, final List<String> policies) {
        final List<PolicyItem> policiesItems = policies.stream()
                                                       .map(p -> PolicyItem.builder().targetPolicy(p).success(true).build())
                                                       .collect(Collectors.toList());
        wireMockServer.stubFor(post(urlPathMatching("/policies/evaluate/bulk"))
                                   .willReturn(
                                       ok(JsonbBuilder.create().toJson(policiesItems))
                                           .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                              ));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PolicyItem {
        @JsonbProperty("target_policy")
        private String targetPolicy;
        private Boolean success;
    }
}
