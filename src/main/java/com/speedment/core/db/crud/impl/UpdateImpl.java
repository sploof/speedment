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

    /**
     * UpdateImpl should be constructed using the appropriate {@link Builder} class.
     *
     * @param table      the table of the entity to update
     * @param selectors  the selectors used to determine which entities to update
     * @param values     the new values to use
     */
    private UpdateImpl(Table table, List<Selector> selectors, Map<Column, Object> values) {
        this.table     = table;
        this.selectors = selectors;
        this.values    = values;
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
     * Builder class for {@link UpdateImpl}.
     */
    public static class Builder {

        private final Table table;
        private final List<Selector> selectors;
        private final Map<Column, Object> values;

        /**
         * Constructs a builder for the specified {@link Table}.
         *
         * @param table  the table
         */
        public Builder(Table table) {
            this.table     = requireNonNull(table);
            this.selectors = new ArrayList<>();
            this.values    = new ConcurrentHashMap<>();
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
         * Builds the new {@link UpdateImpl} instance.
         *
         * @return  the new instance
         */
        public UpdateImpl build() {
            return new UpdateImpl(table, selectors, values);
        }
    }
}