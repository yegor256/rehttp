/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
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
import org.cactoos.func.RunnableOf;
import org.takes.http.Exit;
import org.takes.http.FtCli;

/**
 * Command line entry.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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
            new RunnableOf<>(new VerboseCallable<Void>(new Retry(base), true)),
            1L, 1L, TimeUnit.MINUTES
        );
        new FtCli(new TkApp(base), args).start(Exit.NEVER);
    }

}
