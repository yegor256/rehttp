/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.base;

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.jcabi.aspects.Tv;
import com.jcabi.dynamo.Item;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import org.cactoos.io.InputStreamOf;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Skipped;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.cactoos.text.Sub;
import org.takes.Head;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqGreedy;
import org.takes.rq.RqLive;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rs.RsPrint;
import org.takes.tk.TkProxy;

/**
 * Take in DynamoDB.
 *
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
final class DyTake implements Take {

    /**
     * The item.
     */
    private final Item item;

    /**
     * Delay in msecs between attempts.
     */
    private final transient long delay;

    /**
     * Ctor.
     * @param itm The item
     * @param msec Delay
     */
    DyTake(final Item itm, final long msec) {
        this.item = itm;
        this.delay = msec;
    }

    @Override
    @SuppressWarnings({ "unchecked", "PMD.ExcessiveMethodLength" })
    public Response act(final Request req) throws Exception {
        final URI uri = URI.create(this.item.get("url").getS());
        Request request = req;
        if (this.item.has("request")) {
            request = new RqLive(
                new InputStreamOf(this.item.get("request").getS())
            );
            Logger.info(
                this, "Retrying %s with \"%s\"",
                uri, request.head().iterator().next()
            );
        }
        request = new RqGreedy(request);
        final Response response = new TkProxy(uri).act(
            DyTake.request(request, uri)
        );
        final int code = DyTake.code(response);
        // @checkstyle MagicNumber (1 line)
        final boolean success = code > 199 && code < 500;
        final Collection<Map.Entry<String, AttributeValueUpdate>> update =
            new LinkedList<>();
        update.addAll(
            new ListOf<Map.Entry<String, AttributeValueUpdate>>(
                new MapEntry<>(
                    "attempts",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withN("1")
                    ).withAction(AttributeAction.ADD)
                ),
                new MapEntry<>(
                    "request",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withS(
                            new RqPrint(request).print()
                        )
                    ).withAction(AttributeAction.PUT)
                ),
                new MapEntry<>(
                    "response",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withS(
                            new Sub(
                                new RsPrint(response).print(), 0,
                                Tv.TWENTY * Tv.THOUSAND
                            ).asString()
                        )
                    ).withAction(AttributeAction.PUT)
                ),
                new MapEntry<>(
                    "success",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withS(
                            Boolean.toString(success)
                        )
                    ).withAction(AttributeAction.PUT)
                ),
                new MapEntry<>(
                    "code",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withN(
                            Integer.toString(code)
                        )
                    ).withAction(AttributeAction.PUT)
                ),
                new MapEntry<>(
                    "when",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withN(
                            Long.toString(
                                System.currentTimeMillis() + this.delay
                            )
                        )
                    ).withAction(AttributeAction.PUT)
                )
            )
        );
        if (success) {
            update.add(
                new MapEntry<>(
                    "failed_url",
                    new AttributeValueUpdate().withAction(
                        AttributeAction.DELETE
                    )
                )
            );
        } else {
            update.add(
                new MapEntry<>(
                    "failed_url",
                    new AttributeValueUpdate().withValue(
                        new AttributeValue().withS(
                            uri.toString()
                        )
                    ).withAction(AttributeAction.PUT)
                )
            );
        }
        this.item.put(
            new MapOf<>((Iterable) update)
        );
        return response;
    }

    /**
     * Code of the response.
     * @param response The response
     * @return Code
     * @throws IOException If fails
     */
    private static int code(final Head response) throws IOException {
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
                return new Joined<String>(
                    Collections.singleton(
                        String.format(
                            "%s %s HTTP/1.1",
                            new RqMethod.Base(req).method(),
                            path
                        )
                    ),
                    new Skipped<>(1, req.head())
                );
            }

            @Override
            public InputStream body() throws IOException {
                return req.body();
            }
        };
    }

}
