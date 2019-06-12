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
package net.rehttp;

import java.io.IOException;
import java.util.concurrent.Callable;
import net.rehttp.base.Base;
import org.takes.Request;
import org.takes.Take;
import org.takes.rq.RqFake;

/**
 * Retry them all.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Retry implements Callable<Void> {

    /**
     * The base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public Retry(final Base bse) {
        this.base = bse;
    }

    @Override
    public Void call() throws IOException {
        final Request req = new RqFake();
        for (final Take take : this.base.expired()) {
            take.act(req);
        }
        return null;
    }

}
