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
package net.rehttp.base;

import java.net.MalformedURLException;
import java.net.URL;
import org.cactoos.list.ListOf;
import org.takes.Take;
import org.takes.rs.RsText;

/**
 * Base.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class FakeBase implements Base {

    /**
     * The take as a target.
     */
    private final Take take;

    /**
     * Ctor.
     */
    public FakeBase() {
        this(request -> new RsText("Everything is fine"));
    }

    /**
     * Ctor.
     * @param take The take as a target.
     */
    public FakeBase(final Take take) {
        this.take = take;
    }

    @Override
    public Take target(final URL url, final long time) {
        return this.take;
    }

    @Override
    public Iterable<Take> expired() throws MalformedURLException {
        return new ListOf<>(this.target(new URL("#"), 0L));
    }

    @Override
    public Status status(final URL url) {
        return new FakeStatus();
    }

}
