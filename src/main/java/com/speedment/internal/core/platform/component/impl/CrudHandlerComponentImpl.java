package com.speedment.internal.core.platform.component.impl;

import com.speedment.db.crud.Create;
import com.speedment.db.crud.Delete;
import com.speedment.db.crud.Read;
import com.speedment.db.crud.Result;
import com.speedment.db.crud.Update;
import com.speedment.exception.SpeedmentException;
import com.speedment.internal.core.platform.component.CrudHandlerComponent;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author Emil Forslund
 */
public final class CrudHandlerComponentImpl implements CrudHandlerComponent {
    
    private CreateMethod creator;
    private UpdateMethod updater;
    private DeleteMethod deleter;
    private ReadMethod reader;

    @Override
    public CrudHandlerComponent setCreator(CreateMethod creator) {
        this.creator = creator;
        return this;
    }

    @Override
    public CrudHandlerComponent setUpdater(UpdateMethod updater) {
        this.updater = updater;
        return this;
    }

    @Override
    public CrudHandlerComponent setDeleter(DeleteMethod deleter) {
        this.deleter = deleter;
        return this;
    }

    @Override
    public CrudHandlerComponent setReader(ReadMethod reader) {
        this.reader = reader;
        return this;
    }

    @Override
    public <T> T create(Create operation, Function<Result, T> mapper) throws SpeedmentException {
        return creator.apply(operation, mapper);
    }

    @Override
    public <T> T update(Update operation, Function<Result, T> mapper) throws SpeedmentException {
        return updater.apply(operation, mapper);
    }

    @Override
    public <T> void delete(Delete operation) throws SpeedmentException {
        deleter.apply(operation);
    }

    @Override
    public <T> Stream<T> read(Read operation, Function<Result, T> mapper) throws SpeedmentException {
        return reader.apply(operation, mapper);
    }
}