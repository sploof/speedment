package com.speedment.core.db.crud.impl;

import com.speedment.core.config.model.Table;
import com.speedment.core.db.crud.Delete;
import com.speedment.core.db.crud.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The default implementation of the {@link Delete} operation.
 *
 * @author Emil Forslund
 */
public final class DeleteImpl implements Delete {

    private final Table table;
    private final List<Selector> selectors;

    /**
     * DeleteImpl should be constructed using the appropriate {@link Builder} class.
     *
     * @param table      the table to delete the entity in
     * @param selectors  the selectors used to determine which entities to delete
     */
    private DeleteImpl(Table table, List<Selector> selectors) {
        this.table     = table;
        this.selectors = selectors;
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
     * Builder class for {@link DeleteImpl}.
     */
    public static class Builder {

        private final Table table;
        private final List<Selector> selectors;

        /**
         * Constructs a builder for the specified {@link Table}.
         *
         * @param table  the table
         */
        public Builder(Table table) {
            this.table     = requireNonNull(table);
            this.selectors = new ArrayList<>();
        }

        /**
         * Appends an additional selector to this builder to determine which entities to delete.
         *
         * @param selector  the selector
         * @return          a reference to this builder
         */
        public Builder where(Selector selector) {
            selectors.add(requireNonNull(selector));
            return this;
        }

        /**
         * Builds the new {@link DeleteImpl} instance.
         *
         * @return  the new instance
         */
        public DeleteImpl build() {
            return new DeleteImpl(table, selectors);
        }
    }
}