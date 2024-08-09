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

import com.td.athena.security.policy.authz.AuthzPoliciesRetriever;
import io.quarkus.test.Mock;

import javax.enterprise.context.RequestScoped;
import java.util.Set;

/**
 * This class should be used to override the default Retriever.
 * Look the configuration (com.td.athena.security.policy.retriever=com.talkdesk.tag.reference.common.TestPermissionsEndpointPoliciesRetriever)
 * on the application.properties in your test scope.
 */
@RequestScoped
@Mock
public class TestPermissionsEndpointPoliciesRetriever extends AuthzPoliciesRetriever {

    private static final Set<String> defaultPoliciesForTests = Set.of("reference.write", "reference.read");

    //@RequestScoped is required so that caching only happens per request
    //But we need a way to decide for all instances if it should try to do the real rest calls or just use the mocked data
    private static boolean useDefaultPoliciesForTests = true;

    @Override
    public Set<String> retrieve(final Set<String> requiredPolicies) {
        if (useDefaultPoliciesForTests) {
            return defaultPoliciesForTests;
        } else {
            return super.retrieve(requiredPolicies);
        }
    }

    public static void useDefaultPoliciesForTests() {
        useDefaultPoliciesForTests = true;
    }

    public static void callPoliciesEndpoint() {
        useDefaultPoliciesForTests = false;
    }
}
