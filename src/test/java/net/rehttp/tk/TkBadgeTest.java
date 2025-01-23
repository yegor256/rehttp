/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2025 Yegor Bugayenko
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

import com.jcabi.matchers.XhtmlMatchers;
import java.io.File;
import net.rehttp.base.FakeBase;
import org.cactoos.io.InputOf;
import org.cactoos.io.OutputTo;
import org.cactoos.io.TeeInput;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkBadge}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkBadgeTest {

    @Test
    void rendersSvgBadge() throws Exception {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new TextOf(
                    new TeeInput(
                        new InputOf(
                            new RsPrint(
                                new TkBadge(new FakeBase()).act(
                                    new RqFake(
                                        new ListOf<>(
                                            "GET /?u=http://www.yegor256.com",
                                            "Host: www.rehttp.net"
                                        ),
                                        ""
                                    )
                                )
                            ).printBody()
                        ),
                        new OutputTo(new File("target/rehttp.svg"))
                    )
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/svg:svg//svg:path")
        );
    }
}
