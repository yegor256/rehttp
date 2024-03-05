/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge,  to any person obtaining
 * a copy  of  this  software  and  associated  documentation files  (the
 * "Software"),  to deal in the Software  without restriction,  including
 * without limitation the rights to use,  copy,  modify,  merge, publish,
 * distribute,  sublicense,  and/or sell  copies of the Software,  and to
 * permit persons to whom the Software is furnished to do so,  subject to
 * the  following  conditions:   the  above  copyright  notice  and  this
 * permission notice  shall  be  included  in  all copies or  substantial
 * portions of the Software.  The software is provided  "as is",  without
 * warranty of any kind, express or implied, including but not limited to
 * the warranties  of merchantability,  fitness for  a particular purpose
 * and non-infringement.  In  no  event shall  the  authors  or copyright
 * holders be liable for any claim,  damages or other liability,  whether
 * in an action of contract,  tort or otherwise,  arising from, out of or
 * in connection with the software or  the  use  or other dealings in the
 * software.
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
