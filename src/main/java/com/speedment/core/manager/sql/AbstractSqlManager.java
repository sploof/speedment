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
package com.speedment.core.manager.sql;

import com.speedment.core.Buildable;
import com.speedment.core.config.model.Column;
import com.speedment.core.config.model.Dbms;
import com.speedment.core.config.model.PrimaryKeyColumn;
import com.speedment.core.db.DbmsHandler;
import com.speedment.core.db.crud.Result;
import com.speedment.core.db.crud.impl.*;
import com.speedment.core.manager.AbstractManager;
import com.speedment.core.manager.metaresult.MetaResult;
import com.speedment.core.platform.Platform;
import com.speedment.core.platform.component.DbmsHandlerComponent;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @param <PK> PrimaryKey type for this Manager
 * @param <ENTITY> Entity type for this Manager
 * @param <BUILDER> Builder type for this Manager
 *
 * @author pemi
 * @author Emil Forslund
 */
public abstract class AbstractSqlManager<PK, ENTITY, BUILDER extends Buildable<ENTITY>>
    extends AbstractManager<PK, ENTITY, BUILDER> implements SqlManager<PK, ENTITY, BUILDER> {

    private Function<Result, ENTITY> entityMapper;

    @Override
    public Function<Result, ENTITY> getEntityMapper() {
        return entityMapper;
    }

    @Override
    public void setEntityMapper(Function<Result, ENTITY> entityMapper) {
        this.entityMapper = entityMapper;
    }

    @Override
    public Stream<ENTITY> stream() {
        return dbmsHandler().executeRead(
            new ReadImpl.Builder(getTable())
                .build(),
            entityMapper
        );
    }

    @Override
    public Optional<ENTITY> persist(ENTITY entity) {
        return persist(entity, null);
    }

    @Override
    public Optional<ENTITY> persist(ENTITY entity, Consumer<MetaResult<ENTITY>> listener) {

        // TODO Notify listener

        final CreateImpl.Builder create = new CreateImpl.Builder(getTable());

        getTable().streamOf(Column.class).forEachOrdered(
            col -> create.with(col, get(entity, col))
        );

        return dbmsHandler().executeCreate(create.build(), entityMapper).findAny();
    }

    @Override
    public Optional<ENTITY> update(ENTITY entity) {
        return update(entity, null);
    }

    @Override
    public Optional<ENTITY> update(ENTITY entity, Consumer<MetaResult<ENTITY>> listener) {

        // TODO Notify listener

        final UpdateImpl.Builder update = new UpdateImpl.Builder(getTable());

        getTable().streamOf(Column.class).forEachOrdered(
            col -> update.with(col, get(entity, col))
        );

        getTable().streamOf(PrimaryKeyColumn.class)
            .map(PrimaryKeyColumn::getColumn)
            .forEachOrdered(col ->
                update.where(SelectorImpl.standard(col, get(entity, col)))
            );

        return dbmsHandler().executeUpdate(update.build(), entityMapper).findAny();
    }

    @Override
    public Optional<ENTITY> remove(ENTITY entity) {
        return remove(entity, null);
    }

    @Override
    public Optional<ENTITY> remove(ENTITY entity, Consumer<MetaResult<ENTITY>> listener) {

        // TODO Notify listener

        final DeleteImpl.Builder delete = new DeleteImpl.Builder(getTable());

        getTable().streamOf(PrimaryKeyColumn.class)
            .map(PrimaryKeyColumn::getColumn)
            .forEachOrdered(col ->
                    delete.where(SelectorImpl.standard(col, get(entity, col)))
            );

        return dbmsHandler().executeDelete(delete.build(), entityMapper).findAny();
    }

    protected Dbms getDbms() {
        return getTable().ancestor(Dbms.class).get();
    }

    protected DbmsHandler dbmsHandler() {
        return Platform.get().get(DbmsHandlerComponent.class).get(getDbms());
    }
}