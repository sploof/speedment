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

import com.speedment.config.Table;
import com.speedment.db.crud.Join;
import com.speedment.field.builders.ForeignKeyFinderBuilder;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNull;

/**
 * The default implementation of the {@link Join} interface.
 *
 * @author Emil
 */
public final class JoinImpl implements Join {
    
    private final String columnName;
    private final String otherColumnName;
    private final String otherTableName;

    /**
     * Instantiates the Join.
     * 
     * @param columnName       the name of the local column
     * @param otherColumnName  the name of the foreign column
     * @param otherTableName   the name of the foreign table
     */
    private JoinImpl(String columnName, String otherColumnName, String otherTableName) {
        this.columnName      = requireNonNull(columnName);
        this.otherColumnName = requireNonNull(otherColumnName);
        this.otherTableName  = requireNonNull(otherTableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName() {
        return columnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOtherColumnName() {
        return otherColumnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOtherTableName() {
        return otherTableName;
    }
    
    /**
     * Constructs a new {@link Join} based on a foreign key finder builder.
     *
     * @param <ENTITY>   the type of the entity
     * @param <V>        the type of the value
     * @param <FK>       the type of the foreign entity
     * @param finder     the predicate
     * @return           the constructed {@link Selector}
     * @see              BinaryPredicateBuilder
     */
    public static <ENTITY, V, FK> Join fromForeignKeyFinder(ForeignKeyFinderBuilder<ENTITY, V, FK> finder) {
        return new JoinImpl(
            finder.getField().getColumnName(),
            finder.getField().getForeignColumnName(),
            finder.getField().getForeignTableName()
        );
    }
}