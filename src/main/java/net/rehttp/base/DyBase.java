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

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.cactoos.iterable.Mapped;
import org.takes.Take;

/**
 * Base in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DyBase implements Base {

    /**
     * The region to work with.
     */
    private final transient Region region;

    /**
     * Delay in msecs between attempts.
     */
    private final transient long delay;

    /**
     * Ctor.
     * @param reg Region
     */
    public DyBase(final Region reg) {
        this(reg, TimeUnit.HOURS.toMillis(1L));
    }

    /**
     * Ctor.
     * @param reg Region
     * @param msec Delay in msecs between attempts
     */
    public DyBase(final Region reg, final long msec) {
        this.region = reg;
        this.delay = msec;
    }

    @Override
    public Take target(final URL url, final long time) throws IOException {
        final Collection<Item> items = this.table()
            .frame()
            .through(
                new QueryValve()
                    .withSelect(Select.ALL_ATTRIBUTES)
                    .withLimit(1)
            )
            .where("url", Conditions.equalTo(url))
            .where("time", Conditions.equalTo(time));
        final Item item;
        if (items.isEmpty()) {
            item = this.table().put(
                new Attributes()
                    .with("url", url)
                    .with("time", time)
                    .with("code", 0)
                    .with("attempts", 0)
                    .with("when", System.currentTimeMillis())
                    .with(
                        "ttl",
                        (System.currentTimeMillis()
                            + TimeUnit.DAYS.toMillis(1L))
                            / TimeUnit.SECONDS.toMillis(1L)
                    )
            );
        } else {
            item = items.iterator().next();
        }
        return new DyTake(item, this.delay);
    }

    @Override
    public Iterable<Take> expired() {
        return new Mapped<>(
            item -> new DyTake(item, this.delay),
            this.table()
                .frame()
                .through(
                    new QueryValve()
                        .withIndexName("expired")
                        .withConsistentRead(false)
                        .withSelect(Select.ALL_ATTRIBUTES)
                )
                .where("success", Conditions.equalTo(Boolean.toString(false)))
                .where(
                    "when",
                    new Condition()
                        .withComparisonOperator(ComparisonOperator.LT)
                        .withAttributeValueList(
                            new AttributeValue().withN(
                                Long.toString(System.currentTimeMillis())
                            )
                        )
                )
        );
    }

    @Override
    public Status status(final URL url) {
        return new DyStatus(this.region, url);
    }

    /**
     * Table to work with.
     * @return Table
     */
    private Table table() {
        return this.region.table("targets");
    }

}
