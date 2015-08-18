package com.speedment.core.db.crud.impl;

import com.speedment.core.config.model.Column;
import com.speedment.core.config.model.Table;
import com.speedment.core.db.crud.Create;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of the {@link Create} operation.
 *
 * @author Emil Forslund
 */
public final class CreateImpl implements Create {

    private final Table table;
    private final Map<Column, Object> values;

    /**
     * CreateImpl should be constructed using the appropriate {@link Builder} class.
     *
     * @param table   the table to create the entity in
     * @param values  the values to use
     */
    private CreateImpl(Table table, Map<Column, Object> values) {
        this.table  = table;
        this.values = values;
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
    public Map<Column, Object> getValues() {
        return values;
    }

    /**
     * Builder class for {@link CreateImpl}.
     */
    public static class Builder {

        private final Table table;
        private final Map<Column, Object> values;

        /**
         * Constructs a builder for the specified {@link Table}.
         *
         * @param table  the table
         */
        public Builder(Table table) {
            this.table  = requireNonNull(table);
            this.values = new ConcurrentHashMap<>();
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
         * Builds the new {@link CreateImpl} instance.
         *
         * @return  the new instance
         */
        public CreateImpl build() {
            return new CreateImpl(table, values);
        }
    }
}
