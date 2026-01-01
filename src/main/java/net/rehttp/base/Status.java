/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.base;

import java.io.IOException;
import java.util.Collection;
import org.xembly.Directive;

/**
 * Base.
 *
 * @since 1.0
 */
public interface Status {

    /**
     * Failures.
     * @param after After this date (epoch in milliseconds)
     * @return Map of recent failures
     * @throws IOException If fails
     */
    Collection<Iterable<Directive>> failures(long after) throws IOException;

    /**
     * Full history of the URL.
     * @param after After this date (epoch in milliseconds)
     * @return The history
     * @throws IOException If fails
     */
    Collection<Iterable<Directive>> history(long after) throws IOException;

    /**
     * Full details of one particular request.
     * @param time The time
     * @return The details in plain text
     * @throws IOException If fails
     */
    Iterable<Directive> details(long time) throws IOException;

}
