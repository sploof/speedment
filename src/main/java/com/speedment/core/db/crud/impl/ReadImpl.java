package com.speedment.core.db.crud.impl;

import com.speedment.core.config.model.Table;
import com.speedment.core.db.crud.Read;
import com.speedment.core.db.crud.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of the {@link Read} operation.
 *
 * @author Emil Forslund
 */
public final class ReadImpl implements Read {

    private final Table table;
    private final List<Selector> selectors;
    private final long limit;

    /**
     * ReadImpl should be constructed using the appropriate {@link Builder} class.
     *
     * @param table      the table to read the entity from
     * @param selectors  the selectors used to determine which entities to read
     * @param limit      the maximum number of entities to read
     */
    private ReadImpl(Table table, List<Selector> selectors, long limit) {
        this.table     = table;
        this.selectors = selectors;
        this.limit     = limit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table getTable() {
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Selector> getSelectors() {
        return selectors.stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLimit() {
        return limit;
    }

    /**
     * Builder class for {@link ReadImpl}.
     */
    public static class Builder {

        private final Table table;
        private final List<Selector> selectors;
        private long limit;

        /**
         * Constructs a builder for the specified {@link Table}.
         *
         * @param table  the table
         */
        public Builder(Table table) {
            this.table     = requireNonNull(table);
            this.selectors = new ArrayList<>();
            this.limit     = Long.MAX_VALUE;
        }

        /**
         * Appends an additional selector to this builder to determine which entities to read.
         *
         * @param selector  the selector
         * @return          a reference to this builder
         */
        public Builder where(Selector selector) {
            selectors.add(requireNonNull(selector));
            return this;
        }

        /**
         * Limits the maximum number of entities that the operation may affect.
         *
         * @param limit  the new limit
         * @return       a reference to this builder
         */
        public Builder limit(long limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Builds the new {@link ReadImpl} instance.
         *
         * @return  the new instance
         */
        public ReadImpl build() {
            return new ReadImpl(table, selectors, limit);
        }
    }
}