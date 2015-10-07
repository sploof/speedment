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
package com.speedment.internal.core.field;

import com.speedment.field.methods.Getter;
import com.speedment.field.methods.Setter;
import com.speedment.field.ReferenceForeignKeyField;
import com.speedment.internal.core.field.builders.ForeignKeyFinderBuilderImpl;
import static java.util.Objects.requireNonNull;

/**
 * This class represents a Reference Field that is a Foreign key to another
 * table/column. A Reference Field is something that extends {@link Object}.
 *
 * @author pemi
 * @param <ENTITY> The entity type
 * @param <V> The value type
 * @param <FK> The foreign entity type
 */
public class ReferenceForeignKeyFieldImpl<ENTITY, V, FK>
    extends ReferenceFieldImpl<ENTITY, V>
    implements ReferenceForeignKeyField<ENTITY, V, FK> {

    private final ForeignKeyFinderBuilderImpl<ENTITY, V, FK> finder;
    private final String foreignTableName;
    private final String foreignColumnName;

    public ReferenceForeignKeyFieldImpl(String tableName, String columnName, String foreignTableName, String foreignColumnName, Getter<ENTITY, V> getter, Setter<ENTITY, V> setter, Getter<ENTITY, FK> finder) {
        super(tableName, columnName, getter, setter);
        this.foreignTableName  = requireNonNull(foreignTableName);
        this.foreignColumnName = requireNonNull(foreignColumnName);
        this.finder            = new ForeignKeyFinderBuilderImpl<>(this, finder);
    }

    @Override
    public FK findFrom(ENTITY entity) {
        requireNonNull(entity);
        return finder.apply(entity);
    }

    @Override
    public ForeignKeyFinderBuilderImpl<ENTITY, V, FK> finder() {
        return finder;
    }

    @Override
    public String getForeignTableName() {
        return foreignTableName;
    }

    @Override
    public String getForeignColumnName() {
        return foreignColumnName;
    }
}
