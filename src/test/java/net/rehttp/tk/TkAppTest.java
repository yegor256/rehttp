/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import com.jcabi.matchers.XhtmlMatchers;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.rehttp.base.FakeBase;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkApp}.
 * @since 1.0
 */
final class TkAppTest {

    @BeforeEach
    void resourcesAvailable() {
        Assumptions.assumeFalse(
            TkAppTest.class.getResourceAsStream("/xsl/index.xsl") == null
        );
    }

    @Test
    void returnsXslStylesheet() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(new FakeBase()).act(
                    new RqFake("GET", "/xsl/index.xsl")
                )
            ).printBody(),
            Matchers.startsWith("<?xml")
        );
    }

    @Test
    void passesRequestThrough() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(new FakeBase()).act(
                    this.throughRequest()
                )
            ).print(),
            Matchers.startsWith("HTTP/1.1 200")
        );
    }

    @Test
    void simpleHomePage() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(new FakeBase()).act(
                    new RqFake("GET", "/")
                )
            ).print(),
            Matchers.containsString("<!DOCTYPE html")
        );
    }

    @Test
    void rendersHomePage() throws Exception {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new TkApp(new FakeBase()).act(
                        new RqWithHeader(
                            new RqFake("GET", "/"),
                            "Accept",
                            "text/xml"
                        )
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/@date",
                "/page/@sla",
                "/page/millis",
                "/page/links/link[@rel='self']"
            )
        );
    }

    @Test
    void rendersHomePageAsHtml() throws Exception {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new TkApp(new FakeBase()).act(
                        new RqWithHeader(
                            new RqFake("GET", "/"),
                            "Accept",
                            "text/html"
                        )
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPath("//xhtml:body")
        );
    }

    @Test
    void rendersHomePageViaHttp() throws Exception {
        new FtRemote(new TkApp(new FakeBase())).exec(
            home -> {
                new JdkRequest(home)
                    .header("Accept", "text/plain")
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(XmlResponse.class)
                    .assertXPath("/xhtml:html");
                new JdkRequest(home)
                    .through(VerboseWire.class)
                    .header("Accept", "application/xml")
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(XmlResponse.class)
                    .assertXPath("/page/version");
            }
        );
    }

    @Test
    void rendersNotFoundPage() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(new FakeBase()).act(
                    new RqFake("HEAD", "/not-found")
                )
            ).printBody(),
            Matchers.equalTo("Page not found")
        );
    }

    @Test
    void responseBodyContainsErrorMessageOnly() throws Exception {
        final String msg = "Execution error";
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(
                    new FakeBase(
                        req -> {
                            throw new IllegalArgumentException(msg);
                        }
                    )
                ).act(this.throughRequest())
            ).printBody(),
            Matchers.endsWith(msg)
        );
    }

    /**
     * Produce request with header that can pass a request through.
     * @return Request with header
     */
    private Request throughRequest() {
        return new RqFake(
            new ListOf<>(
                String.format(
                    "GET /%s",
                    URLEncoder.encode(
                        "http://www.yegor256.com", StandardCharsets.UTF_8
                    )
                ),
                "Host: p.rehttp.net"
            ),
            ""
        );
    }
}
