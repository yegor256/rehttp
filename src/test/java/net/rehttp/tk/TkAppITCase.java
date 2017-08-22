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

import com.jcabi.aspects.Tv;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;
import net.rehttp.Dynamo;
import net.rehttp.Retry;
import net.rehttp.base.Base;
import net.rehttp.base.DyBase;
import org.cactoos.func.RunnableOf;
import org.cactoos.iterable.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Take;
import org.takes.facets.forward.RsFailure;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rs.RsText;

/**
 * Integration case for {@link TkApp}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkAppITCase {

    /**
     * App can pass a request through.
     * @throws Exception If some problem inside
     */
    @Test
    public void passesRequestThrough() throws Exception {
        final AtomicInteger count = new AtomicInteger(3);
        final Take take = req -> {
            if (count.decrementAndGet() > 0) {
                throw new RsFailure("Error");
            }
            return new RsText("Success");
        };
        new FtRemote(take).exec(
            home -> {
                final Base base = new DyBase(new Dynamo(), 0L);
                new TkApp(base).act(
                    new RqFake(
                        new ListOf<>(
                            String.format(
                                "GET /r/%s",
                                URLEncoder.encode(home.toString(), "UTF-8")
                            ),
                            "Host: www.rehttp.net"
                        ),
                        ""
                    )
                );
                final Runnable retry = new RunnableOf<>(new Retry(base));
                for (int idx = 0; idx < Tv.TEN; ++idx) {
                    retry.run();
                }
            }
        );
        MatcherAssert.assertThat(count.get(), Matchers.equalTo(0));
    }

}
