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

import java.net.HttpURLConnection;
import net.rehttp.Dynamo;
import net.rehttp.base.DyBase;
import org.cactoos.iterable.ListOf;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.takes.Take;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

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
        final Take take = new TkApp(new DyBase(new Dynamo()));
        MatcherAssert.assertThat(
            new RsPrint(
                take.act(
                    new RqFake(
                        new ListOf<>(
                            "GET /r/http%3A%2F%2Fwww.yegor256.com%2F",
                            "Host: www.rehttp.net"
                        ),
                        ""
                    )
                )
            ),
            new HmRsStatus(HttpURLConnection.HTTP_OK)
        );
    }

}
