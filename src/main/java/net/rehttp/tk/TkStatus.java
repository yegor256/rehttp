/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.rehttp.base.Base;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.LengthOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Status for the URL.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkStatus implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkStatus(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final URL url;
        try {
            url = new URI(new RqHref.Smart(req).single("u")).toURL();
        } catch (final URISyntaxException ex) {
            throw new IOException(ex);
        }
        final int errors = new IoChecked<>(
            new LengthOf(
                this.base.status(url).failures(
                    Long.MAX_VALUE
                )
            )
        ).value().intValue();
        final Response response;
        if (errors == 0) {
            response = new RsText("No errors.");
        } else {
            response = new RsWithStatus(
                new RsText(String.format("%d errors.", errors)),
                HttpURLConnection.HTTP_INTERNAL_ERROR
            );
        }
        return response;
    }

}
