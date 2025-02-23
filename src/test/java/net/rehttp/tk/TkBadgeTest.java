/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
