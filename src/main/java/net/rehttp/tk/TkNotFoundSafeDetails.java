/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import java.net.HttpURLConnection;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithStatus;

/**
 * Decorator that answers with a not-found page when a record is missing.
 *
 * @since 1.0
 */
final class TkNotFoundSafeDetails implements Take {

    /**
     * Origin.
     */
    private final Take origin;

    /**
     * Ctor.
     * @param orgn Take
     */
    TkNotFoundSafeDetails(final Take orgn) {
        this.origin = orgn;
    }

    @Override
    public Response act(final Request req) throws Exception {
        try {
            return this.origin.act(req);
        } catch (final IllegalArgumentException ex) {
            return new RsWithStatus(
                new RsPage("/xsl/not_found.xsl", req),
                HttpURLConnection.HTTP_NOT_FOUND
            );
        }
    }
}
