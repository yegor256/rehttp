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
 * @author Eugene Nikolaev (eug.nikolaev@gmail.com)
 * @version $Id$
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
