package com.speedment.internal.core.manager;

import com.speedment.Speedment;
import com.speedment.config.Column;
import com.speedment.config.PrimaryKeyColumn;
import com.speedment.config.Table;
import com.speedment.db.MetaResult;
import com.speedment.db.crud.Result;
import com.speedment.db.crud.Selector;
import com.speedment.exception.SpeedmentException;
import com.speedment.internal.core.db.crud.CreateImpl;
import com.speedment.internal.core.db.crud.DeleteImpl;
import com.speedment.internal.core.db.crud.SelectorImpl;
import com.speedment.internal.core.db.crud.UpdateImpl;
import com.speedment.internal.core.platform.component.CrudHandlerComponent;
import static java.util.Objects.requireNonNull;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Abstract base implementation of a Manager that translates all persist,
 * update and remove operations into their corresponding CRUD operations and
 * feeds them to the installed {@link CrudHandlerComponent}.
 * 
 * @author Emil Forslund
 * @param <ENTITY>  the type of the entity to manage
 */
public abstract class AbstractCrudManager<ENTITY> extends AbstractManager<ENTITY> {
    
    private final CrudHandlerComponent handler;
    private final Table table;
    private final Column primaryKeyColumn;
    
    protected AbstractCrudManager(Speedment speedment, Table table) {
        super(speedment);
        requireNonNull(table);
        
        this.handler          = speedment.get(CrudHandlerComponent.class);
        this.table            = table;
        this.primaryKeyColumn = findColumnOfPrimaryKey(table);
    }
    
    protected abstract ENTITY instantiate(Result result);
    
    @Override
    public Stream<ENTITY> stream() {
        // TODO initiate stream
    }

    @Override
    public ENTITY persist(ENTITY entity) throws SpeedmentException {
        return handler.create(
            new CreateImpl.Builder(table)
                .with(
                    primaryKeyColumn.getName(),
                    get(entity, primaryKeyColumn)
                ).build()
            , this::instantiate
        );
    }

    @Override
    public ENTITY update(ENTITY entity) throws SpeedmentException {
        final UpdateImpl.Builder update = new UpdateImpl.Builder(table);
        
        table.streamOf(Column.class).forEachOrdered(col ->
            update.with(col.getName(), get(entity, col))
        );
        
        return handler.update(
            update.where(selectorFor(entity)).build(), 
            this::instantiate
        );
    }

    @Override
    public ENTITY remove(ENTITY entity) throws SpeedmentException {
        handler.delete(
            new DeleteImpl.Builder(table)
                .where(selectorFor(entity))
                .build()
        );
        
        return entity;
    }

    @Override
    public ENTITY persist(ENTITY entity, Consumer<MetaResult<ENTITY>> consumer) throws SpeedmentException {
        throw new UnsupportedOperationException("Meta result consumers are not supported with crud operations.");
    }

    @Override
    public ENTITY update(ENTITY entity, Consumer<MetaResult<ENTITY>> consumer) throws SpeedmentException {
        throw new UnsupportedOperationException("Meta result consumers are not supported with crud operations.");
    }

    @Override
    public ENTITY remove(ENTITY entity, Consumer<MetaResult<ENTITY>> consumer) throws SpeedmentException {
        throw new UnsupportedOperationException("Meta result consumers are not supported with crud operations.");
    }

    private Selector selectorFor(ENTITY entity) {
        return SelectorImpl.standard(
            primaryKeyColumn.getName(), 
            get(entity, primaryKeyColumn)
        );
    }
    
    private static Column findColumnOfPrimaryKey(Table table) {
        return table.streamOf(PrimaryKeyColumn.class).findFirst()
            .orElseThrow(() -> new SpeedmentException(
                "Could not find any primary key in table '" + table.getName() + "'."
            )).getColumn();
    }
}