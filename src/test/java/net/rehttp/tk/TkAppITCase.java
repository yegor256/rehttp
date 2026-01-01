/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void resourcesAvailable() {
        Assumptions.assumeFalse(
            TkAppTest.class.getResourceAsStream("/xsl/index.xsl") == null
        );
    }

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
