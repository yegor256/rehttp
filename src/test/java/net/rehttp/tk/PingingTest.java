/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import java.net.HttpURLConnection;
import net.rehttp.base.FakeBase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkApp}.
 * @since 1.0
 */
final class PingingTest {

    @BeforeEach
    void resourcesAvailable() {
        Assumptions.assumeFalse(
            TkAppTest.class.getResourceAsStream("/xsl/index.xsl") == null
        );
    }

    @ParameterizedTest
    @CsvSource(
        {
            "/?x=y",
            "/robots.txt",
            "/xsl/layout.xsl",
            "/xsl/index.xsl",
            "/css/main.css",
            "/images/logo.svg"
        }
    )
    void rendersAllPossibleUrls(final String url) throws Exception {
        MatcherAssert.assertThat(
            url,
            new TkApp(new FakeBase()).act(new RqFake("INFO", url)),
            Matchers.not(
                new HmRsStatus(
                    HttpURLConnection.HTTP_NOT_FOUND
                )
            )
        );
    }
}
