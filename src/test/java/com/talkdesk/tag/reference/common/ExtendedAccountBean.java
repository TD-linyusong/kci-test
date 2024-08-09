package com.talkdesk.tag.reference.common;
/*
 * Talkdesk Confidential
 *
 * Copyright (C) Talkdesk Inc. 2019
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office. Unauthorized copying of this file, via any medium
 * is strictly prohibited.
 */

import com.td.athena.security.account.JwtAccountBean;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;

/**
 * This is a subclass of the athena provided JwtAccountBean. The goal of this class is to allow to dynamically define the
 * JWT so we can authenticate by other means, especially when we receive events from queues.
 */
@Priority(100)
@Alternative
@RequestScoped
public class ExtendedAccountBean extends JwtAccountBean {

    public void setToken(final JsonWebToken token) {
        super.token = token;
    }

    public JsonWebToken getToken() {
        return super.token;
    }
}
