/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.rehttp.base.Base;
import org.cactoos.iterable.HeadOf;
import org.cactoos.iterable.Joined;
import org.cactoos.list.ListOf;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeSource;

/**
 * Info about URL.
 *
 * @since 1.0
 */
final class TkInfo implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     *
     * @param dbase Base
     */
    TkInfo(final Base dbase) {
        this.base = dbase;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String value = new RqHref.Smart(req).single("u");
        if (value.isEmpty()) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "The \"u\" query parameter is mandatory and must contain a URL"
            );
        }
        final URL url;
        try {
            url = new URI(value).toURL();
        } catch (final URISyntaxException | MalformedURLException
            | IllegalArgumentException ex) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                String.format("The URL \"%s\" is invalid", value),
                ex
            );
        }
        return new RsPage("/xsl/info.xsl", req, () -> this.xembly(url));
    }

    private Iterable<XeSource> xembly(final URL url) throws IOException {
        return new ListOf<>(
            new XeAppend("url", url.toString()),
            new XeAppend(
                "encoded_url",
                URLEncoder.encode(url.toString(), StandardCharsets.UTF_8)
            ),
            new XeAppend(
                "targets",
                new XeDirectives(
                    new Joined<>(
                        new HeadOf<>(
                            20,
                            this.base.status(url).failures(Long.MAX_VALUE)
                        )
                    )
                )
            ),
            new XeAppend(
                "history",
                new XeDirectives(
                    new Joined<>(
                        new HeadOf<>(
                            10,
                            this.base.status(url).history(Long.MAX_VALUE)
                        )
                    )
                )
            )
        );
    }
}
