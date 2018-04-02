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
 * @author Eugene Nikolaev (eug.nikolaev@gmail.com)
 * @version $Id$
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
    public final Response act(final Request req) throws IOException {
        return this.take.act(req);
    }
}
