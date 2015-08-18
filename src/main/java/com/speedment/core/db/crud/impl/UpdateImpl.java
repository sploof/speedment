package com.speedment.core.db.crud.impl;

import com.speedment.core.config.model.Column;
import com.speedment.core.config.model.Table;
import com.speedment.core.db.crud.Selector;
import com.speedment.core.db.crud.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of the {@link Update} operation.
 *
 * @author Emil Forslund
 */
public final class UpdateImpl implements Update {

    private final Table table;
    private final List<Selector> selectors;
    private final Map<Column, Object> values;
    private final long limit;

    /**
     * UpdateImpl should be constructed using the appropriate {@link Builder} class.
     *
     * @param table      the table of the entity to update
     * @param selectors  the selectors used to determine which entities to update
     * @param values     the new values to use
     * @param limit      the maximum number of entities to update
     */
    private UpdateImpl(Table table, List<Selector> selectors, Map<Column, Object> values, long limit) {
        this.table     = table;
        this.selectors = selectors;
        this.values    = values;
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
    public Map<Column, Object> getValues() {
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLimit() {
        return limit;
    }

    /**
     * Builder class for {@link UpdateImpl}.
     */
    public static class Builder {

        private final Table table;
        private final List<Selector> selectors;
        private final Map<Column, Object> values;
        private long limit;

        /**
         * Constructs a builder for the specified {@link Table}.
         *
         * @param table  the table
         */
        public Builder(Table table) {
            this.table     = requireNonNull(table);
            this.selectors = new ArrayList<>();
            this.values    = new ConcurrentHashMap<>();
            this.limit     = Long.MAX_VALUE;
        }

        /**
         * Appends an additional selector to this builder to determine which entities to update.
         *
         * @param selector  the selector
         * @return          a reference to this builder
         */
        public Builder where(Selector selector) {
            selectors.add(requireNonNull(selector));
            return this;
        }

        /**
         * Sets the value for a particular {@link Column}.
         *
         * @param column  the column
         * @param value   the value
         * @return        a reference to this builder
         */
        public Builder with(Column column, Object value) {
            values.put(
                requireNonNull(column),
                value
            );

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
         * Builds the new {@link UpdateImpl} instance.
         *
         * @return  the new instance
         */
        public UpdateImpl build() {
            return new UpdateImpl(table, selectors, values, limit);
        }
    }
}