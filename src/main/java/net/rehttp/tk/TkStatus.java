/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Yegor Bugayenko
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
