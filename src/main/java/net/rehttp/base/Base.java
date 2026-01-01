/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.base;

import java.io.IOException;
import java.net.URL;
import org.takes.Take;

/**
 * Base.
 *
 * @since 1.0
 */
public interface Base {

    /**
     * Get target by URL and time.
     * @param url The URL
     * @param time The time
     * @return The request
     * @throws IOException If fails
     */
    Take target(URL url, long time) throws IOException;

    /**
     * Expired targets.
     * @return List of expired targets
     * @throws IOException If fails
     */
    Iterable<Take> expired() throws IOException;

    /**
     * History of the URL.
     * @param url The URL
     * @return The status
     * @throws IOException If fails
     */
    Status status(URL url) throws IOException;

}
