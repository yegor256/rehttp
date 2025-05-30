/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import com.jcabi.manifests.Manifests;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.rehttp.base.Base;
import org.takes.Take;
import org.takes.facets.fallback.TkFallback;
import org.takes.facets.flash.TkFlash;
import org.takes.facets.fork.FkFixed;
import org.takes.facets.fork.FkHost;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.fork.TkRegex;
import org.takes.facets.forward.TkForward;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkGzip;
import org.takes.tk.TkMeasured;
import org.takes.tk.TkSslOnly;
import org.takes.tk.TkVersioned;
import org.takes.tk.TkWithHeaders;
import org.takes.tk.TkWithType;
import org.takes.tk.TkWrap;

/**
 * App.
 *
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
        return new TkFallback(
            new TkWithHeaders(
                new TkVersioned(
                    new TkMeasured(
                        new TkFork(
                            new FkHost(
                                "p.rehttp.net",
                                req -> base.target(
                                    new URI(new RqHref.Base(req).href().path().substring(1)).toURL(),
                                    System.currentTimeMillis()
                                ).act(req)
                            ),
                            new FkRegex(
                                "/p/(.+)",
                                (TkRegex) req -> base.target(
                                    new URI(req.matcher().group(1)).toURL(),
                                    System.currentTimeMillis()
                                ).act(req)
                            ),
                            new FkFixed(
                                new TkSslOnly(
                                    new TkFlash(
                                        new TkSafe(
                                            new TkForward(
                                                new TkGzip(
                                                    new TkFork(
                                                        new FkRegex("/robots.txt", ""),
                                                        new FkRegex(
                                                            "/org/takes/.+\\.xsl",
                                                            new TkClasspath()
                                                        ),
                                                        new FkRegex(
                                                            "/xsl/[a-z\\-]+\\.xsl",
                                                            new TkWithType(
                                                                new TkRefresh(
                                                                    new File(
                                                                        "./src/main/resources/xsl"
                                                                    )
                                                                ),
                                                                "text/xsl"
                                                            )
                                                        ),
                                                        new FkRegex(
                                                            "/css/[a-z]+\\.css",
                                                            new TkWithType(
                                                                new TkRefresh(
                                                                    new File("./src/main/scss")
                                                                ),
                                                                "text/css"
                                                            )
                                                        ),
                                                        new FkRegex(
                                                            "/images/[a-z]+\\.svg",
                                                            new TkWithType(
                                                                new TkRefresh(
                                                                    new File("./src/main/resources")
                                                                ),
                                                                "image/svg+xml"
                                                            )
                                                        ),
                                                        new FkRegex(
                                                            "/images/[a-z]+\\.png",
                                                            new TkWithType(
                                                                new TkRefresh(
                                                                    new File("./src/main/resources")
                                                                ),
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
                                                        new FkRegex("/i", new TkInfo(base)),
                                                        new FkRegex("/d", new TkDetails(base)),
                                                        new FkRegex("/s", new TkStatus(base)),
                                                        new FkRegex("/b", new TkBadge(base))
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                String.format("X-Rehttp-Revision: %s", TkApp.REV),
                "Vary: Cookie"
            ),
            req -> new Opt.Single<>(
                new RsWithStatus(
                    new RsText(req.throwable().getMessage()),
                    req.code()
                )
            )
        );
    }
}
