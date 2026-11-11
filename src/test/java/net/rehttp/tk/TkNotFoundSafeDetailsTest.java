/*
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package net.rehttp.tk;

import java.net.HttpURLConnection;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.tk.TkText;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkNotFoundSafeDetails}.
 * @since 1.0
 */
final class TkNotFoundSafeDetailsTest {

    @Test
    void respondsWithNotFoundWhenRecordIsMissing() throws Exception {
        MatcherAssert.assertThat(
            "Missing record must give 404 status",
            new RsPrint(
                new TkNotFoundSafeDetails(
                    req -> {
                        throw new IllegalArgumentException("Not found");
                    }
                ).act(
                    new RqFake(
                        new ListOf<>("GET /d", "Host: www.rehttp.net"),
                        ""
                    )
                )
            ).printHead(),
            Matchers.startsWith(
                String.format("HTTP/1.1 %d", HttpURLConnection.HTTP_NOT_FOUND)
            )
        );
    }

    @Test
    void passesResponseThroughWhenRecordExists() throws Exception {
        MatcherAssert.assertThat(
            "Existing record must keep the origin response",
            new RsPrint(
                new TkNotFoundSafeDetails(new TkText("found")).act(
                    new RqFake(
                        new ListOf<>("GET /d", "Host: www.rehttp.net"),
                        ""
                    )
                )
            ).printBody(),
            Matchers.equalTo("found")
        );
    }
}
