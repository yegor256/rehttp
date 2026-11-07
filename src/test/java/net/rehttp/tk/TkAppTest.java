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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import net.rehttp.base.Base;
import net.rehttp.base.FakeBase;
import net.rehttp.base.FakeStatus;
import net.rehttp.base.Status;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rq.RqRequestLine;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;
import org.takes.rs.RsText;
import org.takes.tk.TkProxy;

/**
 * Test case for {@link TkApp}.
 *
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

    @Test
    void keepsTargetQueryOnHostRoute() throws Exception {
        final AtomicReference<URL> target = new AtomicReference<>();
        new TkApp(TkAppTest.recording(target)).act(
            new RqFake(
                new ListOf<>(
                    "GET /https://example.com/hook?token=abc&event=push",
                    "Host: p.rehttp.net"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            target.get().toString(),
            Matchers.equalTo("https://example.com/hook?token=abc&event=push")
        );
    }

    @Test
    void keepsTargetQueryOnPathRoute() throws Exception {
        final AtomicReference<URL> target = new AtomicReference<>();
        new TkApp(TkAppTest.recording(target)).act(
            new RqFake(
                new ListOf<>(
                    "GET /p/https://example.com/hook?token=abc",
                    "Host: www.rehttp.net"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            target.get().toString(),
            Matchers.equalTo("https://example.com/hook?token=abc")
        );
    }

    @Test
    void keepsTargetWithoutQueryIntact() throws Exception {
        final AtomicReference<URL> target = new AtomicReference<>();
        new TkApp(TkAppTest.recording(target)).act(
            new RqFake(
                new ListOf<>(
                    "GET /p/https://example.com/hook",
                    "Host: www.rehttp.net"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            target.get().toString(),
            Matchers.equalTo("https://example.com/hook")
        );
    }

    @Test
    void keepsValuelessAndRepeatedParams() throws Exception {
        final AtomicReference<URL> target = new AtomicReference<>();
        new TkApp(TkAppTest.recording(target)).act(
            new RqFake(
                new ListOf<>(
                    "GET /p/https://example.com/hook?flag&a=1&a=2&b=",
                    "Host: www.rehttp.net"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            target.get().toString(),
            Matchers.equalTo("https://example.com/hook?flag&a=1&a=2&b=")
        );
    }

    @Test
    void keepsEncodedTargetQuery() throws Exception {
        final AtomicReference<URL> target = new AtomicReference<>();
        new TkApp(TkAppTest.recording(target)).act(
            new RqFake(
                new ListOf<>(
                    String.format(
                        "GET /%s",
                        URLEncoder.encode(
                            "https://example.com/hook?token=abc%20x",
                            StandardCharsets.UTF_8
                        )
                    ),
                    "Host: p.rehttp.net"
                ),
                ""
            )
        );
        MatcherAssert.assertThat(
            target.get().toString(),
            Matchers.equalTo("https://example.com/hook?token=abc%20x")
        );
    }

    @Test
    void passesTargetQueryToOrigin() throws Exception {
        new FtRemote(
            (Take) req -> new RsText(new RqRequestLine.Base(req).uri())
        ).exec(
            home -> {
                final AtomicReference<URL> target = new AtomicReference<>();
                final URI echo = home.resolve("/hook");
                MatcherAssert.assertThat(
                    new RsPrint(
                        new TkApp(TkAppTest.proxying(target)).act(
                            new RqFake(
                                new ListOf<>(
                                    String.format("POST /%s?token=abc&event=push", echo),
                                    "Host: p.rehttp.net"
                                ),
                                "{}"
                            )
                        )
                    ).printBody(),
                    Matchers.equalTo(
                        String.format("%s?token=abc&event=push", echo.getPath())
                    )
                );
            }
        );
    }

    private static Base recording(final AtomicReference<URL> ref) {
        return new Base() {
            @Override
            public Take target(final URL url, final long time) {
                ref.set(url);
                return req -> new RsText("Everything is fine");
            }

            @Override
            public Iterable<Take> expired() {
                return new ListOf<>();
            }

            @Override
            public Status status(final URL url) {
                return new FakeStatus();
            }
        };
    }

    private static Base proxying(final AtomicReference<URL> ref) {
        return new Base() {
            @Override
            public Take target(final URL url, final long time) throws IOException {
                return TkAppTest.proxiedTake(ref, url);
            }

            @Override
            public Iterable<Take> expired() {
                return new ListOf<>();
            }

            @Override
            public Status status(final URL url) {
                return new FakeStatus();
            }
        };
    }

    private static Take proxiedTake(final AtomicReference<URL> ref, final URL url)
        throws IOException {
        ref.set(url);
        final URI uri = URI.create(url.toString());
        return req -> new TkProxy(uri).act(
            new RqFake(
                new ListOf<>(
                    String.format(
                        "%s %s HTTP/1.1",
                        new RqMethod.Base(req).method(),
                        TkAppTest.pathAndQuery(uri)
                    ),
                    String.format("Host: %s", uri.getRawAuthority())
                ),
                new RqPrint(req).printBody()
            )
        );
    }

    private static String pathAndQuery(final URI uri) {
        final StringBuilder path = new StringBuilder(uri.getRawPath());
        if (path.length() == 0) {
            path.append('/');
        }
        if (uri.getQuery() != null) {
            path.append('?').append(uri.getRawQuery());
        }
        return path.toString();
    }

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
