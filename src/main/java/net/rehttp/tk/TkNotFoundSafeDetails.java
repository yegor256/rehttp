/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Decorator for handle not exist records.
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
    public Response act(final Request req) throws IOException {
        try {
            return this.origin.act(req);
        } catch (final IOException ex) {
            return RsPage(
                "/xsl/not_found.xsl",
                req
            );
        }
        
    }
}
