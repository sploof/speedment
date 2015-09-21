package com.speedment.internal.core.stream.builder.streamterminator.pluggable;

import com.speedment.config.Table;
import com.speedment.db.crud.CrudOperationBuilder;
import com.speedment.field.methods.Setter;
import com.speedment.internal.core.db.crud.CreateImpl;
import com.speedment.internal.core.db.crud.DeleteImpl;
import com.speedment.internal.core.db.crud.ReadImpl;
import com.speedment.internal.core.db.crud.UpdateImpl;
import com.speedment.internal.core.stream.builder.action.Action;
import com.speedment.internal.core.stream.builder.pipeline.Pipeline;
import com.speedment.internal.core.stream.builder.streamterminator.StreamTerminator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Emil
 */
public class ExperimentalStreamTerminator implements StreamTerminator {

    private <T extends Pipeline> T optimize(T initialPipeline, TerminatingAction terminator) {

        final CrudOperationBuilder<?> operation = determineCrudOperation(terminator, 
            () -> findTableForEntityType(terminator.getExpectedClass())
        );
        
        Action<?, ?> action = initialPipeline.getLast();
        Function<?, ?> inner = action.get();
        if (inner instanceof Setter) {
            final Setter getter = (Setter) inner;
            
        }

        return initialPipeline;
    }
    
    private CrudOperationBuilder<?> determineCrudOperation(TerminatingAction terminator, Supplier<Table> tableSupplier) {
        if (terminator instanceof CrudTerminatingAction) {
            final CrudTerminatingAction crudTerminator = (CrudTerminatingAction) terminator;
            switch (crudTerminator.getType()) {
                case CREATE : return new CreateImpl.Builder(crudTerminator.getTable());
                case UPDATE : return new UpdateImpl.Builder(crudTerminator.getTable());
                case DELETE : return new DeleteImpl.Builder(crudTerminator.getTable());
                case READ   : return new ReadImpl.Builder(crudTerminator.getTable());
                default: throw new UnsupportedOperationException("The CRUD type " + crudTerminator.getType().name() + " is not supported.");
            }
        }
        
        return new ReadImpl.Builder(tableSupplier.get());
    }
    
    private Table findTableForEntityType(Class<?> type) {
        return null;
    }
}
