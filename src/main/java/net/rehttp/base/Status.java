/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2024 Yegor Bugayenko
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

import java.io.IOException;
import java.util.Collection;
import org.xembly.Directive;

/**
 * Base.
 *
 * @since 1.0
 */
public interface Status {

    /**
     * Failures.
     * @param after After this date (epoch in milliseconds)
     * @return Map of recent failures
     * @throws IOException If fails
     */
    Collection<Iterable<Directive>> failures(long after) throws IOException;

    /**
     * Full history of the URL.
     * @param after After this date (epoch in milliseconds)
     * @return The history
     * @throws IOException If fails
     */
    Collection<Iterable<Directive>> history(long after) throws IOException;

    /**
     * Full details of one particular request.
     * @param time The time
     * @return The details in plain text
     * @throws IOException If fails
     */
    Iterable<Directive> details(long time) throws IOException;

}
