/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import io.sentry.Sentry;
import java.net.HttpURLConnection;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fallback.FbChain;
import org.takes.facets.fallback.FbStatus;
import org.takes.facets.fallback.TkFallback;
import org.takes.misc.Opt;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Authenticated take.
 *
 * @since 1.0
 */
public class TkSafe implements Take {

    /**
     * Underlying take.
     */
    private final Take take;

    /**
     * Ctor.
     * @param take Original take
     */
    public TkSafe(final Take take) {
        this.take = new TkFallback(
            take,
            new FbChain(
                new FbStatus(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    new RsWithStatus(
                        new RsText("Page not found"),
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                ),
                new FbStatus(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    new RsWithStatus(
                        new RsText("Bad request"),
                        HttpURLConnection.HTTP_BAD_REQUEST
                    )
                ),
                req -> {
                    Sentry.captureException(req.throwable());
                    return new Opt.Empty<>();
                },
                req -> new TkFatal().route(req)
            )
        );
    }

    @Override
    public final Response act(final Request req) throws Exception {
        return this.take.act(req);
    }
}
