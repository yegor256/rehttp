/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2019 Yegor Bugayenko
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

import com.jcabi.manifests.Manifests;
import java.util.Collections;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.flash.XeFlash;
import org.takes.facets.fork.FkTypes;
import org.takes.facets.fork.RsFork;
import org.takes.rs.RsPrettyXml;
import org.takes.rs.RsWithType;
import org.takes.rs.RsWrap;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeDate;
import org.takes.rs.xe.XeLinkHome;
import org.takes.rs.xe.XeLinkSelf;
import org.takes.rs.xe.XeLocalhost;
import org.takes.rs.xe.XeMemory;
import org.takes.rs.xe.XeMillis;
import org.takes.rs.xe.XeSla;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeStylesheet;

/**
 * Index resource, front page of the website.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("PMD.ExcessiveImports")
final class RsPage extends RsWrap {

    /**
     * Ctor.
     * @param xsl XSL
     * @param req Request
     */
    RsPage(final String xsl, final Request req) {
        super(RsPage.make(xsl, req, Collections::emptyList));
    }

    /**
     * Ctor.
     * @param xsl XSL
     * @param req Request
     * @param src Source
     */
    RsPage(final String xsl, final Request req,
        final Scalar<Iterable<XeSource>> src) {
        super(RsPage.make(xsl, req, src));
    }

    /**
     * Make it.
     * @param xsl XSL
     * @param req Request
     * @param src Source
     * @return Response
     */
    private static Response make(final String xsl, final Request req,
        final Scalar<Iterable<XeSource>> src) {
        final Response raw = new RsXembly(
            new XeStylesheet(xsl),
            new XeAppend(
                "page",
                new XeMillis(false),
                new XeChain(src),
                new XeMemory(),
                new XeLinkHome(req),
                new XeLinkSelf(req),
                new XeMillis(true),
                new XeDate(),
                new XeSla(),
                new XeLocalhost(),
                new XeFlash(req),
                new XeAppend(
                    "version",
                    new XeAppend("name", Manifests.read("Rehttp-Version")),
                    new XeAppend("revision", Manifests.read("Rehttp-Revision")),
                    new XeAppend("date", Manifests.read("Rehttp-Date"))
                )
            )
        );
        return new RsFork(
            req,
            new FkTypes(
                "application/xml,text/xml",
                new RsPrettyXml(new RsWithType(raw, "text/xml"))
            ),
            new FkTypes(
                "*/*",
                new RsXslt(new RsWithType(raw, "text/html"))
            )
        );
    }

}
