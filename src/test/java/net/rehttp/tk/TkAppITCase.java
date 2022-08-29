/*
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
import com.jcabi.matchers.XhtmlMatchers;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.rehttp.Dynamo;
import net.rehttp.Retry;
import net.rehttp.base.Base;
import net.rehttp.base.DyBase;
import org.cactoos.list.ListOf;
import org.cactoos.proc.RunnableOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Integration case for {@link TkApp}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkAppITCase {

    @Test
    void passesRequestThrough() throws Exception {
        final AtomicInteger count = new AtomicInteger(3);
        final Take take = req -> {
            final Response response;
            if (count.decrementAndGet() > 0) {
                response = new RsWithStatus(
                    HttpURLConnection.HTTP_INTERNAL_ERROR
                );
            } else {
                response = new RsText("Success");
            }
            return response;
        };
        new FtRemote(take).exec(
            home -> {
                final Base base = new DyBase(new Dynamo(), 0L);
                MatcherAssert.assertThat(
                    new RsPrint(
                        new TkApp(base).act(
                            new RqFake(
                                new ListOf<String>(
                                    String.format(
                                        "PUT /%s",
                                        URLEncoder.encode(
                                            home.toString(),
                                            StandardCharsets.UTF_8.displayName()
                                        )
                                    ),
                                    "Host: p.rehttp.net"
                                ),
                                ""
                            )
                        )
                    ).print(),
                    Matchers.startsWith("HTTP/1.1 500")
                );
                final Runnable retry = new RunnableOf(new Retry(base));
                for (int idx = 0; idx < Tv.TEN; ++idx) {
                    retry.run();
                }
                MatcherAssert.assertThat(
                    XhtmlMatchers.xhtml(
                        new RsPrint(
                            new TkApp(base).act(
                                new RqFake(
                                    new ListOf<>(
                                        String.format(
                                            "GET /i?u=%s",
                                            URLEncoder.encode(
                                                home.toString(),
                                                StandardCharsets.UTF_8.name()
                                            )
                                        ),
                                        "Host: www.rehttp.net",
                                        "Accept: application/xml"
                                    ),
                                    ""
                                )
                            )
                        ).printBody()
                    ),
                    XhtmlMatchers.hasXPaths(
                        "/page/targets[not(target)]",
                        "/page/history/target[code='200']",
                        "/page/history/target[attempts='3']"
                    )
                );
            }
        );
        MatcherAssert.assertThat(count.get(), Matchers.equalTo(0));
    }

}
