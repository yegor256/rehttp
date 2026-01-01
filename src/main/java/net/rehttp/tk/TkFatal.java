/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.takes.Response;
import org.takes.facets.fallback.Fallback;
import org.takes.facets.fallback.RqFallback;
import org.takes.misc.Opt;
import org.takes.rs.RsHtml;
import org.takes.rs.RsVelocity;
import org.takes.rs.RsWithStatus;

/**
 * Fatal error page.
 *
 * @since 1.0
 */
public class TkFatal implements Fallback {

    @Override
    public final Opt<Response> route(final RqFallback req) throws IOException {
        return new Opt.Single<>(
            new RsWithStatus(
                new RsHtml(
                    new RsVelocity(
                        TkApp.class.getResource("error.html.vm"),
                        new RsVelocity.Pair(
                            "err",
                            new IoCheckedText(new TextOf(req.throwable())).asString()
                        ),
                        new RsVelocity.Pair(
                            "rev",
                            Manifests.read("Rehttp-Revision")
                        )
                    )
                ),
                HttpURLConnection.HTTP_INTERNAL_ERROR
            )
        );
    }
}
