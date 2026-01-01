/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import com.jcabi.matchers.XhtmlMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link RsPage}.
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class RsPageTest {

    @BeforeEach
    void resourcesAvailable() {
        Assumptions.assumeFalse(
            RsPageTest.class.getResourceAsStream("/xsl/index.xsl") == null
        );
    }

    @Test
    void rendersHomePageAsHtml() throws Exception {
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new RsPrint(
                    new RsPage(
                        "/xsl/index.xsl",
                        new RqFake()
                    )
                ).printBody()
            ),
            XhtmlMatchers.hasXPath("//xhtml:body")
        );
    }
}
