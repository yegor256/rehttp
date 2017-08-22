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
package net.rehttp.base;

import java.io.IOException;
import java.net.URL;
import org.takes.Take;

/**
 * Base.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public interface Base {

    /**
     * Get target by URL and time.
     * @param url The URL
     * @param time The time
     * @return The request
     * @throws IOException If fails
     */
    Take target(URL url, long time) throws IOException;

    /**
     * Expired targets.
     * @return List of expired targets
     * @throws IOException If fails
     */
    Iterable<Take> expired() throws IOException;

    /**
     * History of the URL.
     * @param url The URL
     * @return The history
     * @throws IOException If fails
     */
    String history(URL url) throws IOException;

    /**
     * History of the URL and time.
     * @param url The URL
     * @param time The time
     * @return The history
     * @throws IOException If fails
     */
    String history(URL url, long time) throws IOException;

}
