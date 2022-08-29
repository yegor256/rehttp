/*
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

import java.util.Collection;
import java.util.Collections;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Base.
 *
 * @since 1.0
 */
public final class FakeStatus implements Status {

    @Override
    public Collection<Iterable<Directive>> failures(final long after) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Iterable<Directive>> history(final long after) {
        return Collections.emptyList();
    }

    @Override
    public Directives details(final long time) {
        return new Directives();
    }
}
