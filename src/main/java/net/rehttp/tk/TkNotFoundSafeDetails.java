/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.rehttp.base.Base;
import org.cactoos.list.ListOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeDirectives;
import org.xembly.Directive;

/**
 * Details of request.
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
            this.origin.act(req);
        } catch (final IOException ex) {
            return RsPage(
                "/xsl/not_found.xsl",
                req
            );
        }
        
    }
}
