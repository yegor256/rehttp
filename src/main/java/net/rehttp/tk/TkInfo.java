/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import com.jcabi.aspects.Tv;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.rehttp.base.Base;
import org.cactoos.iterable.HeadOf;
import org.cactoos.iterable.Joined;
import org.cactoos.list.ListOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;

/**
 * Info about URL.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkInfo implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkInfo(final Base bse) {
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
        return new RsPage(
            "/xsl/info.xsl",
            req,
            () -> new ListOf<>(
                new XeAppend("url", url.toString()),
                new XeAppend(
                    "encoded_url",
                    URLEncoder.encode(
                        url.toString(), StandardCharsets.UTF_8.name()
                    )
                ),
                new XeAppend(
                    "targets",
                    new XeDirectives(
                        new Joined<>(
                            new HeadOf<>(
                                Tv.TWENTY,
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
                                Tv.TEN,
                                this.base.status(url).history(Long.MAX_VALUE)
                            )
                        )
                    )
                )
            )
        );
    }

}
