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

import com.jcabi.aspects.Tv;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.rehttp.base.Base;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Limited;
import org.cactoos.list.ListOf;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;
import org.xembly.Directive;

/**
 * Info about URL.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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
        final URL url = new URL(new RqHref.Smart(req).single("u"));
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
                        new Joined<Directive>(
                            new Limited<Iterable<Directive>>(
                                Tv.TWENTY,
                                this.base.status(url).failures(Long.MAX_VALUE)
                            )
                        )
                    )
                ),
                new XeAppend(
                    "history",
                    new XeDirectives(
                        new Joined<Directive>(
                            new Limited<Iterable<Directive>>(
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
