/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import com.jcabi.log.VerboseProcess;
import java.io.File;
import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkFixed;
import org.takes.facets.fork.FkHitRefresh;
import org.takes.facets.fork.TkFork;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkFiles;

/**
 * Fork by hit-refresh header.
 * To refresh resources.
 *
 * @since 1.0
 */
public class TkRefresh implements Take {

    /**
     * Underlying take.
     */
    private final Take take;

    /**
     * Ctor.
     * @param path Path
     * @throws IOException If fails
     */
    public TkRefresh(final File path) throws IOException {
        this.take =  new TkFork(
            new FkHitRefresh(
                path,
                () -> new VerboseProcess(
                    new ProcessBuilder(
                        "mvn",
                        "generate-resources"
                    )
                ).stdout(),
                new TkFiles("./target/classes")
            ),
            new FkFixed(new TkClasspath())
        );
    }

    @Override
    public final Response act(final Request req) throws Exception {
        return this.take.act(req);
    }
}
