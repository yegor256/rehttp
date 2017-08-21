/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
package net.rehttp.base;

import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Item;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.cactoos.iterable.Skipped;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Concat;
import org.takes.rq.RqMethod;
import org.takes.tk.TkProxy;

/**
 * Take in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
final class DyTake implements Take {

    /**
     * The item.
     */
    private final Item item;

    /**
     * Ctor.
     * @param itm The item
     */
    DyTake(final Item itm) {
        this.item = itm;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final URI uri = URI.create(this.item.get("url").getS());
        final Response response = new TkProxy(uri).act(
            DyTake.request(req, uri)
        );
        final int code = DyTake.code(response);
        this.item.put(
            new AttributeUpdates()
                // @checkstyle MagicNumber (1 line)
                .with("success", Boolean.toString(code > 199 && code < 300))
                .with("code", code)
                .with(
                    "next",
                    System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L)
                )
        );
        return response;
    }

    /**
     * Code of the response.
     * @param response The response
     * @return Code
     * @throws IOException If fails
     */
    private static int code(final Response response) throws IOException {
        final String head = response.head().iterator().next();
        final String[] parts = head.split(" ");
        return Integer.parseInt(parts[1]);
    }

    /**
     * The request to send.
     * @param req Original request
     * @param uri Destination URI
     * @return Request
     */
    private static Request request(final Request req, final URI uri) {
        final StringBuilder path = new StringBuilder(0);
        path.append(uri.getRawPath());
        if (path.length() == 0) {
            path.append('/');
        }
        if (uri.getQuery() != null) {
            path.append('?').append(uri.getRawQuery());
        }
        if (uri.getFragment() != null) {
            path.append('#').append(uri.getRawFragment());
        }
        return new Request() {
            @Override
            public Iterable<String> head() throws IOException {
                return new Concat<>(
                    Collections.singleton(
                        String.format(
                            "%s %s HTTP/1.1",
                            new RqMethod.Base(req).method(),
                            path
                        )
                    ),
                    new Skipped<>(req.head(), 1)
                );
            }
            @Override
            public InputStream body() throws IOException {
                return req.body();
            }
        };
    }

}
