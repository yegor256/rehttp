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

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.http.wire.VerboseWire;
import com.jcabi.matchers.XhtmlMatchers;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import net.rehttp.base.FakeBase;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkApp}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkAppTest {

    /**
     * App can pass a request through.
     * @throws Exception If some problem inside
     */
    @Test
    public void passesRequestThrough() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(new FakeBase()).act(
                    this.throughRequest()
                )
            ).print(),
            Matchers.startsWith("HTTP/1.1 200")
        );
    }

    /**
     * App can render front page.
     * @throws Exception If some problem inside
     */
    @Test
    public void simpleHomePage() throws Exception {
        MatcherAssert.assertThat(
            new RsPrint(
                new TkApp(new FakeBase()).act(
                    new RqFake("GET", "/")
                )
            ).print(),
            Matchers.containsString("<!DOCTYPE html")
        );
    }

    /**
     * App can render front page.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersHomePage() throws Exception {
        System.out.println(
            new RsPrint(
                new TkApp(new FakeBase()).act(
                    new RqWithHeader(
                        new RqFake("GET", "/"),
                        // @checkstyle MultipleStringLiteralsCheck (1 line)
                        "Accept",
                        "text/xml"
                    )
                )
            ).printBody()
        );
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new TkApp(new FakeBase()).act(
                        new RqWithHeader(
                            new RqFake("GET", "/"),
                            // @checkstyle MultipleStringLiteralsCheck (1 line)
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

    /**
     * App can render front page.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersHomePageViaHttp() throws Exception {
        final Take app = new TkApp(new FakeBase());
        new FtRemote(app).exec(
            home -> {
                new JdkRequest(home)
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

    /**
     * App can render not found.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersNotFoundPage() throws Exception {
        final Take take = new TkApp(new FakeBase());
        MatcherAssert.assertThat(
            new RsPrint(
                take.act(new RqFake("HEAD", "/not-found"))
            ).printBody(),
            Matchers.equalTo("Page not found")
        );
    }

    /**
     * App can return a response with error message
     * when a RintimeException throws.
     * @throws Exception If some problem inside
     */
    @Test
    public void responseBodyContainsErrorMessageOnly() throws Exception {
        final String msg = "Execution error";
        final Take take = new TkApp(
            new FakeBase(
                req -> {
                    throw new IllegalArgumentException(msg);
                }
            )
        );
        MatcherAssert.assertThat(
            new RsPrint(
                take.act(this.throughRequest())
            ).printBody(),
            Matchers.endsWith(msg)
        );
    }

    /**
     * Produce request with header that can pass a request through.
     * @return Request with header.
     * @throws UnsupportedEncodingException If the named encoding
     *  is not supported
     */
    private Request throughRequest() throws UnsupportedEncodingException {
        return new RqFake(
            new ListOf<>(
                String.format(
                    "GET /%s",
                    URLEncoder.encode(
                        "http://www.yegor256.com", "UTF-8"
                    )
                ),
                "Host: p.rehttp.net"
            ),
            ""
        );
    }
}
