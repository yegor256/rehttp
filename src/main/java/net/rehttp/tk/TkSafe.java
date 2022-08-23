/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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
 * @author Eugene Nikolaev (eug.nikolaev@gmail.com)
 * @version $Id$
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
