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
import com.speedment.internal.core.db.crud.SelectorImpl;
import com.speedment.internal.core.platform.component.CrudHandlerComponent;
import static java.util.Objects.requireNonNull;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * @author Emil
 * @param <ENTITY>
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
        
    }

    @Override
    public ENTITY persist(ENTITY entity) throws SpeedmentException {
        handler.create(
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
        
    }

    @Override
    public ENTITY remove(ENTITY entity) throws SpeedmentException {
        
    }

    @Override
    public ENTITY persist(ENTITY entity, Consumer<MetaResult<ENTITY>> consumer) throws SpeedmentException {
        
    }

    @Override
    public ENTITY update(ENTITY entity, Consumer<MetaResult<ENTITY>> consumer) throws SpeedmentException {
        
    }

    @Override
    public ENTITY remove(ENTITY entity, Consumer<MetaResult<ENTITY>> consumer) throws SpeedmentException {
        
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