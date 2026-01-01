/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp;

import java.util.concurrent.Callable;
import net.rehttp.base.Base;
import org.takes.Request;
import org.takes.Take;
import org.takes.rq.RqFake;

/**
 * Retry them all.
 *
 * @since 1.0
 */
public final class Retry implements Callable<Void> {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public Retry(final Base bse) {
        this.base = bse;
    }

    @Override
    public Void call() throws Exception {
        final Request req = new RqFake();
        for (final Take take : this.base.expired()) {
            take.act(req);
        }
        return null;
    }

}
