/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.rehttp.base.Base;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithHeaders;
import org.takes.rs.RsWithType;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Badge for the URL.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkBadge implements Take {

    /**
     * XSL.
     */
    private static final XSL SVG = XSLDocument.make(
        TkBadge.class.getResourceAsStream("badge.xsl")
    );

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    TkBadge(final Base bse) {
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
        return new RsWithType(
            new RsWithHeaders(
                new RsWithBody(
                    TkBadge.SVG.with("style", "round").applyTo(
                        new XMLDocument(
                            new Xembler(
                                new Directives()
                                    .add("info")
                                    .add("url")
                                    .set(url)
                                    .up()
                                    .add("total")
                                    .set(
                                        this.base.status(url).history(
                                            Long.MAX_VALUE
                                        ).size()
                                    )
                                    .up()
                                    .add("failures")
                                    .set(
                                        this.base.status(url).failures(
                                            Long.MAX_VALUE
                                        ).size()
                                    )
                                    .up()
                            ).xmlQuietly()
                        )
                    )
                ),
                "Cache-Control: no-cache"
            ),
            "image/svg+xml"
        );
    }

}
