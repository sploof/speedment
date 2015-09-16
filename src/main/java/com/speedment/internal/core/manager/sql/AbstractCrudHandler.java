package com.speedment.internal.core.manager.sql;

import com.speedment.Speedment;
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
 * @author Emil
 */
public abstract class AbstractCrudHandler {
    
    private final Speedment speedment;

    public final void applyTo(Speedment speedment) {
        speedment.get(CrudHandlerComponent.class)
            .setCreator(this::create)
            .setUpdater(this::update)
            .setDeleter(this::delete)
            .setReader(this::read);
    }
    
    protected AbstractCrudHandler(Speedment speedment) {
        this.speedment = speedment;
    }

    protected abstract <T> T create(Create create, Function<Result, T> mapper) throws SpeedmentException;
    protected abstract <T> T update(Update update, Function<Result, T> mapper) throws SpeedmentException;
    protected abstract void delete(Delete delete) throws SpeedmentException;
    protected abstract <T> Stream<T> read(Read read, Function<Result, T> mapper) throws SpeedmentException;
    
    protected final Speedment speedment() {
        return speedment;
    }
}