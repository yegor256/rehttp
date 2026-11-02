/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.base;

import com.jcabi.aspects.Tv;
import com.jcabi.dynamo.Item;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;

/**
 * Take in DynamoDB.
 *
 * @since 1.0
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
     *
     * @param itm The item
     * @param msec Delay
     */
    DyTake(final Item itm, final long msec) {
        this.item = itm;
        this.delay = msec;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response act(final Request req) throws Exception {
        final URI uri = URI.create(this.item.get("url").s());
        Request request = req;
        if (this.item.has("request")) {
            try (InputStream body = new InputStreamOf(this.item.get("request").s())) {
                request = new RqLive(body);
            }
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
        final boolean success = code > 199 && code < 500;
        final Collection<Map.Entry<String, AttributeValueUpdate>> update =
            new ArrayList<>(Tv.SEVEN);
        update.addAll(
            new ListOf<Map.Entry<String, AttributeValueUpdate>>(
                new MapEntry<>(
                    "attempts",
                    AttributeValueUpdate.builder().value(
                        AttributeValue.fromN("1")
                    ).action(AttributeAction.ADD).build()
                ),
                new MapEntry<>(
                    "request",
                    AttributeValueUpdate.builder().value(
                        AttributeValue.fromS(
                            new RqPrint(request).print()
                        )
                    ).action(AttributeAction.PUT).build()
                ),
                new MapEntry<>(
                    "response",
                    AttributeValueUpdate.builder().value(
                        AttributeValue.fromS(
                            new Sub(
                                new RsPrint(response).print(), 0,
                                Tv.TWENTY * Tv.THOUSAND
                            ).asString()
                        )
                    ).action(AttributeAction.PUT).build()
                ),
                new MapEntry<>(
                    "success",
                    AttributeValueUpdate.builder().value(
                        AttributeValue.fromS(
                            Boolean.toString(success)
                        )
                    ).action(AttributeAction.PUT).build()
                ),
                new MapEntry<>(
                    "code",
                    AttributeValueUpdate.builder().value(
                        AttributeValue.fromN(
                            Integer.toString(code)
                        )
                    ).action(AttributeAction.PUT).build()
                ),
                new MapEntry<>(
                    "when",
                    AttributeValueUpdate.builder().value(
                        AttributeValue.fromN(
                            Long.toString(
                                System.currentTimeMillis() + this.delay
                            )
                        )
                    ).action(AttributeAction.PUT).build()
                )
            )
        );
        if (success) {
            update.add(
                new MapEntry<>(
                    "failed_url",
                    AttributeValueUpdate.builder()
                        .action(AttributeAction.DELETE)
                        .build()
                )
            );
        } else {
            update.add(
                new MapEntry<>(
                    "failed_url",
                    AttributeValueUpdate.builder().value(
                        AttributeValue.fromS(
                            uri.toString()
                        )
                    ).action(AttributeAction.PUT).build()
                )
            );
        }
        this.item.put(
            new MapOf<>((Iterable) update)
        );
        return response;
    }

    private static int code(final Head response) throws IOException {
        final String head = response.head().iterator().next();
        final int start = head.indexOf(' ') + 1;
        return Integer.parseInt(head.substring(start, head.indexOf(' ', start)));
    }

    private static Request request(final Request req, final URI uri) {
        final StringBuilder path = new StringBuilder();
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
