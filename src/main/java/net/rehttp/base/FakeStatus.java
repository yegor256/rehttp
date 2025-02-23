/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.base;

import java.util.Collection;
import java.util.Collections;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Base.
 *
 * @since 1.0
 */
public final class FakeStatus implements Status {

    @Override
    public Collection<Iterable<Directive>> failures(final long after) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Iterable<Directive>> history(final long after) {
        return Collections.emptyList();
    }

    @Override
    public Directives details(final long time) {
        return new Directives();
    }
}
