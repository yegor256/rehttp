/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
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
import com.jcabi.manifests.Manifests;
import io.sentry.Sentry;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import net.rehttp.base.Base;
import org.cactoos.text.TextOf;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fallback.FbChain;
import org.takes.facets.fallback.FbStatus;
import org.takes.facets.fallback.RqFallback;
import org.takes.facets.fallback.TkFallback;
import org.takes.facets.flash.TkFlash;
import org.takes.facets.fork.FkFixed;
import org.takes.facets.fork.FkHitRefresh;
import org.takes.facets.fork.FkHost;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.forward.TkForward;
import org.takes.misc.Opt;
import org.takes.misc.Sprintf;
import org.takes.rq.RqHref;
import org.takes.rs.RsHtml;
import org.takes.rs.RsText;
import org.takes.rs.RsVelocity;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkFiles;
import org.takes.tk.TkGzip;
import org.takes.tk.TkMeasured;
import org.takes.tk.TkVersioned;
import org.takes.tk.TkWithHeaders;
import org.takes.tk.TkWithType;
import org.takes.tk.TkWrap;

/**
 * App.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 * @checkstyle LineLength (500 lines)
 */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.ExcessiveMethodLength" })
public final class TkApp extends TkWrap {

    /**
     * Revision.
     */
    private static final String REV = Manifests.read("Rehttp-Revision");

    /**
     * Ctor.
     * @param base Base
     * @throws IOException If fails
     */
    public TkApp(final Base base) throws IOException {
        super(TkApp.app(base));
    }

    /**
     * Ctor.
     * @param base Base
     * @return App
     * @throws IOException If fails
     */
    private static Take app(final Base base) throws IOException {
        return new TkWithHeaders(
            new TkVersioned(
                new TkMeasured(
                    new TkFlash(
                        TkApp.safe(
                            new TkForward(
                                new TkGzip(
                                    new TkFork(
                                        new FkRegex("/robots.txt", ""),
                                        new FkHost(
                                            "p.rehttp.net",
                                            req -> base.target(
                                                new URL(new RqHref.Base(req).href().path().substring(1)),
                                                System.currentTimeMillis()
                                            ).act(req)
                                        ),
                                        new FkRegex(
                                            "/org/takes/.+\\.xsl",
                                            new TkClasspath()
                                        ),
                                        new FkRegex(
                                            "/xsl/[a-z\\-]+\\.xsl",
                                            new TkWithType(
                                                TkApp.refresh("./src/main/xsl"),
                                                "text/xsl"
                                            )
                                        ),
                                        new FkRegex(
                                            "/css/[a-z]+\\.css",
                                            new TkWithType(
                                                TkApp.refresh("./src/main/scss"),
                                                "text/css"
                                            )
                                        ),
                                        new FkRegex(
                                            "/images/[a-z]+\\.svg",
                                            new TkWithType(
                                                TkApp.refresh("./src/main/resources"),
                                                "image/svg+xml"
                                            )
                                        ),
                                        new FkRegex(
                                            "/images/[a-z]+\\.png",
                                            new TkWithType(
                                                TkApp.refresh("./src/main/resources"),
                                                "image/png"
                                            )
                                        ),
                                        new FkRegex(
                                            "/",
                                            (Take) request -> new RsPage(
                                                "/xsl/index.xsl",
                                                request
                                            )
                                        ),
                                        new FkRegex(
                                            "/",
                                            (Take) request -> new RsPage(
                                                "/xsl/index.xsl",
                                                request
                                            )
                                        ),
                                        new FkRegex("/i", new TkInfo(base)),
                                        new FkRegex("/h", new TkHistory(base)),
                                        new FkRegex("/d", new TkDetails(base))
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            new Sprintf("X-Rehttp-Revision: %s", TkApp.REV).toString(),
            "Vary: Cookie"
        );
    }

    /**
     * With fallback.
     * @param take Takes
     * @return Authenticated takes
     */
    private static Take safe(final Take take) {
        return new TkFallback(
            take,
            new FbChain(
                new FbStatus(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    new RsWithStatus(
                        new RsText("Page not found"),
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                ),
                new FbStatus(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    new RsWithStatus(
                        new RsText("Bad request"),
                        HttpURLConnection.HTTP_BAD_REQUEST
                    )
                ),
                req -> {
                    Sentry.capture(req.throwable());
                    return new Opt.Empty<>();
                },
                req -> new Opt.Single<>(TkApp.fatal(req))
            )
        );
    }

    /**
     * Make fatal error page.
     * @param req Request
     * @return Response
     * @throws IOException If fails
     */
    private static Response fatal(final RqFallback req) throws IOException {
        return new RsWithStatus(
            new RsHtml(
                new RsVelocity(
                    TkApp.class.getResource("error.html.vm"),
                    new RsVelocity.Pair(
                        "err",
                        new TextOf(req.throwable()).asString()
                    ),
                    new RsVelocity.Pair("rev", Manifests.read("Rehttp-Revision"))
                )
            ),
            HttpURLConnection.HTTP_INTERNAL_ERROR
        );
    }

    /**
     * Ctor.
     * @param path Path of files
     * @return Take
     * @throws IOException If fails
     */
    private static Take refresh(final String path) throws IOException {
        return new TkFork(
            new FkHitRefresh(
                new File(path),
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
}
