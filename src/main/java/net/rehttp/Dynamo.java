/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
        final Credentials.Simple creds = new Credentials.Simple(
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
