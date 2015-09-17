/**
 *
 * Copyright (c) 2006-2015, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.internal.core.db.crud;

import com.speedment.config.Column;
import com.speedment.config.Table;
import com.speedment.db.crud.Create;
import static java.util.Collections.unmodifiableMap;

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
    private final Map<String, Object> values;

    /**
     * CreateImpl should be constructed using the appropriate {@link Builder} class.
     *
     * @param table   the table to create the entity in
     * @param values  the values to use
     */
    private CreateImpl(Table table, Map<String, Object> values) {
        this.table  = table;
        this.values = unmodifiableMap(values);
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
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * Builder class for {@link CreateImpl}.
     */
    public static class Builder {

        private final Table table;
        private final Map<String, Object> values;

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
        public Builder with(String column, Object value) {
            values.put(
                requireNonNull(column),
                value
            );

            return this;
        }
        
        /**
         * Adds all the specified values mapped to the particular column name.
         * If the same column name already has a value, the old value will be
         * overwritten with the new one suggested.

         * @param values      values mapped to column names
         * @return            a reference to this builder
         */
        public CreateImpl.Builder with(Map<String, Object> values) {
            values.forEach(this::with);
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
