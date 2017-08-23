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

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.jcabi.aspects.Tv;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.cactoos.collection.Mapped;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Status in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class DyStatus implements Status {

    /**
     * The region to work with.
     */
    private final transient Region region;

    /**
     * The URL.
     */
    private final transient URL url;

    /**
     * Ctor.
     * @param reg Region
     * @param target Target URL
     */
    DyStatus(final Region reg, final URL target) {
        this.region = reg;
        this.url = target;
    }

    @Override
    public Collection<Iterable<Directive>> failures(final long after) {
        return new Mapped<>(
            this.table()
                .frame()
                .through(
                    new QueryValve()
                        .withIndexName("failures")
                        .withAttributesToGet(
                            "url", "time", "code", "attempts", "when", "ttl"
                        )
                        .withLimit(Tv.FIFTY)
                        .withConsistentRead(false)
                        .withScanIndexForward(false)
                )
                .where("failed_url", Conditions.equalTo(this.url))
                .where(
                    "time",
                    new Condition()
                        .withComparisonOperator(ComparisonOperator.LT)
                        .withAttributeValueList(
                            new AttributeValue().withN(
                                Long.toString(after)
                            )
                        )
                ),
            item -> DyStatus.xembly(item, false)
        );
    }

    @Override
    public Collection<Iterable<Directive>> history(final long after) {
        return new Mapped<>(
            this.table()
                .frame()
                .through(
                    new QueryValve()
                        .withAttributesToGet(
                            "url", "time", "code", "attempts", "when", "ttl"
                        )
                        .withLimit(Tv.FIFTY)
                        .withScanIndexForward(false)
                )
                .where("url", Conditions.equalTo(this.url))
                .where(
                    "time",
                    new Condition()
                        .withComparisonOperator(ComparisonOperator.LT)
                        .withAttributeValueList(
                            new AttributeValue().withN(
                                Long.toString(after)
                            )
                        )
                ),
            item -> DyStatus.xembly(item, false)
        );
    }

    @Override
    public Iterable<Directive> details(final long time) throws IOException {
        final Collection<Item> items = this.table()
            .frame()
            .through(
                new QueryValve()
                    .withSelect(Select.ALL_ATTRIBUTES)
                    .withLimit(1)
            )
            .where("url", Conditions.equalTo(this.url))
            .where("time", Conditions.equalTo(time));
        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                String.format(
                    "Request at %d for %s not found",
                    time, this.url
                )
            );
        }
        return DyStatus.xembly(items.iterator().next(), true);
    }

    /**
     * To Xembly.
     * @param item The item
     * @param full Full info?
     * @return Directives
     * @throws IOException If fails
     */
    private static Iterable<Directive> xembly(
        final Item item, final boolean full) throws IOException {
        final long time = Long.parseLong(item.get("time").getN());
        final long when = Long.parseLong(item.get("when").getN());
        final long ttl = Long.parseLong(item.get("ttl").getN())
            * TimeUnit.SECONDS.toMillis(1L);
        final Directives dirs = new Directives()
            .add("target")
            .add("url")
            .set(item.get("url").getS()).up()
            .add("time").set(time).up()
            .add("time_utc").set(DyStatus.utc(time)).up()
            .add("code").set(Integer.parseInt(item.get("code").getN())).up()
            .add("attempts")
            .set(Long.parseLong(item.get("attempts").getN())).up()
            .add("when").set(when).up()
            .add("when_utc").set(DyStatus.utc(when)).up()
            .add("ttl").set(ttl).up()
            .add("ttl_utc").set(DyStatus.utc(ttl)).up()
            .add("minutes_left")
            .set(
                (when - System.currentTimeMillis())
                    / TimeUnit.MINUTES.toMillis(1L)
            )
            .up()
            .add("ttl_minutes_left")
            .set(
                (ttl - System.currentTimeMillis())
                    / TimeUnit.MINUTES.toMillis(1L)
            )
            .up();
        if (full) {
            dirs.add("request")
                .set(Xembler.escape(item.get("request").getS())).up()
                .add("response")
                .set(Xembler.escape(item.get("response").getS())).up();
        }
        return dirs.up();
    }

    /**
     * Time to UTC.
     * @param time Time in the DB
     * @return UTC
     */
    private static String utc(final long time) {
        return ZonedDateTime.ofInstant(
            new Date(time).toInstant(),
            ZoneOffset.UTC
        ).format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * Table to work with.
     * @return Table
     */
    private Table table() {
        return this.region.table("targets");
    }

}
