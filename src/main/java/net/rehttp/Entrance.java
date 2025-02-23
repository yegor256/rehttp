/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp;

import com.jcabi.log.VerboseCallable;
import com.jcabi.manifests.Manifests;
import io.sentry.Sentry;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.rehttp.base.Base;
import net.rehttp.base.DyBase;
import net.rehttp.tk.TkApp;
import org.cactoos.proc.RunnableOf;
import org.takes.http.Exit;
import org.takes.http.FtCli;

/**
 * Command line entry.
 *
 * @since 1.0
 */
public final class Entrance {

    /**
     * Ctor.
     */
    private Entrance() {
        // utility class
    }

    /**
     * Main entry point.
     * @param args Arguments
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        Sentry.init(Manifests.read("Rehttp-SentryDsn"));
        final Base base = new DyBase(new Dynamo());
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
            new RunnableOf(new VerboseCallable<Void>(new Retry(base), true)),
            1L, 1L, TimeUnit.MINUTES
        );
        new FtCli(new TkApp(base), args).start(Exit.NEVER);
    }

}
