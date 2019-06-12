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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import com.jcabi.dynamo.retry.ReRegion;
import com.jcabi.log.Logger;
import com.jcabi.manifests.Manifests;

/**
 * Command line entry.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class Dynamo implements Region {

    /**
     * Region.
     */
    private final transient Region region = Dynamo.connect();

    @Override
    public AmazonDynamoDB aws() {
        return this.region.aws();
    }

    @Override
    public Table table(final String name) {
        return this.region.table(name);
    }

    /**
     * Connect.
     * @return Region
     */
    private static Region connect() {
        final String key = Manifests.read("Rehttp-DynamoKey");
        final Credentials creds = new Credentials.Simple(
            key, Manifests.read("Rehttp-DynamoSecret")
        );
        final Region region;
        if (key.startsWith("AAAAA")) {
            final int port = Integer.parseInt(
                System.getProperty("dynamo.port")
            );
            region = new Region.Simple(new Credentials.Direct(creds, port));
            Logger.warn(Entrance.class, "Test DynamoDB at port #%d", port);
        } else {
            region = new Region.Prefixed(
                new ReRegion(new Region.Simple(creds)),
                "rehttp-"
            );
        }
        Logger.info(Entrance.class, "DynamoDB connected as %s", key);
        return region;
    }

}
