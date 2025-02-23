/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.base;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.cactoos.list.ListOf;
import org.takes.Take;
import org.takes.rs.RsText;

/**
 * Base.
 *
 * @since 1.0
 */
public final class FakeBase implements Base {

    /**
     * The take as a target.
     */
    private final Take take;

    /**
     * Ctor.
     */
    public FakeBase() {
        this(request -> new RsText("Everything is fine"));
    }

    /**
     * Ctor.
     * @param take The take as a target.
     */
    public FakeBase(final Take take) {
        this.take = take;
    }

    @Override
    public Take target(final URL url, final long time) {
        return this.take;
    }

    @Override
    public Iterable<Take> expired() throws IOException {
        try {
            return new ListOf<>(this.target(new URI("#").toURL(), 0L));
        } catch (final URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public Status status(final URL url) {
        return new FakeStatus();
    }

}
